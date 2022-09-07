package org.commcare.dalvik.abha.utility

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.commcare.dalvik.abha.R

object DialogUtility {

    fun showDialog(context: Context, msg: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.resources.getString(R.string.app_name))
            .setCancelable(false)
            .setMessage(msg)
            .setPositiveButton(context.resources.getString(R.string.ok)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    fun showDialog(context: Context, msg: String ,actionPositive:()->Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.resources.getString(R.string.app_name))
            .setCancelable(false)
            .setMessage(msg)
            .setPositiveButton(context.resources.getString(R.string.ok)) { dialog, which ->
                actionPositive.invoke()
            }
            .show()
    }
}