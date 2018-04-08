package com.talmacel.iosif.wearebase.activities;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.talmacel.iosif.wearebase.MyApplication;
import com.talmacel.iosif.wearebase.R;
import com.talmacel.iosif.wearebase.adapters.DataRecyclerAdapter;
import com.talmacel.iosif.wearebase.models.BaseData;
import com.talmacel.iosif.wearebase.utils.JsonLoader;
import com.talmacel.iosif.wearebase.utils.MarkerUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    DataRecyclerAdapter adapter;
    SupportMapFragment mapFragment;
    BaseData[] initialData;
    List<BaseData> sortedData;
    int markerIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        params.setBehavior(behavior);
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        markerIndex = 0;
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        adapter = new DataRecyclerAdapter(this, sortedData);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        final SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(sortedData != null)
                    OnQueryChanged(newText);
                return false;
            }
        });

        JsonLoader.instance.downloadParsedJson(this, MyApplication.URL, BaseData[].class, true, new JsonLoader.OnLoadListener<BaseData[]>() {
            @Override
            public void OnLoad(BaseData[] parsedJson) {
                OnDataLoaded(parsedJson);
            }

            @Override
            public void OnLoadCache(BaseData[] parsedJson) {
                OnDataLoaded(parsedJson);
            }
        });
    }


    void OnQueryChanged(String newText){
        sortedData.clear();
        String query = newText.toLowerCase();
        for (BaseData current : initialData) {
            if(current.name.toLowerCase().contains(query)){
                sortedData.add(current);
                continue;
            }

            String filtersString = current.brands_available.toString();
            if(filtersString != null && !filtersString.isEmpty()){
                String filters[] = filtersString.toLowerCase().substring(1,filtersString.length() - 2).split(",");
                for (String filter : filters) {
                    if(filter.contains(query)){
                        sortedData.add(current);
                        break;
                    }
                }
            }
        }
        adapter.setItems(sortedData);
        adapter.notifyDataSetChanged();
        markerIndex = 0;
        mapFragment.getMapAsync(MainActivity.this);
    }

    void OnDataLoaded(BaseData[] parsedJson){
        if(sortedData != null)
            return;

        sortedData = new ArrayList<>();
        sortedData.addAll(Arrays.asList(parsedJson));
        Collections.sort(sortedData, new Comparator<BaseData>() {
            @Override
            public int compare(BaseData o1, BaseData o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        initialData = new BaseData[sortedData.size()];
        sortedData.toArray(initialData);

        adapter.setItems(sortedData);
        adapter.notifyDataSetChanged();
        mapFragment.getMapAsync(MainActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(markerIndex == 0){
            googleMap.clear();
            googleMap.getUiSettings().setMapToolbarEnabled(false);
        }

        if(markerIndex >= sortedData.size())
            return;

        BaseData data = sortedData.get(markerIndex);
        Bitmap marker = MarkerUtils.drawMarker(markerIndex, MarkerUtils.markerColors[Math.max((int) data.rating - 1, 0)]);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(data.loc.latlng.lat, data.loc.latlng.lng))
                .icon(BitmapDescriptorFactory.fromBitmap(marker))
                .title(data.name)
                .flat(true));


        markerIndex++;
        mapFragment.getMapAsync(MainActivity.this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                File[] Files = getCacheDir().listFiles();
                if(Files != null) {
                    int j;
                    for(j = 0; j < Files.length; j++) {
                        System.out.println(Files[j].getAbsolutePath());
                        System.out.println(Files[j].delete());
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
