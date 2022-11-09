package vn.app.qrcode.data.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "qr_code")
open class QRCodeCreator(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @NonNull @ColumnInfo(name = "type")
    var type: Int = -1,
    @NonNull @ColumnInfo(name = "display_name")
    var displayName: String = "",
    @NonNull @ColumnInfo(name = "raw_value")
    var rawValue: String = "",
    @NonNull @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean = false,
    @NonNull @ColumnInfo(name = "created_at")
    var createdAt: Long = Date().time,
    @ColumnInfo(name = "last_scan")
    var lastScan: Long? = null,
    @ColumnInfo(name = "created_by")
    var createdBy: Int? = null,
    @NonNull @ColumnInfo(name = "hash_code")
    var hashCode: String = "",
    @NonNull @ColumnInfo(name = "content")
    var content: String = "",
): Parcelable
