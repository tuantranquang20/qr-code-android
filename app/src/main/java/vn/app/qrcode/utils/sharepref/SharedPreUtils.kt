package vn.app.qrcode.utils.sharepref

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.preference.PreferenceManager
import vn.app.qrcode.AppApplication

object SharedPreUtils {

    private val SHARED_PREFERENCES = "preferences"

    val prefs: SharedPreferences
        get() = getPrefs(AppApplication.instance!!)

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppApplication.instance!! /* Activity context */)

    fun isVibrate(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun setVibrate(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun getString(context: Context, key: String): String? {
        return getPrefs(context).getString(key, "")
    }

    fun getString(key: String): String? {
        return prefs.getString(key, "")
    }

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return prefs.getLong(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun getListString(key: String): ArrayList<String> {
        val myList = TextUtils.split(prefs.getString(key, ""), "‚‗‚")
        val newList = ArrayList<String>()
        for (aMyList in myList) {
            newList.add(aMyList)
        }
        return newList
    }

    fun putListString(key: String, list: ArrayList<String>) {
        val myIntList = list.toTypedArray()
        prefs.edit().putString(key, TextUtils.join("‚‗‚", myIntList)).apply()
    }

    fun clearPrefs() {
        prefs.edit().clear().apply()
    }

}
