package com.manimaran.wikiaudio.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.manimaran.wikiaudio.R;
import com.manimaran.wikiaudio.activities.CommonWebActivity;
import com.manimaran.wikiaudio.constants.Constants;
import com.manimaran.wikiaudio.constants.Urls;
import com.manimaran.wikiaudio.listerners.OnLanguageSelectionListener;
import com.manimaran.wikiaudio.utils.GeneralUtils;
import com.manimaran.wikiaudio.utils.PrefManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EndlessAdapter extends ArrayAdapter<String> {

    private static final int RECORD_AUDIO_REQUEST_CODE = 101;
    private List<String> itemList;
    private Context ctx;
    private Activity activity;
    private int layoutId;
    private Boolean isContributionMode;

    private PrefManager pref;

    private OnLanguageSelectionListener listener;

    public EndlessAdapter(Context ctx, List<String> itemList, int layoutId, Boolean isContributionMode) {
        super(ctx, layoutId, itemList);
        this.itemList = itemList;
        this.ctx = ctx;
        this.activity = (Activity) ctx;
        this.layoutId = layoutId;
        this.isContributionMode = isContributionMode;
        this.pref = new PrefManager(ctx);
    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = url.substring(url.lastIndexOf(".") + 1);
        if (!TextUtils.isEmpty(extension)) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public void setCallbackListener(OnLanguageSelectionListener listener) {
        this.listener = listener;
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
        View mView = convertView;

        if (mView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = inflater.inflate(layoutId, parent, false);
        }

        // We should use class holder pattern
        TextView tv = mView.findViewById(R.id.txt1);
        tv.setText(itemList.get(position));

        tv.setOnClickListener(view -> {
            Activity activity1 = (Activity) ctx;
            if (isContributionMode) {
                if (GeneralUtils.checkPermissionGranted(activity1)) {
                    GeneralUtils.showRecordDialog(activity, itemList.get(position));
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getPermissionToRecordAudio();
                }
            } else {
                openWiktionaryWebView(position);
            }
        });

        if (isContributionMode) {
            LinearLayout btnWiki = mView.findViewById(R.id.btn_wiki_meaning);
            btnWiki.setVisibility(View.VISIBLE);
            btnWiki.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWiktionaryWebView(position);
                }
            });
        }


        return mView;

    }

    private void openWiktionaryWebView(int position) {
        Activity activity1 = (Activity) ctx;
        Intent intent = new Intent(ctx, CommonWebActivity.class);
        String word = itemList.get(position);
        String url = String.format(Urls.WIKTIONARY_WEB, isContributionMode ? pref.getLanguageCodeSpell4Wiki() : pref.getLanguageCodeWiktionary(), word);
        intent.putExtra(Constants.TITLE, word);
        intent.putExtra(Constants.URL, url);
        intent.putExtra(Constants.IS_CONTRIBUTION_MODE, isContributionMode);
        intent.putExtra(Constants.IS_WIKTIONARY_WORD, true);
        activity1.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermissionToRecordAudio() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        Activity activity = (Activity) ctx;
        if (!GeneralUtils.checkPermissionGranted((activity))) {
            showMsg("Must need Microphone and Storage permissions.\nPlease grant those permissions");

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            activity.requestPermissions(
                    new String[]
                            {
                                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }
                    , RECORD_AUDIO_REQUEST_CODE);
        }
    }


    private void showMsg(String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

}

