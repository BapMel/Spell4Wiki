package com.manimarank.spell4wiki.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.manimarank.spell4wiki.R;
import com.manimarank.spell4wiki.ui.common.BaseActivity;
import com.manimarank.spell4wiki.ui.dialogs.AppLanguageDialog;
import com.manimarank.spell4wiki.utils.constants.ListMode;
import com.manimarank.spell4wiki.databases.DBHelper;
import com.manimarank.spell4wiki.databases.dao.WikiLangDao;
import com.manimarank.spell4wiki.databases.entities.WikiLang;
import com.manimarank.spell4wiki.fragments.LanguageSelectionFragment;
import com.manimarank.spell4wiki.listerners.OnLanguageSelectionListener;
import com.manimarank.spell4wiki.utils.pref.PrefManager;
import com.manimarank.spell4wiki.utils.WikiLicense;

import java.util.Arrays;


public class SettingsActivity extends BaseActivity {

    private PrefManager pref;
    private WikiLangDao wikiLangDao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.settings));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView txtTitleLicense = findViewById(R.id.txtTitleLicense);
        TextView txtSpell4WikiLang = findViewById(R.id.txtSpell4WikiLang);
        TextView txtSpell4WordListLang = findViewById(R.id.txtSpell4WordListLang);
        TextView txtSpell4WordLang = findViewById(R.id.txtSpell4WordLang);
        TextView txtWiktionaryLang = findViewById(R.id.txtWiktionaryLang);
        TextView txtLicenseOfUploadAudio = findViewById(R.id.txtLicenseOfUploadAudio);
        TextView txtLicenseOfUploadAudioLegalCode = findViewById(R.id.txtLicenseOfUploadAudioLegalCode);
        TextView txtAppLanguage = findViewById(R.id.txtAppLanguage);
        SwitchCompat switchAbortAlertStatus = findViewById(R.id.switchAbortAlerts);
        TextView txtAbortAlertStatus = findViewById(R.id.txtAbortAlertStatus);

        View layoutSpell4WikiLang = findViewById(R.id.layoutSpell4WikiLang);
        View layoutSpell4WordListLang = findViewById(R.id.layoutSpell4WordListLang);
        View layoutSpell4WordLang = findViewById(R.id.layoutSpell4WordLang);
        View layoutWiktionaryLang = findViewById(R.id.layoutWiktionaryLang);
        View layoutLicenseOfUploadAudio = findViewById(R.id.layoutLicenseOfUploadAudio);
        View layoutLanguageOfApp = findViewById(R.id.layoutLanguageOfApp);
        View layoutAbortDialog = findViewById(R.id.layoutAbortAlert);

        pref = new PrefManager(getApplicationContext());
        wikiLangDao = DBHelper.getInstance(getApplicationContext()).getAppDatabase().getWikiLangDao();
        if (pref.isAnonymous()) {
            txtTitleLicense.setVisibility(View.GONE);
            layoutSpell4WikiLang.setVisibility(View.GONE);
            layoutSpell4WordListLang.setVisibility(View.GONE);
            layoutSpell4WordLang.setVisibility(View.GONE);
            layoutLicenseOfUploadAudio.setVisibility(View.GONE);
        }

        updateLanguageView(txtSpell4WikiLang, pref.getLanguageCodeSpell4Wiki());
        updateLanguageView(txtSpell4WordListLang, pref.getLanguageCodeSpell4WordList());
        updateLanguageView(txtSpell4WordLang, pref.getLanguageCodeSpell4Word());
        updateLanguageView(txtWiktionaryLang, pref.getLanguageCodeWiktionary());

        layoutSpell4WikiLang.setOnClickListener(v -> {
            OnLanguageSelectionListener callback = langCode -> updateLanguageView(txtSpell4WikiLang, pref.getLanguageCodeSpell4Wiki());
            LanguageSelectionFragment languageSelectionFragment = new LanguageSelectionFragment(this);
            languageSelectionFragment.init(callback, ListMode.SPELL_4_WIKI);
            languageSelectionFragment.show(getSupportFragmentManager());
        });

        layoutSpell4WordListLang.setOnClickListener(v -> {
            OnLanguageSelectionListener callback = langCode -> updateLanguageView(txtSpell4WordListLang, pref.getLanguageCodeSpell4WordList());
            LanguageSelectionFragment languageSelectionFragment = new LanguageSelectionFragment(this);
            languageSelectionFragment.init(callback, ListMode.SPELL_4_WORD_LIST);
            languageSelectionFragment.show(getSupportFragmentManager());
        });


        layoutSpell4WordLang.setOnClickListener(v -> {
            OnLanguageSelectionListener callback = langCode -> updateLanguageView(txtSpell4WordLang, pref.getLanguageCodeSpell4Word());
            LanguageSelectionFragment languageSelectionFragment = new LanguageSelectionFragment(this);
            languageSelectionFragment.init(callback, ListMode.SPELL_4_WORD);
            languageSelectionFragment.show(getSupportFragmentManager());
        });

        layoutWiktionaryLang.setOnClickListener(v -> {
            OnLanguageSelectionListener callback = langCode -> updateLanguageView(txtWiktionaryLang, pref.getLanguageCodeWiktionary());
            LanguageSelectionFragment languageSelectionFragment = new LanguageSelectionFragment(this);
            languageSelectionFragment.init(callback, ListMode.WIKTIONARY);
            languageSelectionFragment.show(getSupportFragmentManager());
        });

        updateLicenseView(txtLicenseOfUploadAudio, txtLicenseOfUploadAudioLegalCode);
        layoutLicenseOfUploadAudio.setOnClickListener(v -> {
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.license_choose_alert);// add a radio button list

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

        txtAppLanguage.setText(AppLanguageDialog.INSTANCE.getSelectedLanguage());
        layoutLanguageOfApp.setOnClickListener(v -> AppLanguageDialog.INSTANCE.show(SettingsActivity.this));

        updateAbortAlertStatus(switchAbortAlertStatus, txtAbortAlertStatus, pref.getAbortAlertStatus());
        layoutAbortDialog.setOnClickListener(view -> updateAbortAlertStatus(switchAbortAlertStatus, txtAbortAlertStatus, !pref.getAbortAlertStatus()));
        switchAbortAlertStatus.setOnCheckedChangeListener((compoundButton, show) -> updateAbortAlertStatus(switchAbortAlertStatus, txtAbortAlertStatus, show));
    }

    private void updateAbortAlertStatus(SwitchCompat switchAbortAlertStatus, TextView txtAbortAlertStatus, Boolean show) {
        switchAbortAlertStatus.setChecked(show);
        pref.setAbortAlertStatus(show);
        txtAbortAlertStatus.setText(getString(show ? R.string.show_exit_alerts_in_spell4wiki_options : R.string.hide_exit_alerts_in_spell4wiki_options));
    }

    private void updateLanguageView(TextView txtView, String languageCode) {
        if (languageCode != null && txtView != null && wikiLangDao != null) {
            WikiLang wikiLang = wikiLangDao.getWikiLanguageWithCode(languageCode);
            String value = "";
            if (wikiLang != null && !TextUtils.isEmpty(wikiLang.getName()))
                value = wikiLang.getLocalName() + " - " + wikiLang.getName() + " : " + languageCode;
            txtView.setText(value);
        }
    }

    private void updateLicenseView(TextView txtLicenseOfUploadAudio, TextView txtLicenseOfUploadAudioLegalCode) {
        txtLicenseOfUploadAudio.setText(getString(WikiLicense.licenseNameId(pref.getUploadAudioLicense())));
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
