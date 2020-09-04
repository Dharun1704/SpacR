package com.example.spacr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spacr.NASAapi.ApiSearchInterface;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.itangqi.waveloadingview.WaveLoadingView;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchDetailActivity extends AppCompatActivity {

    private static final String TAG = "SearchDetailActivity";

    private RelativeLayout relativeLayout;
    private ImageView image;
    private WebView video;
    private WaveLoadingView loadingView;
    TextView title, photographer, id, keywords, date, desc;

    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(Color.parseColor("#080820"));

        image = findViewById(R.id.detail_image);
        video = findViewById(R.id.detail_video);
        relativeLayout = findViewById(R.id.videoLayout);
        title = findViewById(R.id.item_title);
        photographer = findViewById(R.id.item_photographer);
        id = findViewById(R.id.item_id);
        keywords = findViewById(R.id.item_keywords);
        date = findViewById(R.id.item_dateCreated);
        desc = findViewById(R.id.item_desc);
        loadingView = findViewById(R.id.detailWaveLoader);

        Intent intent = getIntent();
        String mediaType = intent.getStringExtra("media");
        String imgUrl = intent.getStringExtra("imgUrl");
        String mTitle = intent.getStringExtra("title");
        String mPhoGrp = intent.getStringExtra("photographer");
        String mId = intent.getStringExtra("id");
        ArrayList<String> mKeywords = intent.getStringArrayListExtra("keywords");
        String mDate = intent.getStringExtra("date");
        String mDesc = intent.getStringExtra("desc");

        title.setText(mTitle);

        assert mediaType != null;
        if (mediaType.equals("image") && imgUrl != null) {
            Picasso
                    .get()
                    .load(imgUrl)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            loadingView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
        else if (mediaType.equals("video")) {
            assert imgUrl != null;
            getVideoUrl(imgUrl);
        }

        photographer.setText("Photographed by: " + mPhoGrp);
        id.setText("NASA ID: " + mId);
        String keyword = "";
        if (mKeywords == null) {
            keyword = "null";
        }
        else {
            for (int i = 0; i < mKeywords.size(); i++) {
                if (i != mKeywords.size() - 1)
                    keyword += mKeywords.get(i) + ", ";
                else
                    keyword += mKeywords.get(i);
            }
        }

        keywords.setText("Keywords: " + keyword);
        date.setText("Created at: " + mDate);
        desc.setText(mDesc);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void getVideoUrl(String url) {
        String[] parts = url.split("~");
        parts[1] = "mobile.mp4";
        videoPath = parts[0] + "~" + parts[1];

        image.setVisibility(View.GONE);
        video.getSettings().setJavaScriptEnabled(true);
        video.loadUrl(videoPath);
        video.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadingView.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}