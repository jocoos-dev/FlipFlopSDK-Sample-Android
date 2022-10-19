package com.jocoos.flipflop.sample.util

import com.google.gson.*
import com.jocoos.flipflop.api.model.Video
import com.jocoos.flipflop.api.model.VideoGoods
import java.lang.reflect.Type
import java.util.*

class DateAdapter : JsonSerializer<Date?>, JsonDeserializer<Date> {
    override fun serialize(src: Date?, type: Type, context: JsonSerializationContext): JsonElement? {
        return if (src == null) {
            null
        } else JsonPrimitive(src.time)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        type: Type,
        context: JsonDeserializationContext
    ): Date {
        return Date(json.asJsonPrimitive.asLong)
    }
}

object Jsons {
    private var GSON: Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES) //.setExclusionStrategies(new AnnotationExclusionStrategy(JsonIgnore.class))
        .registerTypeAdapter(
            Date::class.java,
            DateAdapter()
        ) //.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory())
        .create()

    fun gson(): Gson {
        return GSON
    }

    fun toString(target: Any?): String {
        return GSON.toJson(target)
    }

    fun <T> toModel(content: String?, clazz: Class<T>?): T {
        return GSON.fromJson(content, clazz)
    }
}
// string to video goods
fun <T> toVideoGoodsList(videos: List<Video>, classOfT: Class<T>): List<VideoGoods<T>> {
    return videos.map {
        val goods: T? = if (it.data.isNotEmpty()) {
            try {
                Jsons.gson().fromJson(it.data, classOfT)
            } catch (e: JsonSyntaxException) {
                null
            }
        } else {
            null
        }
        if (it.userName == null) {
            it.userName = it.user?.username
        }
        VideoGoods(it, goods)
    }
}

fun <T> dataToJson(dataInfo: T): String {
    return try {
        Jsons.gson().toJson(dataInfo)
    } catch (e: JsonIOException) {
        ""
    }
}
