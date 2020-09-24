package uk.ac.ed.inf.backend;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

public class JsonParser {

    private static final Gson gson = new Gson();

    public static JsonObject readAsObj(String data) throws IOException {
        return JsonParser.gson.fromJson(data, JsonObject.class);
    }

    public static JsonArray readAsArr(String data) throws IOException {
        return JsonParser.gson.fromJson(data, JsonArray.class);
    }
}
