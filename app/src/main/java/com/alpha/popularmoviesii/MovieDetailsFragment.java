package com.alpha.popularmoviesii;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.popularmoviesii.data.MovieContract;
import com.alpha.popularmoviesii.data.MovieDbHelper;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
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
public class MovieDetailsFragment extends Fragment {

    private boolean flag = false;
    private long movie_id;
    private List<MovieReviewItem> review_list = new ArrayList<>();
    private List<MovieTrailerItem> trailer_list = new ArrayList<>();
    private View view;
    private LinearLayout mLinearListViewReview;
    private LinearLayout mLinearListViewTrailer;
    private android.support.v7.widget.CardView mTrailerCardView;
    private android.support.v7.widget.CardView mReviewCardView;
    private ArrayList<MovieItem> arrayList;
    private ImageView imdb_image;

    private LinearLayout linear_movie_image;
    private android.support.v7.widget.CardView movie_details_card;
    private android.support.v7.widget.CardView movie_overview_card;

    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_movie_details, container, false);

        arrayList = new ArrayList<>();
        int position = 0;
        DecimalFormat df = new DecimalFormat("##.#");
        Bundle extras = this.getArguments();
        if (extras != null) {
            position = extras.getInt("position");
            arrayList = extras.getParcelableArrayList("gridData");
        } else {
            arrayList = null;
        }

        ImageView movie_image = (ImageView) rootview.findViewById(R.id.movie_image);

        ImageView movie_poster = (ImageView) rootview.findViewById(R.id.movie_poster);
        TextView movie_title = (TextView) rootview.findViewById(R.id.movie_title);
        TextView movie_release_date = (TextView) rootview.findViewById(R.id.movie_release_date);
        TextView movie_overview = (TextView) rootview.findViewById(R.id.movie_overview);
        TextView movie_votes = (TextView) rootview.findViewById(R.id.movie_votes);
        TextView movie_popularity = (TextView) rootview.findViewById(R.id.movie_popularity);


        if (arrayList != null && arrayList.get(position) != null) {
            movie_id = arrayList.get(position).getId();
            Picasso.with(getActivity()).load(arrayList.get(position).getBackdropPath()).into(movie_image);
            Picasso.with(getActivity()).load(arrayList.get(position).getPosterPath()).into(movie_poster);
            movie_poster.setContentDescription(arrayList.get(position).getTitle());
            movie_image.setContentDescription(arrayList.get(position).getTitle());
            movie_title.setText(arrayList.get(position).getTitle());
            movie_release_date.setText(arrayList.get(position).getReleaseDate());
            movie_overview.setText(arrayList.get(position).getOverview());
            movie_votes.setText(df.format(arrayList.get(position).getVoteAverage()) + "/10");
            movie_popularity.setText(df.format(arrayList.get(position).getPopularity()));
        }

        view = rootview;
        return rootview;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imdb_image = (ImageView) view.findViewById(R.id.imdb_image);

        linear_movie_image = (LinearLayout) view.findViewById(R.id.linear_movie_image);
        movie_details_card = (android.support.v7.widget.CardView) view.findViewById(R.id.movie_details_card);
        movie_overview_card = (android.support.v7.widget.CardView) view.findViewById(R.id.movie_overview_card);

        mReviewCardView = (android.support.v7.widget.CardView) view.findViewById(R.id.movie_reviews_card);
        mTrailerCardView = (android.support.v7.widget.CardView) view.findViewById(R.id.movie_trailer_card);

        mLinearListViewReview = (LinearLayout) view.findViewById(R.id.linear_listview_review);
        mLinearListViewTrailer = (LinearLayout) view.findViewById(R.id.linear_listview_trailer);

        if (arrayList != null) {
            StringBuilder url = new StringBuilder(Config.BaseURL).append(movie_id).append("/");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url.toString())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MovieAPIService service = retrofit.create(MovieAPIService.class);
            Call<MovieReviewPagination> call = service.getMovieReviews();

            if (call != null)
                call.enqueue(new Callback<MovieReviewPagination>() {

                    @Override
                    public void onResponse(Call<MovieReviewPagination> call, Response<MovieReviewPagination> response) {


                        try {
                            review_list = response.body().getResults();

                            for (int i = 0; i < review_list.size(); i++) {

                                mReviewCardView.setVisibility(View.VISIBLE);
                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View mLinearView = inflater.inflate(R.layout.review_row, null);

                                TextView review_author = (TextView) mLinearView.findViewById(R.id.review_author);
                                TextView review_text = (TextView) mLinearView.findViewById(R.id.review_text);

                                review_author.setText(review_list.get(i).getAuthor());
                                review_text.setText(review_list.get(i).getContent());


                                mLinearListViewReview.addView(mLinearView);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieReviewPagination> call, Throwable t) {

//                    Toast.makeText(getActivity(), "Webservice failed!!!", Toast.LENGTH_LONG).show();
                    }
                });

            url = new StringBuilder(Config.BaseURL).append(movie_id).append("/");
            retrofit = new Retrofit.Builder()
                    .baseUrl(url.toString())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(MovieAPIService.class);
            Call<MovieTrailerPagination> call1 = service.getMovieTrailers();

            if (call1 != null)
                call1.enqueue(new Callback<MovieTrailerPagination>() {

                    @Override
                    public void onResponse(Call<MovieTrailerPagination> call, Response<MovieTrailerPagination> response) {


                        try {
                            trailer_list = response.body().getResults();

                            for (int i = 0; i < trailer_list.size(); i++) {

                                mTrailerCardView.setVisibility(View.VISIBLE);

                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View mLinearView = inflater.inflate(R.layout.trailer_row, null);

                                ImageView trailer_image = (ImageView) mLinearView.findViewById(R.id.trailer_image);
                                TextView trailer_text = (TextView) mLinearView.findViewById(R.id.trailer_text);

                                final String key = trailer_list.get(i).getKey();
                                String thumbnail = "http://img.youtube.com/vi/" + trailer_list.get(i).getKey() + "/0.jpg";

                                Glide.with(getActivity()).load(thumbnail).into(trailer_image);


                                trailer_text.setText(trailer_list.get(i).getName());


                                mLinearListViewTrailer.addView(mLinearView);


                                mLinearListViewTrailer.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + key));
                                        startActivity(intent);

                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieTrailerPagination> call, Throwable t) {

//                    Toast.makeText(getActivity(), "Webservice failed!!!", Toast.LENGTH_LONG).show();

                    }
                });


            final ImageView fav_image = (ImageView) view.findViewById(R.id.fav_star);

            flag = checkFavorite(movie_id);
            if (!flag) {
                fav_image.setImageResource(R.drawable.star);
            } else {
                fav_image.setImageResource(R.drawable.star_marked);
            }

            fav_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!flag) {

                        ArrayList<MovieItem> arrayList = new ArrayList<>();
                        Bundle extras = getArguments();
                        int position = extras.getInt("position");
                        arrayList = extras.getParcelableArrayList("gridData");
                        ContentValues values = new ContentValues();
                        if (arrayList != null && arrayList.get(position) != null) {
                            movie_id = arrayList.get(position).getId();
                            values.put(MovieDbHelper.movie_id, movie_id);
                            values.put(MovieDbHelper.movie_image, arrayList.get(position).getBackdropPath());
                            values.put(MovieDbHelper.movie_poster, arrayList.get(position).getPosterPath());
                            values.put(MovieDbHelper.movie_title, arrayList.get(position).getTitle());
                            values.put(MovieDbHelper.movie_overview, arrayList.get(position).getOverview());
                            values.put(MovieDbHelper.movie_rating, arrayList.get(position).getVoteAverage());
                            values.put(MovieDbHelper.movie_popularity, arrayList.get(position).getPopularity());
                            values.put(MovieDbHelper.movie_date, arrayList.get(position).getReleaseDate());

                        }
                        getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                                values);
                        fav_image.setImageResource(R.drawable.star_marked);
                        flag = true;
                        Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_SHORT).show();
                    } else {

                        getActivity().getContentResolver().delete(
                                MovieContract.MovieEntry.CONTENT_URI,
                                MovieDbHelper.movie_id + " = ?",
                                new String[]{Integer.toString((int) movie_id)}
                        );
                        fav_image.setImageResource(R.drawable.star);
                        flag = false;
                        Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            imdb_image.setVisibility(View.VISIBLE);
            mTrailerCardView.setVisibility(View.GONE);
            mReviewCardView.setVisibility(View.GONE);
            mLinearListViewReview.setVisibility(View.GONE);
            mLinearListViewTrailer.setVisibility(View.GONE);
            linear_movie_image.setVisibility(View.GONE);
            movie_details_card.setVisibility(View.GONE);
            movie_overview_card.setVisibility(View.GONE);
        }
    }

    private boolean checkFavorite(long movie_id) {

        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieDbHelper.movie_id + " = ?",
                new String[]{Integer.toString((int) movie_id)},
                null
        );
        int numRows = cursor.getCount();

        cursor.close();
        return (numRows == 1);
    }

}
