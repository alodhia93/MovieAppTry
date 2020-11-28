package com.example.movieapptry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapptry.data.FavoriteMovieDatabase;
import com.example.movieapptry.model.Movie;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

public class DetailActivity extends AppCompatActivity {
    TextView movieName, overview, userRating, releaseDate;
    ImageView imageView;
    FavoriteMovieDatabase favoriteMovieDatabase;
    Movie favorite;
    final AppCompatActivity appCompatActivity = DetailActivity.this;
    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initCollapsingToolbar();
        imageView = findViewById(R.id.movie_thumbnail);
        movieName = findViewById(R.id.movie_title);
        overview = findViewById(R.id.overview_synopsis);
        userRating = findViewById(R.id.user_rating);
        releaseDate = findViewById(R.id.release);

        Intent intentStarted =getIntent();
        if (intentStarted.hasExtra("original_title")){
            String thumbnail = getIntent().getExtras().getString("poster_path");
            String movieTitle = getIntent().getExtras().getString("original_title");
            String stringOverview = getIntent().getExtras().getString("overview");
            String rating = getIntent().getExtras().getString("vote_average");
            String dateRelease = getIntent().getExtras().getString("release_date");
            GlideApp.with(this).load(thumbnail).into(imageView);
            movieName.setText(movieTitle);
            overview.setText(stringOverview);
            userRating.setText(rating);
            releaseDate.setText(dateRelease);
        }else{
            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show();
        }
        MaterialFavoriteButton materialFavoriteButtonNice = (MaterialFavoriteButton) findViewById(R.id.favorite_button);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        materialFavoriteButtonNice.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener(){
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite){
                        if (favorite){
                            SharedPreferences.Editor editor = getSharedPreferences("com.example.movieapptry.DetailActivity", MODE_PRIVATE).edit();
                            editor.putBoolean("Favorite Added", true);
                            editor.commit();
                            saveFavorite();
                            Snackbar.make(buttonView, "Added to Favorite", Snackbar.LENGTH_SHORT).show();
                        }else{
                            int movie_id = getIntent().getExtras().getInt("id");
                            favoriteMovieDatabase = new FavoriteMovieDatabase(DetailActivity.this);
                            favoriteMovieDatabase.deleteFavorite(movie_id);
                            SharedPreferences.Editor editor = getSharedPreferences("com.example.movieapptry.DetailActivity", MODE_PRIVATE).edit();
                            editor.putBoolean("Favorite Removed", true);
                            editor.commit();
                            Snackbar.make(buttonView, "Removed from Favorite", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0){
                    collapsingToolbarLayout.setTitle(getString(R.string.movie_details));
                    isShow = true;
                }else if(isShow){
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    public void saveFavorite(){
        favoriteMovieDatabase = new FavoriteMovieDatabase(appCompatActivity);
        favorite = new Movie();
        int movie_id = getIntent().getExtras().getInt("id");
        String rate = getIntent().getExtras().getString("vote_average");
        String poster = getIntent().getExtras().getString("poster_path");
        favorite.setId(movie_id);
        favorite.setOriginalTitle(movieName.getText().toString().trim());
        favorite.setPosterPath(poster);
        favorite.setVoteAverage(Double.parseDouble(rate));
        favorite.setOverview(overview.getText().toString().trim());
        favoriteMovieDatabase.addFavorite(favorite);
    }
}
