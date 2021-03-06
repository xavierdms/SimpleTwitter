package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {


    private final int REQUEST_CODE = 21;

    private TwitterClient client;
    RecyclerView rvTweets;
    private TweetsAdapter adapter;
    private List<Tweet> tweets;
    private ImageButton ibNight;

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        swipeContainer = findViewById(R.id.swipeContainer);

        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorNightBar,
                R.color.colorAccent,
                R.color.colorNightBar);

        // find the recycler view
        rvTweets = findViewById(R.id.rvTweets);

        // initialize list of tweets and adapter from the data source
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        // recycler view setup: layout manager and setting adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        ibNight = findViewById(R.id.ibNight);


        populateHomeTimeline();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("TwitterClient", "Content is being refreshed");
                populateHomeTimeline();
            }
        });

        ibNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TimelineActivity.this, TimelineActivityNight.class
                ));
            }
        });

        FloatingActionButton fabCompose = findViewById(R.id.fabCompose);
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose)
        {
            // Tapped on the compose icon

            // Navigate to a new activity
            Intent i = new Intent(this, ComposeActivity.class);
            this.startActivityForResult(i, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // REQUEST_CODE is defined above
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Pull info out of the data Intent
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // Update the recycler view with this tweet
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
        }
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Log.d("TwitterClient", response.toString());
                // iterate through the list of tweets

                List<Tweet> tweetsToAdd = new ArrayList<>();
                for (int i = 0; i < response.length(); i++)
                {
                    try {
                        // convert each json object into a tweet object
                        JSONObject jsonTweetObject = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(jsonTweetObject);
                        // add the tweet into the data source
                        tweetsToAdd.add(tweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                adapter.clear();
                adapter.addTweets(tweetsToAdd);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("TwitterClient", responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("TwitterClient", errorResponse.toString());
            }
        });
    }




}
