package org.sc.data.helper;

import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JsonHelper {
    private static String toJson(Object object) {
        return new Gson().toJson(object);
    }
    public static ResponseTransformer json() {
        return JsonHelper::toJson;
    }
}
