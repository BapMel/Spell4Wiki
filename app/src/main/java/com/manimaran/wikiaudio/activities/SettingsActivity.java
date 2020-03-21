package com.manimaran.wikiaudio.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.manimaran.wikiaudio.R;
import com.manimaran.wikiaudio.fragments.BottomSheetFragment;
import com.manimaran.wikiaudio.listerners.CallBackListener;
import com.manimaran.wikiaudio.utils.PrefManager;
import com.manimaran.wikiaudio.utils.WikiLicense;

import java.util.Arrays;


public class SettingsActivity extends AppCompatActivity {

    private PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.settings));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView txtSpell4WikiLang = findViewById(R.id.txtSpell4WikiLang);
        TextView txtSpell4WordListLang = findViewById(R.id.txtSpell4WordListLang);
        TextView txtSpell4WordLang = findViewById(R.id.txtSpell4WordLang);
        TextView txtWiktionaryLang = findViewById(R.id.txtWiktionaryLang);
        TextView txtLicenseOfUploadAudio = findViewById(R.id.txtLicenseOfUploadAudio);
        TextView txtLicenseOfUploadAudioLegalCode = findViewById(R.id.txtLicenseOfUploadAudioLegalCode);


        View layoutSpell4WikiLang = findViewById(R.id.layoutSpell4WikiLang);
        View layoutSpell4WordListLang = findViewById(R.id.layoutSpell4WordListLang);
        View layoutSpell4WordLang = findViewById(R.id.layoutSpell4WordLang);
        View layoutWiktionaryLang = findViewById(R.id.layoutWiktionaryLang);
        View layoutLicenseOfUploadAudio = findViewById(R.id.layoutLicenseOfUploadAudio);


        pref = new PrefManager(getApplicationContext());
        txtSpell4WikiLang.setText(pref.getLanguageCodeSpell4Wiki());
        txtSpell4WordListLang.setText(pref.getLanguageCodeSpell4WordList());
        txtSpell4WordLang.setText(pref.getLanguageCodeSpell4Word());
        txtWiktionaryLang.setText(pref.getLanguageCodeWiktionary());

        layoutSpell4WikiLang.setOnClickListener(v -> {
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
            CallBackListener callback = langCode -> {
                pref.setLanguageCodeSpell4Wiki(langCode);
                txtSpell4WikiLang.setText(pref.getLanguageCodeSpell4Wiki());
            };
            bottomSheetFragment.setCalBack(callback);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            bottomSheetFragment.setCancelable(false);
        });

        layoutSpell4WordListLang.setOnClickListener(v -> {
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
            CallBackListener callback = langCode -> {
                pref.setLanguageCodeSpell4WordList(langCode);
                txtSpell4WordListLang.setText(pref.getLanguageCodeSpell4WordList());
            };
            bottomSheetFragment.setCalBack(callback);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            bottomSheetFragment.setCancelable(false);
            bottomSheetFragment.setIsTempMode(true);
        });


        layoutSpell4WordLang.setOnClickListener(v -> {
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
            CallBackListener callback = langCode -> {
                pref.setLanguageCodeSpell4Word(langCode);
                txtSpell4WordLang.setText(pref.getLanguageCodeSpell4Word());
            };
            bottomSheetFragment.setCalBack(callback);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            bottomSheetFragment.setCancelable(false);
            bottomSheetFragment.setIsTempMode(true);
        });

        layoutWiktionaryLang.setOnClickListener(v -> {
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
            CallBackListener callback = langCode -> {
                pref.setLanguageCodeWiktionary(langCode);
                txtWiktionaryLang.setText(pref.getLanguageCodeWiktionary());
            };
            bottomSheetFragment.setCalBack(callback);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            bottomSheetFragment.setCancelable(false);
            bottomSheetFragment.setIsTempMode(true);
        });

        updateLicenseView(txtLicenseOfUploadAudio, txtLicenseOfUploadAudioLegalCode);
        layoutLicenseOfUploadAudio.setOnClickListener(v -> {
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.hint_license_choose_alert);// add a radio button list

            String[] licensePrefList = {
                    WikiLicense.LicensePrefs.CC_0,
                    WikiLicense.LicensePrefs.CC_BY_3,
                    WikiLicense.LicensePrefs.CC_BY_SA_3,
                    WikiLicense.LicensePrefs.CC_BY_4,
                    WikiLicense.LicensePrefs.CC_BY_SA_4
            };

            String[] licenseList = {
                    getString(R.string.license_name_cc_zero),
                    getString(R.string.license_name_cc_by_three),
                    getString(R.string.license_name_cc_by_sa_three),
                    getString(R.string.license_name_cc_by_four),
                    getString(R.string.license_name_cc_by_sa_four)
            };

            int checkedItem = Arrays.asList(licensePrefList).indexOf(pref.getUploadAudioLicense());
            builder.setSingleChoiceItems(licenseList, checkedItem, (dialog, which) -> {
                pref.setUploadAudioLicense(licensePrefList[which]);
                updateLicenseView(txtLicenseOfUploadAudio, txtLicenseOfUploadAudioLegalCode);
                dialog.dismiss();
            });
            builder.setNegativeButton(getString(R.string.cancel), null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }

    private void updateLicenseView(TextView txtLicenseOfUploadAudio, TextView txtLicenseOfUploadAudioLegalCode) {
        txtLicenseOfUploadAudio.setText(WikiLicense.licenseNameId(pref.getUploadAudioLicense()));
        txtLicenseOfUploadAudioLegalCode.setMovementMethod(LinkMovementMethod.getInstance());
        String ccLegalInfo = "(<a href=" + WikiLicense.licenseUrlFor(pref.getUploadAudioLicense()) + "><font color='" + ContextCompat.getColor(getApplicationContext(), R.color.w_green) + "'>legal code</font></a>)";
        txtLicenseOfUploadAudioLegalCode.setText(Html.fromHtml(ccLegalInfo));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

}
