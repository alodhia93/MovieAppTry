package com.example.movieapptry.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapptry.DetailActivity;
import com.example.movieapptry.GlideApp;
import com.example.movieapptry.R;
import com.example.movieapptry.model.Movie;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {
    private final Context mContext;
    private final List<Movie> movieList;

    public MoviesAdapter(Context mContext, List<Movie> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesAdapter.MyViewHolder viewHolder, int i) {
        viewHolder.title.setText(movieList.get(i).getOriginalTitle());
        String vote = Double.toString(movieList.get(i).getVoteAverage());
        viewHolder.user_rating.setText(vote);
        GlideApp.with(mContext).load(movieList.get(i).getPosterPath()).into(viewHolder.thumbnail);
    }


    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, user_rating;
        public ImageView thumbnail;
        public MyViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.title);
            user_rating = view.findViewById(R.id.userrating);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    Movie clickedDataItem = movieList.get(position);
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra("original_title", movieList.get(position).getOriginalTitle());
                    intent.putExtra("poster_path", movieList.get(position).getPosterPath());
                    intent.putExtra("overview", movieList.get(position).getOverview());
                    intent.putExtra("vote_average", Double.toString(movieList.get(position).getVoteAverage()));
                    intent.putExtra("release_date", movieList.get(position).getReleaseDate());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    Toast.makeText(v.getContext(), "You Clicked "+ clickedDataItem.getOriginalTitle(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
