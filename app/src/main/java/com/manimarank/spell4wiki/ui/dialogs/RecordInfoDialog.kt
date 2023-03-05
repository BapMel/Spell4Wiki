package com.manimarank.spell4wiki.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import com.manimarank.spell4wiki.R
import com.manimarank.spell4wiki.data.prefs.AppPref
import com.manimarank.spell4wiki.data.prefs.PrefManager
import com.manimarank.spell4wiki.utils.WikiLicense
import kotlinx.android.synthetic.main.activity_record_audio_pop_up.*
import kotlinx.android.synthetic.main.activity_settings.*

object RecordInfoDialog {


    fun show(activity: Activity) {
        try {
            if (!AppPref.getRecordInfoShowed()) {
                // Show Dialog
                val builder = AlertDialog.Builder(activity)
                builder.setTitle(R.string.record_info_dialog_title)
                builder.setMessage(R.string.record_info_dialog_message)
                builder.setCancelable(false)

                builder.setPositiveButton(R.string.record_info_dialog_ok) { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                AppPref.setRecordInfoShowed()
                dialog.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}