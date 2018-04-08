package com.talmacel.iosif.wearebase.utils;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class JsonLoader {
    public static JsonLoader instance;
    private List<JsonLoadRequest> jsonRequests;
    private List<JsonLoadTask> jsonTasks;

    public JsonLoader(){
        jsonRequests = new ArrayList<>();
        jsonTasks = new ArrayList<>();
    }

    public static void CreateStaticInstance(){
        if(instance == null)
            instance = new JsonLoader();
    }

    public <T> void downloadParsedJson(Context context, String link, Class<T> typeParameterClass, boolean saveOnDisk, OnLoadListener<T> listener){
        JsonLoadRequest request = new JsonLoadRequest(link, listener);
        jsonRequests.add(request);
        for (JsonLoadTask task : jsonTasks) {
            if(task.link.equals(link)){
                Log.e("sfdas", "sameeeeeeeeeeeeee");
                return;
            }
        }

        JsonLoadTask<T> task = new JsonLoadTask<>(context, link, typeParameterClass, saveOnDisk);
        jsonTasks.add(task);

        if(hasCache(context, link)){
            new JsonCacheAsyncTask<T>(this, task).execute();
        }
        new JsonWebAsyncTask<T>(this, task).execute();
    }


    private <T> void onWebDataLoaded(JsonLoadTask<T> task, String json){
        T data = jsonToClass(task.tClass, json);
        for (int i = jsonRequests.size() - 1; i >= 0; i--) {
            if(jsonRequests.get(i).link.equals(task.link) && jsonRequests.get(i).listener != null){
                jsonRequests.get(i).listener.OnLoad(data);
                jsonRequests.remove(i);
            }
        }
        jsonTasks.remove(task);
    }

    private <T> void onCacheDataLoaded(JsonLoadTask<T> task, String json) {
        T data = jsonToClass(task.tClass, json);
        for (int i = jsonRequests.size() - 1; i >= 0; i--) {
            if(jsonRequests.get(i).link.equals(task.link) && jsonRequests.get(i).listener != null){
                jsonRequests.get(i).listener.OnLoadCache(data);
            }
        }
    }

    private <T> void onFailedTask(JsonLoadTask<T> task) {
        for (int i = jsonRequests.size() - 1; i >= 0; i--) {
            if(jsonRequests.get(i).link.equals(task.link) && jsonRequests.get(i).listener != null){
                jsonRequests.remove(i);
            }
        }
        jsonTasks.remove(task);
    }

    String getJsonFromDiskCache(Context mContext, String link){
        String fileName = null;
        try {
            fileName = URLEncoder.encode(link, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return  null;
        }

        File file = new File( mContext.getCacheDir(), fileName);
        if(file.exists()){
            try {
                StringBuilder text = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();

                return text.toString();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  null;
    }

    void addJsonToDiskCache(Context mContext, String link, String json){
        String fileName = null;
        try {
            fileName = URLEncoder.encode(link, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return;
        }

        File file = new File(mContext.getCacheDir(), fileName);
        try {
            FileWriter writer = new FileWriter(file);
            writer.append(json);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasCache(Context mContext, String link){
        String fileName = null;
        try {
            fileName = URLEncoder.encode(link, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return  false;
        }

        File file = new File(mContext.getCacheDir(), fileName);
        return file.exists();
    }

    public <T> T jsonToClass(Class<T> tClass, String json){
        JsonParser parser = new JsonParser();
        JsonElement mJson =  parser.parse(json);
        return new Gson().fromJson(mJson, tClass);
    }



    static class JsonWebAsyncTask<T> extends AsyncTask<String, Void, String> {
        private JsonLoadTask<T> task;
        private WeakReference<JsonLoader> jsonLoader;

        JsonWebAsyncTask(JsonLoader loader, JsonLoadTask<T> task) {
            this.jsonLoader = new WeakReference<>(loader);
            this.task = task;
            Log.e("sfasf", "JsonWebAsyncTask");
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            try {
                URL uri = new URL(task.link);
                urlConnection = (HttpURLConnection) uri.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpsURLConnection.HTTP_OK) {
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                StringBuilder stringBuffer = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuffer.append(line);
                }
                String data = stringBuffer.toString();

                if(task.saveOnDisk && jsonLoader.get() != null && task.context.get() != null)
                    jsonLoader.get().addJsonToDiskCache(task.context.get(), task.link, data);

                return data;
            } catch (Exception e) {
                if (urlConnection != null)
                    urlConnection.disconnect();
                Log.e("JsonUtils", "Error downloading json from " + task.link);
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            if (jsonLoader.get() != null && json != null) {
                jsonLoader.get().onWebDataLoaded(task, json);
            }else if(jsonLoader.get() != null){
                jsonLoader.get().onFailedTask(task);
            }
        }
    }


    static class JsonCacheAsyncTask<T> extends AsyncTask<String, Void, String> {
        private JsonLoadTask<T> task;
        private WeakReference<JsonLoader> jsonLoader;

        JsonCacheAsyncTask(JsonLoader loader, JsonLoadTask<T> task) {
            this.jsonLoader = new WeakReference<>(loader);
            this.task = task;
        }

        @Override
        protected String doInBackground(String... params) {
            if(jsonLoader.get() != null && task.context.get() != null){
                return jsonLoader.get().getJsonFromDiskCache(task.context.get(), task.link);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String json) {
            if (jsonLoader.get() != null && json != null) {
                jsonLoader.get().onCacheDataLoaded(task, json);
            }
        }
    }

    public interface OnLoadListener<T>{
        void OnLoad(T parsedJson);
        void OnLoadCache(T parsedJson);
    }

    class JsonLoadTask <T> {
        WeakReference<Context> context;
        Class<T> tClass;
        String link;
        boolean saveOnDisk;

        JsonLoadTask(Context context, String link, Class<T> tClass, boolean saveOnDisk) {
            this.link = link;
            this.tClass = tClass;
            this.saveOnDisk = saveOnDisk;
            this.context = new WeakReference<>(context);
        }
    }

    class JsonLoadRequest {
        OnLoadListener listener;
        String link;

        JsonLoadRequest(String link, OnLoadListener listener) {
            this.link = link;
            this.listener = listener;
        }
    }
}