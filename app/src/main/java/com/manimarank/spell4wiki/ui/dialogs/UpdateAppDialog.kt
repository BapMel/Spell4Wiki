package com.manimarank.spell4wiki.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import com.manimarank.spell4wiki.R
import com.manimarank.spell4wiki.utils.pref.AppPref
import com.manimarank.spell4wiki.utils.GeneralUtils
import com.manimarank.spell4wiki.utils.constants.Urls

object UpdateAppDialog {


    fun show(activity: Activity) {
        try {
            if (AppPref.checkAppUpdateAvailable(activity)) {
                // Show Dialog
                val builder = AlertDialog.Builder(activity)
                builder.setTitle(R.string.update_dialog_title)
                builder.setMessage(R.string.update_dialog_message)
                builder.setCancelable(false)

                builder.setPositiveButton(R.string.update_dialog_ok) { dialog, _ ->
                    dialog.dismiss()
                    GeneralUtils.openUrlInBrowser(activity, Urls.APP_LINK)
                }

                builder.setNegativeButton(R.string.update_dialog_no) { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                AppPref.setUpdateShowed()
                dialog.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}