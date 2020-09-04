package com.example.spacr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spacr.Models.APODResults;
import com.example.spacr.Models.Item;
import com.example.spacr.Models.SearchResult;
import com.example.spacr.NASAapi.ApiDisplayClient;
import com.example.spacr.NASAapi.ApiDisplayInterface;
import com.example.spacr.NASAapi.ApiSearchClient;
import com.example.spacr.NASAapi.ApiSearchInterface;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import me.itangqi.waveloadingview.WaveLoadingView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    public static final String API = "WaSrGyZqy1UDtEYSEmTRkHzs4inJl1uGRowbHsGI";
    private static final String TAG = "MainActivity";

    public static Calendar calendar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Adapter adapter;
    private LinearLayout displayLayout, searchLayout;

    Button chooseDate, goToAPOD;
    private TextView date, resultsNo;
    private WaveLoadingView loadingView, recyclerWaveLoader;
    private ImageView image;
    private WebView video;
    private MaterialSearchBar searchBar;

    private ArrayList<Item> searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(Color.parseColor("#080820"));

        displayLayout = findViewById(R.id.display_layout);
        searchLayout = findViewById(R.id.search_layout);

        date = findViewById(R.id.date);
        chooseDate = findViewById(R.id.chooseDate);
        image = findViewById(R.id.space_image);
        video = findViewById(R.id.space_video);
        loadingView = findViewById(R.id.waveLoader);
        loadingView.setProgressValue(50);
        searchBar = findViewById(R.id.searchBar);
        resultsNo = findViewById(R.id.searchResultsNo);
        goToAPOD = findViewById(R.id.goToAPOD);
        recyclerWaveLoader = findViewById(R.id.recyclerWaveLoader);

        recyclerView = findViewById(R.id.searchRecyclerView);
        layoutManager = new GridLayoutManager(this, 2);

        calendar = Calendar.getInstance();
        calendar.get(Calendar.YEAR);
        calendar.get(Calendar.MONTH);
        calendar.get(Calendar.DAY_OF_MONTH);

        String dateString = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
        date.setText(dateString);

        getApod(calendar);
        setSearchBar();

        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        goToAPOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.GONE);
                displayLayout.setVisibility(View.VISIBLE);

                searchBar.closeSearch();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        if (!calendar.after(Calendar.getInstance())) {
            String dateString = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());
            date.setText(dateString);

            getApod(calendar);
        }
        else
            Toast.makeText(MainActivity.this, "APOD for future dates not available.", Toast.LENGTH_SHORT).show();
    }

    private void getApod(Calendar calendar) {
        displayLayout.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.GONE);


        ApiDisplayInterface apiDisplayInterface = ApiDisplayClient.getApiDisplayClient().create(ApiDisplayInterface.class);

        String apiDate = calendar.get(Calendar.YEAR) + "-";
        if (calendar.get(Calendar.MONTH) < 10) {
            apiDate += "0" + (calendar.get(Calendar.MONTH) + 1) + "-";
        } else
            apiDate += (calendar.get(Calendar.MONTH) + 1) + "-";

        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            apiDate += "0" + calendar.get(Calendar.DAY_OF_MONTH);
        } else
            apiDate += calendar.get(Calendar.DAY_OF_MONTH);

        Call<APODResults> call = apiDisplayInterface.getAPOD(apiDate, true, API);
        call.enqueue(new Callback<APODResults>() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onResponse(Call<APODResults> call, Response<APODResults> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getMedia_type().equals("image")) {
                        video.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                        loadingView.setVisibility(View.VISIBLE);
                        Picasso
                                .get()
                                .load(response.body().getHdurl())
                                .into(image, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        loadingView.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                    } else if (response.body().getMedia_type().equals("video")) {
                        Log.d(TAG, "onResponse: " + response.body().getUrl());
                        image.setVisibility(View.GONE);
                        loadingView.setVisibility(View.VISIBLE);
                        video.getSettings().setJavaScriptEnabled(true);
                        video.loadUrl(response.body().getUrl() + "?autoplay=1&vq=small");
                        video.setWebViewClient(new WebViewClient() {

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                loadingView.setVisibility(View.GONE);
                                video.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                }
            }

            @Override
            public void onFailure(Call<APODResults> call, Throwable t) {

            }
        });
    }

    private void getSearchResults(String query) {
        searchItem = new ArrayList<>();

        if (searchLayout.getVisibility() == View.GONE) {
            displayLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
        }
        resultsNo.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        recyclerWaveLoader.setVisibility(View.VISIBLE);

        ApiSearchInterface apiSearchInterface = ApiSearchClient.getApiSearchClient().create(ApiSearchInterface.class);
        Call<SearchResult> call = apiSearchInterface.getSearchResults(query);
        call.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String number = "Results: " + response.body().getCollection().getItems().size();
                    resultsNo.setText(number);
                    for (int i = 0; i < response.body().getCollection().getItems().size(); i++) {
                        if (!response.body().getCollection().getItems().get(i).getData().get(0)
                                .getMedia_type().equals("audio")) {
                            searchItem.add(response.body().getCollection().getItems().get(i));
                        }
                    }

                    recyclerWaveLoader.setVisibility(View.GONE);
                    resultsNo.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = null;
                    adapter = new Adapter(MainActivity.this, searchItem);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
                        @SuppressLint("SimpleDateFormat")
                        @Override
                        public void onItemClick(int position) {
                            Item item = searchItem.get(position);
                            assert item != null;
                            Date date = item.getData().get(0).getDate_created();
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                            String formattedDate = dateFormat.format(date);

                            Intent intent = new Intent(MainActivity.this, SearchDetailActivity.class);
                            intent.putExtra("media", item.getData().get(0).getMedia_type());
                            intent.putExtra("imgUrl", item.getLinks().get(0).getHref());
                            intent.putExtra("title", item.getData().get(0).getTitle());
                            intent.putExtra("photographer", item.getData().get(0).getPhotographer());
                            intent.putExtra("id", item.getData().get(0).getNasa_id());
                            intent.putExtra("keywords", item.getData().get(0).getKeywords());
                            intent.putExtra("date", formattedDate);
                            intent.putExtra("desc", item.getData().get(0).getDescription());

                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {

            }
        });
    }

    private void setSearchBar() {

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    getSearchResults(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if (text.length() > 2) {
                    getSearchResults(text.toString());
                }
                else
                    Toast.makeText(MainActivity.this, "Length of search word must be greater than 2",
                            Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });
    }
}