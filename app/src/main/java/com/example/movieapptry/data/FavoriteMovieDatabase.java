package com.example.movieapptry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.movieapptry.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavoriteMovieDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favorite.db";
    private static final int DATABASE_VERSION = 1;
    public static final String LOGTAG = "FAVORITE";
    SQLiteOpenHelper dbhandler;
    SQLiteDatabase db;
    public FavoriteMovieDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void open(){
        Log.i(LOGTAG, "Database Opened");
        db = dbhandler.getWritableDatabase();
    }
    public void close(){
        Log.i(LOGTAG, "Database Closed");
        dbhandler.close();
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteMovie.FavoriteEntry.TABLE_NAME + " ("+
                FavoriteMovie.FavoriteEntry._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                FavoriteMovie.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER, " +
                FavoriteMovie.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteMovie.FavoriteEntry.COLUMN_USERRATING + " REAL NOT NULL, " +
                FavoriteMovie.FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteMovie.FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL" + ");";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMovie.FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addFavorite(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteMovie.FavoriteEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(FavoriteMovie.FavoriteEntry.COLUMN_TITLE, movie.getOriginalTitle());
        contentValues.put(FavoriteMovie.FavoriteEntry.COLUMN_USERRATING, movie.getVoteAverage());
        contentValues.put(FavoriteMovie.FavoriteEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(FavoriteMovie.FavoriteEntry.COLUMN_OVERVIEW, movie.getOverview());
        db.insert(FavoriteMovie.FavoriteEntry.TABLE_NAME, null, contentValues);
        db.close();
    }

    public void deleteFavorite(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FavoriteMovie.FavoriteEntry.TABLE_NAME, FavoriteMovie.FavoriteEntry.COLUMN_MOVIE_ID+ "=" + id, null);
    }

    public List<Movie> getAllFavorites(){
        String[] columns = {FavoriteMovie.FavoriteEntry._ID,
                FavoriteMovie.FavoriteEntry.COLUMN_MOVIE_ID,
                FavoriteMovie.FavoriteEntry.COLUMN_TITLE,
                FavoriteMovie.FavoriteEntry.COLUMN_USERRATING,
                FavoriteMovie.FavoriteEntry.COLUMN_POSTER_PATH,
                FavoriteMovie.FavoriteEntry.COLUMN_OVERVIEW};
        String sortOrder = FavoriteMovie.FavoriteEntry._ID + " ASC";
        List<Movie> favoriteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FavoriteMovie.FavoriteEntry.TABLE_NAME, columns,
                null,
                null,
                null,
                null,
                sortOrder);
        if (cursor.moveToFirst()){
            do {
                Movie movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavoriteMovie.FavoriteEntry.COLUMN_MOVIE_ID))));
                movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(FavoriteMovie.FavoriteEntry.COLUMN_TITLE)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(FavoriteMovie.FavoriteEntry.COLUMN_USERRATING))));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoriteMovie.FavoriteEntry.COLUMN_POSTER_PATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(FavoriteMovie.FavoriteEntry.COLUMN_OVERVIEW)));
                favoriteList.add(movie);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteList;
    }
}
