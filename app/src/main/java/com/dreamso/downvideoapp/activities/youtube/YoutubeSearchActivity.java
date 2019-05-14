package com.dreamso.downvideoapp.activities.youtube;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamso.downvideoapp.R;
import com.dreamso.downvideoapp.fragments.adapters.VideoPostAdapter;
import com.dreamso.downvideoapp.fragments.models.YoutubeDataModel;
import com.dreamso.downvideoapp.fragments.tabFragments.TrendingTabFragment;
import com.dreamso.downvideoapp.interfaces.OnItemClickListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class YoutubeSearchActivity extends AppCompatActivity {

    private static String GOOGLE_YOUTUBE_API_KEY = "AIzaSyDY5KEE5eQbAxowrjoQY7L-N3wql81I45k";//here you should use your api key for testing purpose you can use this api also
    private static String SEARCH_KEYWORD = ""; //here you should use your channel id for testing purpose you can use this api also

    private static String CHANNLE_GET_URL =  "";

    private RecyclerView mList_videos = null;
    private VideoPostAdapter adapter = null;
    private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_search);
        handleIntent(getIntent());

        mList_videos = (RecyclerView) findViewById(R.id.mList_videos);
        initList(mListData);
        new YoutubeSearchActivity.RequestYoutubeAPI().execute();
    }

    private void initList(ArrayList<YoutubeDataModel> mListData) {
        mList_videos.setLayoutManager(new LinearLayoutManager(YoutubeSearchActivity.this));
        adapter = new VideoPostAdapter(YoutubeSearchActivity.this, mListData, new OnItemClickListener() {
            @Override
            public void onItemClick(YoutubeDataModel item) {
                YoutubeDataModel youtubeDataModel = item;
                Intent intent = new Intent(YoutubeSearchActivity.this, DetailsActivity.class);
                intent.putExtra(YoutubeDataModel.class.toString(), youtubeDataModel);
                startActivity(intent);
            }
        });
        mList_videos.setAdapter(adapter);

    }

    //create an asynctask to get all the data from youtube
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CHANNLE_GET_URL);
            Log.e("URL", CHANNLE_GET_URL);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                String json = EntityUtils.toString(httpEntity);
                return json;
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String response){
            super.onPostExecute(response);
            if(response != null){
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response",jsonObject.toString());
                    mListData = parseVideoListFromResponse(jsonObject);
                    initList(mListData);


                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject) {
        ArrayList<YoutubeDataModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json.has("id")) {
                        JSONObject jsonID = json.getJSONObject("id");
                        String video_id = "";
                        if (jsonID.has("videoId")) {
                            video_id = jsonID.getString("videoId");
                        }
                        if (jsonID.has("kind")) {
                            if (jsonID.getString("kind").equals("youtube#video")) {
                                YoutubeDataModel youtubeObject = new YoutubeDataModel();
                                JSONObject jsonSnippet = json.getJSONObject("snippet");
                                String title = jsonSnippet.getString("title");
                                String description = jsonSnippet.getString("description");
                                String publishedAt = jsonSnippet.getString("publishedAt");
                                String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");

                                youtubeObject.setTitle(title);
                                youtubeObject.setDescription(description);
                                youtubeObject.setPublishedAt(publishedAt);
                                youtubeObject.setThumbnail(thumbnail);
                                youtubeObject.setVideo_id(video_id);
                                mList.add(youtubeObject);

                            }
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mList;

    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            try {
                String encodedQueryString = URLEncoder.encode(query,"UTF-8");
                SEARCH_KEYWORD = encodedQueryString;
                Log.e("SearchQuery", query);
            } catch (UnsupportedEncodingException e) {
                SEARCH_KEYWORD = query;
                e.printStackTrace();
            }

        }

        CHANNLE_GET_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + SEARCH_KEYWORD + "&maxResults=25&order=viewCount&key=" +GOOGLE_YOUTUBE_API_KEY;
    }


}

