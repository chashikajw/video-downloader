package com.dreamso.downvideoapp.activities.youtube;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamso.downvideoapp.R;
import com.dreamso.downvideoapp.YoutubeDL.Extractor;
import com.dreamso.downvideoapp.YoutubeDL.Format;
import com.dreamso.downvideoapp.YoutubeDL.Utils.FormatUtils;
import com.dreamso.downvideoapp.YoutubeDL.VideoInfo;
import com.dreamso.downvideoapp.customUI.RecyclerViewEmptySupport;
import com.dreamso.downvideoapp.fragments.adapters.CommentAdapter;
import com.dreamso.downvideoapp.fragments.adapters.VideoInfoAdapter;
import com.dreamso.downvideoapp.fragments.models.YoutubeCommentModel;
import com.dreamso.downvideoapp.fragments.models.YoutubeDataModel;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import me.harshithgoka.youtubedl.Adapters.FormatAdapter;

public class DetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static String GOOGLE_YOUTUBE_API = "AIzaSyDY5KEE5eQbAxowrjoQY7L-N3wql81I45k";
    private YoutubeDataModel youtubeDataModel = null;
    TextView textViewName;
    TextView textViewDes;
    TextView textViewDate;
    // ImageView imageViewIcon;
    public static final String VIDEO_ID = "c2UNv38V6y4";
    private YouTubePlayerView mYoutubePlayerView = null;
    private YouTubePlayer mYoutubePlayer = null;
    private ArrayList<YoutubeCommentModel> mListData = new ArrayList<>();
    private CommentAdapter mAdapter = null;
    private RecyclerView mList_videos = null;


    //downloadayout
    static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 9293;
    static final String HISTORY = "VideoInfos";


    TextView log;


    List<Format> formats;

    Extractor extractor;

    Pattern youtubeUrlPattern;

    RecyclerViewEmptySupport formatsRecyclerView;
    FormatAdapter formatAdapter;
    LinearLayoutManager formatLinearLayoutManager;

    RecyclerViewEmptySupport viRecyclerView;
    VideoInfoAdapter viAdapter;
    LinearLayoutManager viLinearLayoutManager;

    BottomSheetBehavior<View> bottomSheetBehavior;
    List<ProgressBar> progressBars;

    SharedPreferences mPrefs;
    SharedPreferences sharedPreferences;
    ArrayList<VideoInfo> history;

    TextView videoTitle;
    Gson gson;
    HashMap<Long, Format> inProgressDownloads;
    ArrayList<Pair<Format, Format>> mixingDownloads;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                return;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        youtubeDataModel = getIntent().getParcelableExtra(YoutubeDataModel.class.toString());
        Log.e("", youtubeDataModel.getDescription());

        mYoutubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        mYoutubePlayerView.initialize(GOOGLE_YOUTUBE_API, this);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDes = (TextView) findViewById(R.id.textViewDes);
        // imageViewIcon = (ImageView) findViewById(R.id.imageView);
        textViewDate = (TextView) findViewById(R.id.textViewDate);

        textViewName.setText(youtubeDataModel.getTitle());
        textViewDes.setText(youtubeDataModel.getDescription());
        textViewDate.setText(youtubeDataModel.getPublishedAt());

        mList_videos = (RecyclerView) findViewById(R.id.mList_videos);
        new RequestYoutubeCommentAPI().execute();

        /*if (!checkPermissionForReadExtertalStorage()) {
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        //downloadLayout
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        }







        log = (TextView) findViewById(R.id.textView);
        log.setMovementMethod(new ScrollingMovementMethod());



        progressBars = new ArrayList<>();
        progressBars.add((ProgressBar) findViewById(R.id.progressBar));
        progressBars.add((ProgressBar) findViewById(R.id.progressBar2));

        formats = new ArrayList<>();
        extractor = new Extractor();

        youtubeUrlPattern = Pattern.compile(extractor._VALID_URL);

        // Formats Holder Bottom Sheet
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        videoTitle = findViewById(R.id.video_title);
        // Formats
        formatsRecyclerView = findViewById(R.id.recycler_view);
        formatAdapter = new FormatAdapter(getApplicationContext(), formats, this);
        formatsRecyclerView.setAdapter(formatAdapter);
        formatLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        formatsRecyclerView.setLayoutManager(formatLinearLayoutManager);




        // Best Download

        Button btn = findViewById(R.id.best_download);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Format f1 = null, f2 = null;
                for (Format format: formats) {
                    if (format.getItag() == 137) {
                        f1 = format;
                    }
                    if (format.getItag() == 140) {
                        f2 = format;
                    }
                }
                Pair<Format, Format> p = new Pair<>(f1, f2);

                if (f1 == null || f2 == null) {
                    return;
                }

                download(f1);
                download(f2);

                mixingDownloads.add(p);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("mixing_downloads", gson.toJson(mixingDownloads));
                editor.apply();
            }
        });

    }

    //dowloadLayout

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mPrefs.edit();

        String connectionsJSONString = gson.toJson(history);
        editor.putString(HISTORY, connectionsJSONString);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            super.onBackPressed();
        }
        else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d("INFO", "onCreateOptionsMenu called");
        getMenuInflater().inflate(R.menu.menu_formats, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_LONG).show();
//                openSettingPage();
                return true;
            default:
                break;
        }

        return false;
    }

    private void println (String s) {
        log.append(s + "\n");
    }

    public String preprocess (String s) {
        int index = s.lastIndexOf("#");
        if (index > 0) {
            s = s.substring(0, index);
        }

        s = s.replaceFirst("m.youtube.com", "www.youtube.com");
        s = s.replaceFirst("&.*", "");

        return s;
    }

    public void startDownload(String url) {
        Log.e("beforeStartDown", url);
        url = preprocess(url);

        java.util.regex.Matcher m = youtubeUrlPattern.matcher(url);
        println("Url: " + url);

        AsyncTask<String, Void, VideoInfo> asyncTask = new DetailsActivity.YoutubeDLAsyncTaskNew(getApplicationContext(), extractor);
        asyncTask.execute(url);
    }



    public void showLoading () {
        for (ProgressBar bar : progressBars) {
            bar.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoading() {
        for (ProgressBar bar: progressBars) {
            bar.setVisibility(View.GONE);
        }
    }

    public void loadVideoInfo(VideoInfo videoInfo) {
        Log.d("II", "Loading videoInfo");
        int numRemoved = formats.size();
        formats.clear();
        formatAdapter.notifyItemRangeRemoved(0, numRemoved);
        formats.addAll(videoInfo.getFormats());
        formatAdapter.notifyItemRangeInserted(0, videoInfo.getFormats().size());
        videoTitle.setText(videoInfo.getTitle());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }



    class YoutubeDLAsyncTaskNew extends AsyncTask<String, Void, VideoInfo> {
        Context context;
        Extractor ytextractor;

        public YoutubeDLAsyncTaskNew(Context context, Extractor extractor) {
            this.context = context;
            ytextractor = extractor;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected VideoInfo doInBackground(String... strings) {
            String you_url = strings[0];
            Log.e("StringURL",you_url);
            return ytextractor.getFormats(you_url);
        }

        @Override
        protected void onPostExecute(VideoInfo videoInfo) {
            hideLoading();
            if (videoInfo != null) {

                List<Format> formats = videoInfo.getFormats();

                if (formats.size() > 0) {


                    loadVideoInfo(videoInfo);

                    String finalurl = formats.get(0).getUrl();
                    println(finalurl);

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    assert clipboard != null;
                    ClipData clip = ClipData.newRawUri("DownloadURL", Uri.parse(finalurl));
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(getApplicationContext(), String.format("Best quality link (%s) copied to Clipboard", formats.get(0).getQuality()), Toast.LENGTH_SHORT).show();
                }
                else {
                    println("No. of formats: 0");
                    Toast.makeText(getApplicationContext(), "Not yet implemented encrypted signature", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                println("Error connecting to the Internet");
            }
        }
    };


    public String getDownloadDirectory(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("download_folder", Environment.DIRECTORY_DOWNLOADS);
    }

    public String greatestCommonPrefix(String a, String b) {
        int minLength = Math.min(a.length(), b.length());
        for (int i = 0; i < minLength; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return a.substring(0, i);
            }
        }
        return a.substring(0, minLength);
    }

    public void download (Format format) {
        String extension = FormatUtils.INSTANCE.getExtension(format);
        String filename = format.sanitizeFilename() + "." + extension;
        Log.d("Filename", filename);
        String final_download_directory = getDownloadDirectory(getApplicationContext());

        DownloadManager dm =null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dm = getSystemService(DownloadManager.class);
        }else{
            dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        }

        // Temporary Download folder
        File[] files = ContextCompat.getExternalFilesDirs(getApplicationContext(), Environment.DIRECTORY_MOVIES);
        int maximum_length = 0;
        int which_index = -1;
        String s;
        for (int i = 0; i < files.length; i++) {
            s = greatestCommonPrefix(final_download_directory, files[i].getAbsolutePath());
            if (s.length() > maximum_length) {
                maximum_length = s.length();
                which_index = i;
            }
        }
        Uri uri = null;
        if (which_index > -1) {
            uri = Uri.fromFile(files[which_index]);
        }

        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(format.getUrl()));
        req.setTitle(filename);
        req.setDescription(final_download_directory + File.separator + filename);
        if (uri != null) {
            req.setDestinationUri(uri);
        }
        else {
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator + filename);
        }
        req.allowScanningByMediaScanner();
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        long download_id = dm.enqueue(req);

        inProgressDownloads.put(download_id, format);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("in_progress", gson.toJson(inProgressDownloads));
        editor.apply();

        Toast.makeText(getApplicationContext(), String.format("Your media file now downloading to \"%s\" folder. Check the notification area.", final_download_directory), Toast.LENGTH_SHORT).show();
        format.setDowmloadState(Format.DownloadState.DOWNLOADING);
    }

    public void back_btn_pressed(View view) {
        finish();
    }

//    public void playVideo(View view) {
//        if (mYoutubePlayer != null) {
//            if (mYoutubePlayer.isPlaying())
//                mYoutubePlayer.pause();
//            else
//                mYoutubePlayer.play();
//        }
//    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        if (!wasRestored) {
            youTubePlayer.cueVideo(youtubeDataModel.getVideo_id());
        }
        mYoutubePlayer = youTubePlayer;
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    public void share_btn_pressed(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String link = ("https://www.youtube.com/watch?v=" + youtubeDataModel.getVideo_id());
        // this is the text that will be shared
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, youtubeDataModel.getTitle()
                + "Share");

        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "share"));
    }

    public void downloadVideo(View view) {
        String link = ("https://www.youtube.com/watch?v=" + youtubeDataModel.getVideo_id());
        startDownload(link);
        //Intent myIntent = new Intent(getBaseContext(), DetailsActivity.class);
        //myIntent.putExtra("link",link);
        //startActivity(myIntent);
        //get the download URL
        /*String youtubeLink = ("https://www.youtube.com/watch?v=" + youtubeDataModel.getVideo_id());
        Log.e("VIdeoId :", youtubeDataModel.getVideo_id());
        Log.e("Youtube Link:", youtubeLink);

        YouTubeUriExtractor ytEx = new YouTubeUriExtractor(DetailsActivity.this) {
            @Override
            public void onUrisAvailable(String videoID, String videoTitle, SparseArray<YtFile> ytFiles) {
                if (ytFiles != null) {
                    int itag = 22;
                    //This is the download URL
                    String downloadURL = ytFiles.get(itag).getUrl();
                    Log.e("download URL :", downloadURL);

                    //now download it like a file
                    new RequestDownloadVideoStream().execute(downloadURL, videoTitle);


                }

            }
        };

        ytEx.execute(youtubeLink);
         */
    }



    private ProgressDialog pDialog;


    private class RequestDownloadVideoStream extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DetailsActivity.this);
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream is = null;
            URL u = null;
            int len1 = 0;
            int temp_progress = 0;
            int progress = 0;
            try {
                u = new URL(params[0]);
                is = u.openStream();
                HttpURLConnection huc = (HttpURLConnection) u.openConnection();
                huc.connect();
                int size = huc.getContentLength();

                if (huc != null) {
                    String file_name = params[1] + ".mp4";
                    String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/YoutubeVideos";
                    Log.e("storage path: ", storagePath);
                    File f = new File(storagePath);
                    if (!f.exists()) {
                        f.mkdir();
                    }

                    FileOutputStream fos = new FileOutputStream(f+"/"+file_name);
                    byte[] buffer = new byte[1024];
                    int total = 0;
                    if (is != null) {
                        while ((len1 = is.read(buffer)) != -1) {
                            total += len1;
                            // publishing the progress....
                            // After this onProgressUpdate will be called
                            progress = (int) ((total * 100) / size);
                            if(progress >= 0) {
                                temp_progress = progress;
                                publishProgress("" + progress);
                            }else
                                publishProgress("" + temp_progress+1);

                            fos.write(buffer, 0, len1);
                        }
                    }

                    if (fos != null) {
                        publishProgress("" + 100);
                        fos.close();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }


    private class RequestYoutubeCommentAPI extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String VIDEO_COMMENT_URL = "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&maxResults=100&videoId=" + youtubeDataModel.getVideo_id() + "&key=" + GOOGLE_YOUTUBE_API;
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(VIDEO_COMMENT_URL);
            Log.e("url: ", VIDEO_COMMENT_URL);
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
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    mListData = parseJson(jsonObject);
                    initVideoList(mListData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initVideoList(ArrayList<YoutubeCommentModel> mListData) {
        mList_videos.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommentAdapter(this, mListData);
        mList_videos.setAdapter(mAdapter);
    }

    public ArrayList<YoutubeCommentModel> parseJson(JSONObject jsonObject) {
        ArrayList<YoutubeCommentModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    YoutubeCommentModel youtubeObject = new YoutubeCommentModel();
                    JSONObject jsonTopLevelComment = json.getJSONObject("snippet").getJSONObject("topLevelComment");
                    JSONObject jsonSnippet = jsonTopLevelComment.getJSONObject("snippet");

                    String title = jsonSnippet.getString("authorDisplayName");
                    String thumbnail = jsonSnippet.getString("authorProfileImageUrl");
                    String comment = jsonSnippet.getString("textDisplay");

                    youtubeObject.setTitle(title);
                    youtubeObject.setComment(comment);
                    youtubeObject.setThumbnail(thumbnail);
                    mList.add(youtubeObject);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mList;

    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int result2 = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED);
        }
        return false;
    }
}