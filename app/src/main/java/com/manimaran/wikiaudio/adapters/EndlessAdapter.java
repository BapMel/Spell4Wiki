package com.manimaran.wikiaudio.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;
import com.manimaran.wikiaudio.BuildConfig;
import com.manimaran.wikiaudio.R;
import com.manimaran.wikiaudio.activities.CommonWebActivity;
import com.manimaran.wikiaudio.constants.Constants;
import com.manimaran.wikiaudio.constants.Urls;
import com.manimaran.wikiaudio.utils.GeneralUtils;
import com.manimaran.wikiaudio.utils.PrefManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.manimaran.wikiaudio.constants.EnumTypeDef.ListMode;

public class EndlessAdapter extends ArrayAdapter<String> {

    private static final int RECORD_AUDIO_REQUEST_CODE = 101;
    private List<String> itemList;
    private Activity activity;

    private PrefManager pref;
    @ListMode
    private int mode;

    private View rootView;

    public EndlessAdapter(Context ctx, List<String> itemList, @ListMode int mode) {
        super(ctx, R.layout.search_result_row, itemList);
        this.itemList = itemList;
        this.activity = (Activity) ctx;
        this.pref = new PrefManager(ctx);
        this.mode = mode;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public String getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).hashCode();
    }

    @NotNull
    @Override
    public View getView(final int position, View convertView, @NotNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(inflater != null)
                rootView = inflater.inflate(R.layout.search_result_row, parent, false);
        }else
            rootView = convertView;

        // We should use class holder pattern
        TextView tv = rootView.findViewById(R.id.txt1);
        tv.setText(itemList.get(position));

        tv.setOnClickListener(view -> {

            switch (mode) {
                case ListMode.SPELL_4_WIKI:
                case ListMode.SPELL_4_WORD_LIST:
                case ListMode.SPELL_4_WORD:
                    if (GeneralUtils.checkPermissionGranted(activity)) {
                        GeneralUtils.showRecordDialog(activity, itemList.get(position), getLanguageCode());
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getPermissionToRecordAudio();
                    }
                    break;
                case ListMode.WIKTIONARY:
                default:
                    openWiktionaryWebView(position);
                    break;
                case ListMode.TEMP:
                    break;
            }
        });

        LinearLayout btnWiki = rootView.findViewById(R.id.btn_wiki_meaning);
        btnWiki.setVisibility(View.VISIBLE);
        btnWiki.setOnClickListener(v -> openWiktionaryWebView(position));

        return rootView;
    }

    private String getLanguageCode() {
        switch (mode) {
            case ListMode.SPELL_4_WIKI:
                return pref.getLanguageCodeSpell4Wiki();
            case ListMode.SPELL_4_WORD_LIST:
                return pref.getLanguageCodeSpell4WordList();
            case ListMode.SPELL_4_WORD:
                return pref.getLanguageCodeSpell4Word();
            case ListMode.WIKTIONARY:
                return pref.getLanguageCodeWiktionary();
            case ListMode.TEMP:
            default:
                return null;
        }
    }

    private void openWiktionaryWebView(int position) {
        Intent intent = new Intent(activity, CommonWebActivity.class);
        String word = itemList.get(position);
        String langCode = getLanguageCode();
        if (langCode == null)
            langCode = Constants.DEFAULT_LANGUAGE_CODE;

        String url = String.format(Urls.WIKTIONARY_WEB, langCode, word);
        intent.putExtra(Constants.TITLE, word);
        intent.putExtra(Constants.URL, url);
        intent.putExtra(Constants.IS_WIKTIONARY_WORD, true);
        intent.putExtra(Constants.LANGUAGE_CODE, langCode);
        activity.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermissionToRecordAudio() {
        if (GeneralUtils.permissionDenied(activity))
            showAppSettingsPageHint();
        activity.requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
                , RECORD_AUDIO_REQUEST_CODE);
    }

    private void showAppSettingsPageHint() {
        if (rootView != null) {
            Snackbar.make(rootView, activity.getString(R.string.permisstion_required), Snackbar.LENGTH_LONG)
                    .setAction(activity.getString(R.string.go_settings), view -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    })
                    .show();
        }
    }

}

