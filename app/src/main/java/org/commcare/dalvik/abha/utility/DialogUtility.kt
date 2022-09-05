package org.commcare.dalvik.abha.utility

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import org.commcare.dalvik.abha.R

object DialogUtility {

    fun showDialog(context:Context , msg:String){
        MaterialAlertDialogBuilder(context)
            .setTitle(context.resources.getString(R.string.app_name))
            .setMessage(msg)
            .setNeutralButton(context.resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setNegativeButton(context.resources.getString(R.string.decline)) { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton(context.resources.getString(R.string.accept)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
}