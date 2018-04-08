package com.talmacel.iosif.wearebase.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.talmacel.iosif.wearebase.R;
import com.talmacel.iosif.wearebase.models.BaseData;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InfoActivity extends AppCompatActivity {
    BaseData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = (BaseData) getIntent().getSerializableExtra("info");

        if(data == null){
            finish();
            return;
        }

        ((TextView)findViewById(R.id.name_value)).setText(data.name);
        ((TextView)findViewById(R.id.description_value)).setText(data.description);
        Date date = new Date(data.date * 1000);
        SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        ((TextView)findViewById(R.id.date_value)).setText(dateformat.format(date));
        ((TextView)findViewById(R.id.state_value)).setText(data.status);
        ((TextView)findViewById(R.id.type_value)).setText(data.type);
        ((TextView)findViewById(R.id.brands_value)).setText(data.brands_available.toString().replace("\"", "" ).replace("[", "").replace("]", ""));
        ((TextView)findViewById(R.id.organization_value)).setText(data.loc.organization);
        ((TextView)findViewById(R.id.country_value)).setText(data.loc.country);
        ((TextView)findViewById(R.id.city_value)).setText(data.loc.city);
        ((TextView)findViewById(R.id.state_value)).setText(data.loc.state);
        ((TextView)findViewById(R.id.street_value)).setText(data.loc.street_adress);
        ((TextView)findViewById(R.id.zip_value)).setText(data.loc.zip);

        boolean shouldShowComments = data.comments != null && data.comments.length > 0;
        findViewById(R.id.comments_container).setVisibility(shouldShowComments ? View.VISIBLE : View.GONE);
        findViewById(R.id.comments_info).setVisibility(shouldShowComments ? View.VISIBLE : View.GONE);

        if(shouldShowComments){
            ViewStub comment1 = findViewById(R.id.comment_1);
            comment1.setOnInflateListener(new ViewStub.OnInflateListener() {
                @Override
                public void onInflate(ViewStub stub, View inflated) {
                    ((TextView)inflated.findViewById(R.id.comment_name)).setText(data.comments[0].author);
                    ((TextView)inflated.findViewById(R.id.comment_text)).setText(data.comments[0].content);

                }
            });
            comment1.inflate();

            if(data.comments.length > 1){
                ViewStub comment2 = findViewById(R.id.comment_2);
                comment2.setOnInflateListener(new ViewStub.OnInflateListener() {
                    @Override
                    public void onInflate(ViewStub stub, View inflated) {
                        ((TextView)inflated.findViewById(R.id.comment_name)).setText(data.comments[1].author);
                        ((TextView)inflated.findViewById(R.id.comment_text)).setText(data.comments[1].content);
                    }
                });
                comment2.inflate();
            }

            if(data.comments.length > 2){
                ViewStub comment3 = findViewById(R.id.comment_3);
                comment3.setOnInflateListener(new ViewStub.OnInflateListener() {
                    @Override
                    public void onInflate(ViewStub stub, View inflated) {
                        ((TextView)inflated.findViewById(R.id.comment_name)).setText(data.comments[2].author);
                        ((TextView)inflated.findViewById(R.id.comment_text)).setText(data.comments[2].content);
                    }
                });
                comment3.inflate();
            }

            findViewById(R.id.more_comments).setVisibility(data.comments.length > 3 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
