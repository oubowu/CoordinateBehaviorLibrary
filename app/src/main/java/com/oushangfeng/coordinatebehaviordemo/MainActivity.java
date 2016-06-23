package com.oushangfeng.coordinatebehaviordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView horizontalRecyclerView = (RecyclerView) findViewById(R.id.horizontal);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<String> strList = new ArrayList();
        for (int i = 0; i < 20; i++) {
            strList.add(i + "");
        }
        horizontalRecyclerView.setAdapter(new BaseRecyclerAdapter<String>(this, strList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_simple;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, String item) {
                holder.getTextView(R.id.text_view).setText(item);
            }
        });

        final RecyclerView verticalRecyclerView = (RecyclerView) findViewById(R.id.vertical);
        verticalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        verticalRecyclerView.setAdapter(new BaseRecyclerAdapter<String>(this, strList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_simple;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, String item) {
                holder.getTextView(R.id.text_view).setText(item);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
