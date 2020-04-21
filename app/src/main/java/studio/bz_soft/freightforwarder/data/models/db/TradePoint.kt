package studio.bz_soft.freightforwarder.data.models.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trade_point")
class TradePoint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "sale_point") val storePoint: String,
    @ColumnInfo(name = "type") val type: String,                  // Enum ["ИП", "ООО", "ПАО"]
    @ColumnInfo(name = "tax_number") val taxNumber: String,       // ИНН
    @ColumnInfo(name = "tax_number_1") val taxNumber_1: String,   // ОГРН
    @ColumnInfo(name = "address_actual") val addressActual: String, // Фактический адрес
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "address_legal") val addressLegal: String,   // Юридический адрес
    @ColumnInfo(name = "contact_phone") val phone: String,
    @ColumnInfo(name = "contact_mail") val email: String,
    @ColumnInfo(name = "lpr") val lprName: String,
    @ColumnInfo(name = "payment_type") val paymentType: String,   // Enum ["Безнал. НДС", "Безнал. без НДС", "Наличные"]
    @ColumnInfo(name = "products_range") val productsRange: String,     // Enum []
    @ColumnInfo(name = "market_type") val marketType: String,     // Enum []
    @ColumnInfo(name = "company_type") val companyType: String,   // Enum []
    @ColumnInfo(name = "work_time") val workTime: String,
    @ColumnInfo(name = "dealer") val dealer: String,
    @ColumnInfo(name = "photo_outside") val photoOutside: String, // image url
    @ColumnInfo(name = "photo_inside") val photoInside: String,   // image url
    @ColumnInfo(name = "photo_goods") val photoGoods: String,     // image url
    @ColumnInfo(name = "photo_corner") val photoCorner: String
)