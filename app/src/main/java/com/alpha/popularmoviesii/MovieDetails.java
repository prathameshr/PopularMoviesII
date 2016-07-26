package com.alpha.popularmoviesii;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Movie Detail Screen Activity
 */

public class MovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (savedInstanceState == null) {

            MovieDetailsFragment dFragment = new MovieDetailsFragment();
            Bundle extras = new Bundle();
            extras.putParcelableArrayList("gridData", getIntent().getExtras().getParcelableArrayList("gridData"));
            extras.putInt("position", getIntent().getExtras().getInt("position"));
            dFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.movie_details_fragment, dFragment).
                    commit();
        }
    }

}
