package com.example.movieapptry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.movieapptry.adapter.MoviesAdapter;
import com.example.movieapptry.api.Client;
import com.example.movieapptry.api.Service;
import com.example.movieapptry.model.Movie;
import com.example.movieapptry.model.MovieResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeContainer;
    public static final String API_KEY = "0dde3e9896a8c299d142e214fcb636f8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        swipeContainer = findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(() -> {
            initViews();
            Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_SHORT).show();
        });
    }

    public Activity getActivity() {
        Context context = this;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Movies.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        recyclerView = findViewById(R.id.recycler_view);
        List<Movie> movieList = new ArrayList<>();
        MoviesAdapter moviesAdapter = new MoviesAdapter(this, movieList);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(moviesAdapter);
        moviesAdapter.notifyDataSetChanged();
        loadJSON();
    }

    private void loadJSON() {
        try {
            if (API_KEY.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain your API Key", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            Client client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<MovieResponse> call = apiService.getPopularMovies(API_KEY);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.menu_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}