package com.example.exampractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private RecyclerView testView;
    private Toolbar toolbar;
    private TestAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        getSupportActionBar().setTitle(DbQuery.g_catlist.get(DbQuery.g_selected_cat_index).getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        testView = findViewById(R.id.test_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        testView.setLayoutManager(layoutManager);

        //loadTestData();

        DbQuery.loadTestData(new MyCompleteListener() {
            @Override
            public void onSuccess() {

                adapter = new TestAdapter(DbQuery.g_testList);
                testView.setAdapter(adapter);

            }

            @Override
            public void onFailure() {

                Toast.makeText(TestActivity.this, "Something went wrong please try again later ",
                        Toast.LENGTH_SHORT).show();

            }
        });



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            TestActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
