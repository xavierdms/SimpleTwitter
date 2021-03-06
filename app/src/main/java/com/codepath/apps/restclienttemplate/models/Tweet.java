package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import static com.codepath.apps.restclienttemplate.models.TimeFormatter.getTimeDifference;

@Parcel
public class Tweet {

    public String body;
    public long uid;
    public String createdAt;
    public User user;
    public String time;
    public String mediaUrl;

    // empty constructor needed by the Parceler library
    public Tweet() {
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        String formattedTime = getTimeDifference(tweet.createdAt);
        tweet.time = formattedTime;
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));



        return tweet;
    }


}
