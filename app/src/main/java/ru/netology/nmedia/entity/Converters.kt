package ru.netology.nmedia.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {
    @TypeConverter
    fun toJson(list: List<Int>) = Gson().toJson(list)

    @TypeConverter
    fun fromJson(json: String): List<Int> {
        return Gson().fromJson(json, object : TypeToken<List<Int>>() {}.type)
    }
//    class MapUsersPrevConverter {
//        @TypeConverter
//        fun toJson(map: Map<Int, UserPreview>) = Gson().toJson(map)
//
//        @TypeConverter
//        fun fromJson(json: String): Map<Int, UserPreview> {
//            return Gson().fromJson(json, object : TypeToken<Map<Int, UserPreview>>() {}.type)
//        }
//    }
}