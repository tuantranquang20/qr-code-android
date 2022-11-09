package vn.app.qrcode.ui.home.camera

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.CommonColumns
import android.provider.ContactsContract.CommonDataKinds.Event
import android.provider.ContactsContract.CommonDataKinds.Website
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.barcode.common.Barcode
import timber.log.Timber
import vn.app.qrcode.data.constant.LINK_BROWSER_FACEBOOK

object CameraHelper {
    fun setIntentToFacebook(activity: Activity, url: String): Intent? {
        var intent: Intent? = null
        try {
            val applicationInfo: ApplicationInfo =
                activity.packageManager.getApplicationInfo(
                    "com.facebook.katana",
                    0
                )
            if (applicationInfo.enabled) {
                val uri = LINK_BROWSER_FACEBOOK + url
                intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            }
        } catch (ignored: PackageManager.NameNotFoundException) {
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        }
        return intent
    }

    fun setIntentToBrowser(url: String): Intent {
        return Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setIntentToConnectWifi(ssid: String, pass: String, type: Int): Intent? {
        var intent: Intent? = null
        try {
            var suggestion: WifiNetworkSuggestion? = null
            when (type) {
                Barcode.WiFi.TYPE_OPEN -> {
                    suggestion =
                        WifiNetworkSuggestion.Builder()
                            .setSsid(ssid)
                            .build()
                }
                Barcode.WiFi.TYPE_WPA -> {
                    suggestion =
                        WifiNetworkSuggestion.Builder()
                            .setSsid(ssid)
                            .setWpa2Passphrase(pass)
                            .build()
                }

            }

            val bundle = Bundle()
            bundle.putParcelableArrayList(
                Settings.EXTRA_WIFI_NETWORK_LIST,
                arrayListOf(suggestion)
            )
            intent = Intent(Settings.ACTION_WIFI_ADD_NETWORKS);
            intent.putExtras(bundle)
        } catch (e: Exception) {
            Timber.d(e)
        }
        return intent
    }

    fun setIntentToContact(
        name: String,
        phoneNumber: String,
        email: String,
        address: String,
        jobTitle: String,
        url: String,
        birthday: String,
        note: String
    ): Intent {
        //Open Contact app
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
        }
        val data = ArrayList<ContentValues>()
        data.add(addRowForContact(
            dataColumns = ContactsContract.Data.MIMETYPE,
            dataKinds = Website.CONTENT_ITEM_TYPE,
            fieldName = Website.URL,
            fieldValue = url,
            typeColumns = Website.TYPE,
            typeKinds = Website.TYPE_HOME
        ))
        data.add(addRowForContact(
            dataColumns = ContactsContract.Data.MIMETYPE,
            dataKinds = Event.CONTENT_ITEM_TYPE,
            fieldName = Event.START_DATE,
            fieldValue = birthday,
            typeColumns = Event.TYPE,
            typeKinds = Event.TYPE_BIRTHDAY
        ))

        intent.apply {
            // Inserts an email address
            putExtra(ContactsContract.Intents.Insert.NAME, name)
            putExtra(ContactsContract.Intents.Insert.EMAIL, email)
            putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber)
            putExtra(ContactsContract.Intents.Insert.POSTAL, address)
            putExtra(ContactsContract.Intents.Insert.JOB_TITLE, jobTitle)
            putExtra(ContactsContract.Intents.Insert.NOTES, note)
            putExtra(ContactsContract.Intents.Insert.DATA, data)
        }
        return intent
    }

    private fun addRowForContact(
        dataColumns: String,
        dataKinds: String,
        fieldValue: String,
        fieldName: String,
        typeColumns: String,
        typeKinds: Int
    ): ContentValues {
        val row = ContentValues()
        row.put(dataColumns, dataKinds)
        row.put(fieldName, fieldValue)
        row.put(typeColumns, typeKinds)
        return row
    }
}