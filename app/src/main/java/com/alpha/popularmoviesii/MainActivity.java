package com.alpha.popularmoviesii;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * MainActivity class
 */

public class MainActivity extends AppCompatActivity implements MainActivityFragment.MoviesCallBack{

    boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            if (findViewById(R.id.movie_detail_container) != null) {
                    isTablet = true;
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.movie_detail_container, new MovieDetailsFragment())
                            .commit();
                }
            }
            else
            {
                isTablet = false;
            }

    }


    @Override
    public void onMovieSelect(ArrayList<MovieItem> mGridData, int position) {
        if(isTablet) {
            MovieDetailsFragment dFragment = new MovieDetailsFragment();
            Bundle extras = new Bundle();
            extras.putParcelableArrayList("gridData", mGridData);
            extras.putInt("position", position);
            dFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().
            replace(R.id.movie_detail_container, dFragment).
            commit();
        } else {
            Intent intent = new Intent(this, MovieDetails.class);
            Bundle extras = new Bundle();
            extras.putParcelableArrayList("gridData", mGridData);
            extras.putInt("position", position);
            intent.putExtras(extras);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}