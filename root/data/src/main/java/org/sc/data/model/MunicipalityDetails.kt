package org.sc.data.model

data class MunicipalityDetails(
    var code: String,
    var city: String,
    var province: String,
    var provinceShort: String
)
{
    companion object {
        const val CODE = "code"
        const val CITY = "city"
        const val PROVINCE = "province"
        const val PROVINCE_SHORT = "provinceShort"
    }
}