package me.oriient.backendlogger.services.android

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.annotation.Keep
import androidx.annotation.RestrictTo

internal val context: Context
    get() { return _context }
@Suppress("ObjectPropertyName")
private lateinit var _context: Context

@Keep
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class BackendLoggerInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        Log.d(TAG, "onCreate() called")
        _context = context!!.applicationContext
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    companion object {
        @Suppress("ObjectPropertyName")
        private val TAG = BackendLoggerInitProvider::class.java.simpleName
    }
}