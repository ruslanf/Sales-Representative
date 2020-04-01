package studio.bz_soft.freightforwarder.data.models

import com.google.gson.annotations.SerializedName

data class StorePointModel(
    @SerializedName("sale_point") val storePoint: String?,
    @SerializedName("type") val type: String?,                  // Enum ["ИП", "ООО", "ПАО"]
    @SerializedName("tax_number") val taxNumber: String?,       // ИНН
    @SerializedName("tax_number_1") val taxNumber_1: String?,   // ОГРН
    @SerializedName("address_actual") val addressActual: String?, // Фактический адрес
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("address_legal") val addressLegal: String?,   // Юридический адрес
    @SerializedName("contact_phone") val phone: String?,
    @SerializedName("contact_mail") val email: String?,
    @SerializedName("lpr") val lprName: String?,
    @SerializedName("payment_type") val paymentType: String?,   // Enum ["Безнал. НДС", "Безнал. без НДС", "Наличные"]
    @SerializedName("products_range") val productsRange: String?,     // Enum []
    @SerializedName("market_type") val marketType: String?,     // Enum []
    @SerializedName("company_type") val companyType: String?,   // Enum []
    @SerializedName("work_time") val workTime: String?,
    @SerializedName("dealer") val dealer: String?,
    @SerializedName("photo_outside") val photoOutside: String?, // image url
    @SerializedName("photo_inside") val photoInside: String?,   // image url
    @SerializedName("photo_goods") val photoGoods: String?,     // image url
    @SerializedName("photo_corner") val photoCorner: String?
)