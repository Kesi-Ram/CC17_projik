package com.example.cc17_projik

import android.annotation.SuppressLint
import android.content.SharedPreferences

/** KTX edit returns Unit; these writes need the commit result before reporting success. */
@SuppressLint("UseKtx") // KTX edit discards the Boolean returned by commit().
inline fun SharedPreferences.commitChanges(update: SharedPreferences.Editor.() -> Unit): Boolean {
    val editor = edit()
    editor.update()
    return editor.commit()
}
