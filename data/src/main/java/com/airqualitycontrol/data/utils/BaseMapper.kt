package com.airqualitycontrol.data.utils

interface BaseMapper<OUTPUT> {
    fun mapToDomain(): OUTPUT
}
