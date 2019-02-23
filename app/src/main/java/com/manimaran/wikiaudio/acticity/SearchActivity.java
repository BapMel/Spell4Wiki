package com.manimaran.wikiaudio.acticity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.manimaran.wikiaudio.R;
import com.manimaran.wikiaudio.fragment.BottomSheetFragment;
import com.manimaran.wikiaudio.util.GeneralUtils;
import com.manimaran.wikiaudio.util.PrefManager;
import com.manimaran.wikiaudio.view.EndlessAdapter;
import com.manimaran.wikiaudio.view.EndlessListView;
import com.manimaran.wikiaudio.wiki.MediaWikiClient;
import com.manimaran.wikiaudio.wiki.ServiceGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements EndlessListView.EndlessListener {

    private EndlessListView resultListView;

    private String queryString;
    private Integer nextOffset;
    private boolean doubleBackToExitPressedOnce = false;
    private PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        pref = new PrefManager(getApplicationContext());

        SearchView searchBar = (SearchView) findViewById(R.id.search_bar);
        searchBar.requestFocus();
        searchBar.setIconifiedByDefault(false);
        searchBar.setQueryHint(getResources().getString(R.string.search_here));
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                queryString = s;
                nextOffset = 0;
                resultListView.reset();

                ImageView wiktionaryLogo = (ImageView) findViewById(R.id.wiktionary_logo);
                wiktionaryLogo.setVisibility(View.GONE);
                resultListView.setVisibility(View.VISIBLE);

                search(queryString);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        resultListView = (EndlessListView) findViewById(R.id.search_result_list);
        resultListView.setLoadingView(R.layout.loading_row);
        resultListView.setAdapter(new EndlessAdapter(this, new ArrayList<String>(), R.layout.search_result_row, false));
        //
        resultListView.setListener(this);
        resultListView.setVisibility(View.INVISIBLE);


        setTitle("Wiktionary");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.action_wiktionary).setIcon(R.drawable.ic_record);
        menu.findItem(R.id.action_settings).setIcon(R.drawable.ic_cancel);
        if(pref.getIsAnonymous())
            menu.findItem(R.id.action_wiktionary).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_wiktionary:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            case R.id.action_logout:
                GeneralUtils.logoutAlert(SearchActivity.this);
                return true;
            case R.id.action_lang_change:
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                bottomSheetFragment.setCancelable(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void search(String query) {
        MediaWikiClient mediaWikiClient = ServiceGenerator.createService(MediaWikiClient.class,
                getApplicationContext(), String.format(ServiceGenerator.WIKTIONARY_URL, pref.getLangCode()));
        Call<ResponseBody> call = mediaWikiClient.fetchRecords(query, nextOffset);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseStr = response.body().string();
                        try {
                            processSearchResult(responseStr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            searchFailed("Server misbehaved! Please try again later.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        searchFailed("Please check your connection!\nScroll to try again!");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                searchFailed("Please check your connection!\nScroll to try again!");
            }
        });
    }

    private void searchFailed(String msg) {
        resultListView.loadLaterOnScroll();
        Toast.makeText(this, "Search failed!\n" + msg, Toast.LENGTH_LONG).show();
    }

    private void processSearchResult(String responseStr) throws JSONException {
        JSONObject reader = new JSONObject(responseStr);

        try {
            ArrayList<String> titleList = new ArrayList<>();
            Log.w("TAG", "Wiki " + new Gson().toJson(reader));
            JSONArray searchResults = reader.getJSONObject("query").optJSONArray("search");
            for (int ii = 0; ii < searchResults.length(); ii++) {
                titleList.add(
                        searchResults.getJSONObject(ii).getString("title")
                        //+ " --> " + searchResults.getJSONObject(ii).getString("pageid")
                );
            }
            if (reader.has("continue"))
                nextOffset = reader.getJSONObject("continue").getInt("sroffset");
            else
                nextOffset = null;
            resultListView.addNewData(titleList);
        }catch (Exception e)
        {
            e.printStackTrace();
            resultListView.addNewData(Collections.singletonList("Error accrued"));
        }
    }

    @Override
    public boolean loadData() {
        // Triggered only when new data needs to be appended to the list
        // Return true if loading is in progress, false if there is no more data to load
        if (nextOffset != null) {
            search(queryString);
            return true;
        } else
            return false;
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce)
            super.onBackPressed();
        else
        {
            this.doubleBackToExitPressedOnce = true;
            GeneralUtils.showToast(getApplicationContext(), getString(R.string.alert_to_exit));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}

