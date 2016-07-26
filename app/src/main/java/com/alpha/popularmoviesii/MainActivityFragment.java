package com.alpha.popularmoviesii;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.alpha.popularmoviesii.data.MovieContract;
import com.alpha.popularmoviesii.data.MovieDbHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private GridViewAdapter gridViewAdapter;
    private ArrayList<MovieItem> mGridData;
    private SharedPreferences movieSharedPreferences;
    private int sortSharedPref;
    private TextView textview;
    private ProgressDialog dialog;
    private AlertDialog.Builder alert;
    private MoviesCallBack mCallback;


    public interface MoviesCallBack {
        void onMovieSelect(ArrayList<MovieItem> mGridData, int position);
    }
    public MainActivityFragment() {
    }


    @Override
    public void onResume() {
        super.onResume();
        textview.setVisibility(View.GONE);
        updateView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);


        textview = (TextView) rootview.findViewById(R.id.main_activity_textview);

        GridView gridview = (GridView) rootview.findViewById(R.id.movies_gridview);
        movieSharedPreferences = PreferenceManager.getDefaultSharedPreferences(rootview.getContext());
        sortSharedPref = Integer.parseInt(movieSharedPreferences.getString("sorting_pref", "1"));

        mGridData = new ArrayList<>();
        gridViewAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item, mGridData);
        gridview.setAdapter(gridViewAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {


                try {
                    mCallback = (MoviesCallBack) getActivity();
                } catch (ClassCastException e) {
                    throw new ClassCastException(getActivity().toString() + " must implement MoviesCallBack interface");
                }

                if(mCallback != null) {
                    mCallback.onMovieSelect(mGridData,position);
                }
            }
        });

        return rootview;
    }




    private void updateView() {


        mGridData.clear();
        gridViewAdapter.clear();
        getMovieDetails();
    }

    private void getMovieDetails() {

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();

        mGridData.clear();
        sortSharedPref = Integer.parseInt(movieSharedPreferences.getString("sorting_pref", "1"));
        String url = Config.BaseURL;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieAPIService service = retrofit.create(MovieAPIService.class);
        Call<MoviePagination> call = null;
        if (sortSharedPref == 1) {
            call = service.getPopularMovies();
        } else if (sortSharedPref == 2) {
            call = service.getTopRatedMovies();
        } else if (sortSharedPref == 3) {
            getAllFavoriteMovies();


            gridViewAdapter.notifyDataSetChanged();
            dialog.dismiss();

        }

        alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("Please check your network connection and restart the app.")
                .setCancelable(false)
                .setTitle("Alert!")
                .setIcon(R.drawable.network)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface ddialog, int id) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        ddialog.dismiss();
                        getActivity().finish();
                    }
                });


        if (call != null)
            call.enqueue(new Callback<MoviePagination>() {

                @Override
                public void onResponse(Call<MoviePagination> call, Response<MoviePagination> response) {

                    try {
                        List<MovieItem> list = response.body().getResults();

                        for (int i = 0; i < list.size(); i++) {
                            MovieItem item = new MovieItem();
                            item.setPosterPath(Config.ImageURL + list.get(i).getPosterPath());
                            item.setBackdropPath(Config.ImageURL + list.get(i).getBackdropPath());
                            item.setOverview(list.get(i).getOverview());
                            item.setPopularity(list.get(i).getPopularity());
                            item.setReleaseDate(list.get(i).getReleaseDate());
                            item.setTitle(list.get(i).getTitle());
                            item.setVoteAverage(list.get(i).getVoteAverage());
                            item.setVoteCount(list.get(i).getVoteCount());
                            item.setAdult(list.get(i).getAdult());
                            item.setGenreIds(list.get(i).getGenreIds());
                            item.setOriginalLanguage(list.get(i).getOriginalLanguage());
                            item.setId(list.get(i).getId());
                            item.setVideo(list.get(i).getVideo());

                            mGridData.add(item);
                        }

                        if (mGridData.size() > 0) {
                            textview.setVisibility(View.GONE);
                        } else {
                            textview.setVisibility(View.VISIBLE);
                        }

                        gridViewAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(Call<MoviePagination> call, Throwable t) {

                    alert.show();

                }
            });
    }

    @Override
    public void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onDestroy();

    }

    private void getAllFavoriteMovies() {

        mGridData.addAll(getMyFavoriteMovies(getActivity().getContentResolver()));
        if (mGridData.size() > 0) {
            textview.setVisibility(View.GONE);
        } else {
            textview.setVisibility(View.VISIBLE);
        }
    }


    private ArrayList<MovieItem> getMyFavoriteMovies(ContentResolver contentResolver) {
        Uri uri = Uri.parse(MovieContract.BASE_CONTENT_URI + "/movies");
        Cursor cursor = contentResolver.query(uri, null, null, null, null, null);
        ArrayList<MovieItem> movies = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                MovieItem movie = new MovieItem();
                movie.setId(cursor.getInt(cursor.getColumnIndex(MovieDbHelper.movie_id)));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieDbHelper.movie_title)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieDbHelper.movie_overview)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(MovieDbHelper.movie_rating))));
                movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(MovieDbHelper.movie_image)));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieDbHelper.movie_poster)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieDbHelper.movie_date)));
                movie.setPopularity(Double.parseDouble(cursor.getString(cursor.getColumnIndex(MovieDbHelper.movie_popularity))));

                movies.add(movie);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return movies;
    }


}

