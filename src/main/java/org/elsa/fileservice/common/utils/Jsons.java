package org.elsa.fileservice.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author valor
 * @date 2018/9/25 16:48
 */
public class Jsons {

    private static final Gson GSON = new Gson();

    /**
     * 将驼峰转为下划线
     */
    private static final Gson GSON_UNDERSCORES = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    /**
     * 不允许外部实例化
     */
    private Jsons() { }

    /**
     * format entity to json.
     *
     * @return String 「Json」
     */
    public static String toJson(Object o) {
        return GSON.toJson(o);
    }

    /**
     * format entity to json with underscores.
     *
     * @return String 「Json」
     */
    public static String toJsonUnderscores(Object o) {
        return GSON_UNDERSCORES.toJson(o);
    }

    /**
     * parse json and get child string by key.
     *
     * @return String 「value」
     */
    public static Integer parseGetInteger(String json, String key) {
        JSONObject o = JSON.parseObject(json);
        return o.getInteger(key);
    }

    /**
     * parse json and get child string by key.
     *
     * @return String 「value」
     */
    public static String parseGetString(String json, String key) {
        JSONObject o = JSON.parseObject(json);
        return o.getString(key);
    }

    /**
     * parse json and get child entity by key.
     *
     * @return T 「entity」
     */
    public static <T> T parseGetObjectEntity(String json, String key, Class<T> clazz) {
        return parseObject(parseGetString(json, key), clazz);
    }

    /**
     * parse json and get child list entity by key.
     *
     * @return List<T> 「entity」
     */
    public static <T> List<T> parseGetArrayEntity(String json, String key, Class<T> clazz) {
        return parseArray(parseGetString(json, key), clazz);
    }

    /**
     * parse json to map.
     *
     * @return Map<String, T> 「map」
     */
    public static <T> Map<String, T> parseObject(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, T>>() {});
    }

    /**
     * parse json by class.
     *
     * @return T 「entity」
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * parse json by class.
     *
     * @return T 「entity」
     */
    public static <T> T parseObject(Object o, Class<T> clazz) {
        return JSON.parseObject(toJson(o), clazz);
    }

    /**
     * parse json to list of map.
     *
     * @return List<Map<String, T>> 「map」
     */
    public static <T> List<Map<String, T>> parseArray(String json) {
        Type type = new TypeToken<List<Map<String, T>>>() {}.getType();
        return GSON.fromJson(json, type);
    }

    /**
     * parse json by class.
     *
     * @return List<T> 「entity」
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    /**
     * parse json by class.
     *
     * @return List<T> 「entity」
     */
    public static <T> List<T> parseArray(Object o, Class<T> clazz) {
        return JSON.parseArray(toJson(o), clazz);
    }
}
