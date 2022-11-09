package vn.app.qrcode.di.module


import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import vn.app.qrcode.data.QrCodeDB
import vn.app.qrcode.data.dao.QrCodeDao
import vn.app.qrcode.data.usermanager.UserManager
import java.util.Date

val appModule = module {

    single { provideGson() }

    single { UserManager() }

    single { QrCodeDB.getDatabase(androidApplication()) }
    single { provideDao(get()) }

}

fun provideGson(): Gson {
    val builder = GsonBuilder()

    builder.registerTypeAdapter(Date::class.java, JsonDeserializer<Date> { json, _, _ ->
        json?.asJsonPrimitive?.asLong?.let {
            return@JsonDeserializer Date(it)
        }
    })

    builder.registerTypeAdapter(Date::class.java, JsonSerializer<Date> { date, _, _ ->
        JsonPrimitive(date.time)
    })

    return builder.create()
}

fun provideDataBase(application: Application): QrCodeDB {
    return Room.databaseBuilder(application, QrCodeDB::class.java, "qr_code_db")
        .build()
}

fun provideDao(dataBase: QrCodeDB): QrCodeDao {
    return dataBase.qrCodeDao
}