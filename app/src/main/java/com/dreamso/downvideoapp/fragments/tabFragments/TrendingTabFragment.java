package com.dreamso.downvideoapp.fragments.tabFragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamso.downvideoapp.R;
import com.dreamso.downvideoapp.activities.youtube.DetailsActivity;
import com.dreamso.downvideoapp.fragments.adapters.VideoPostAdapter;
import com.dreamso.downvideoapp.fragments.models.YoutubeDataModel;
import com.dreamso.downvideoapp.interfaces.OnItemClickListener;
import com.neovisionaries.i18n.CountryCode;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrendingTabFragment extends Fragment {

    private static String GOOGLE_YOUTUBE_API_KEY = "AIzaSyDY5KEE5eQbAxowrjoQY7L-N3wql81I45k";//here you should use your api key for testing purpose you can use this api also
    private static String CHANNEL_ID = "UCoMdktPbSTixAyNGwb-UYkQ"; //here you should use your channel id for testing purpose you can use this api also
    private static String REGION_CODE = "IN";

    private static String CHANNLE_GET_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet&chart=mostPopular&regionCode=" + REGION_CODE + "&maxResults=20&key=" + GOOGLE_YOUTUBE_API_KEY + "";

    private RecyclerView mList_videos = null;
    private VideoPostAdapter adapter = null;
    private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();


    public TrendingTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragmenttab_trending, container, false);





        String locale = rootView.getResources().getConfiguration().locale.getCountry();

        CountryCode cc = CountryCode.getByCode(locale);
        REGION_CODE = cc.getAlpha2();

        Log.e("region_code", REGION_CODE );
        CHANNLE_GET_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet&chart=mostPopular&regionCode=" + REGION_CODE + "&maxResults=20&key=" + GOOGLE_YOUTUBE_API_KEY + "";


        mList_videos = (RecyclerView) rootView.findViewById(R.id.mList_videos);
        initList(mListData);
        new RequestYoutubeAPI().execute();
        return rootView;
    }

    private void initList(ArrayList<YoutubeDataModel> mListData) {
        mList_videos.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new VideoPostAdapter(getActivity(), mListData, new OnItemClickListener() {
            @Override
            public void onItemClick(YoutubeDataModel item) {
                YoutubeDataModel youtubeDataModel = item;
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
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

    private ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject){
        ArrayList<YoutubeDataModel> mList = new ArrayList<>();

        if(jsonObject.has("items")){
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    if (json.has("id")) {

                        String video_id = json.getString("id");

                        if (json.has("kind")) {
                            if (json.getString("kind").equals("youtube#video")) {
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
                                Log.e("youtubeobject",youtubeObject.getTitle());

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

}
