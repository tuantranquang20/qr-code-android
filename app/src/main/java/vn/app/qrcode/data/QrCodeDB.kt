package vn.app.qrcode.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import vn.app.qrcode.data.constant.DATABASE_CREATOR_NAME
import vn.app.qrcode.data.dao.QrCodeDao
import vn.app.qrcode.data.model.QRCodeCreator

@Database(entities = [QRCodeCreator::class], version = 1, exportSchema = false)
abstract class QrCodeDB: RoomDatabase() {
    abstract val qrCodeDao: QrCodeDao

    companion object {
        @Volatile
        private var INSTANCE: QrCodeDB? = null
        fun getDatabase(context: Context): QrCodeDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QrCodeDB::class.java,
                    DATABASE_CREATOR_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

