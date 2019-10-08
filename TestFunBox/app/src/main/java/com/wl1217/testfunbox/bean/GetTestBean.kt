package net.winsoft.yzfc.bean

data class GetTestBean(
    val info: GetTestBean_Info,
    val msg: String,
    val success: Boolean
)

data class GetTestBean_Info(
    val phone: String
)