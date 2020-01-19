package com.manimaran.wikiaudio.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;

import com.manimaran.wikiaudio.R;
import com.manimaran.wikiaudio.constant.Constants;
import com.manimaran.wikiaudio.utils.GeneralUtils;
import com.manimaran.wikiaudio.utils.PrefManager;


public class Spell4WordActivity extends AppCompatActivity {

    private EditText editSpell4Word;
    private Button btnRecord;

    private PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_4_word);

        setTitle();

        pref = new PrefManager(this);

        btnRecord = findViewById(R.id.btn_record);
        editSpell4Word = findViewById(R.id.editWord);

        editSpell4Word.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editSpell4Word.getRight() - editSpell4Word.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // your action here
                    if (!TextUtils.isEmpty(editSpell4Word.getText()) && editSpell4Word.getText().length() < 30)
                        openWikitionaryPage(editSpell4Word.getText().toString());
                    else
                        GeneralUtils.showSnack(editSpell4Word, "Enter valid word");
                    return true;
                }
            }
            return false;
        });


        btnRecord.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(editSpell4Word.getText()) && editSpell4Word.getText().length() < 30) {
                GeneralUtils.showRecordDialog(Spell4WordActivity.this, editSpell4Word.getText().toString().trim());
            } else
                GeneralUtils.showSnack(editSpell4Word, "Enter valid word");
        });
    }

    private void openWikitionaryPage(String wordInfo) {
        Intent intent = new Intent(getApplicationContext(), CommonWebActivity.class);
        String url = String.format(getString(R.string.url_wiktionary_web), pref.getContributionLangCode(), wordInfo);
        intent.putExtra(Constants.TITLE, wordInfo);
        intent.putExtra(Constants.URL, url);
        intent.putExtra(Constants.IS_CONTRIBUTION_MODE, true);
        intent.putExtra(Constants.IS_WIKTIONARY_WORD, true);
        startActivity(intent);
    }


    private void setTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.spell4word));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setSubtitle(ApiClient.getUrl(UrlType.WIKTIONARY_PAGE, getApplicationContext()));
        }
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

