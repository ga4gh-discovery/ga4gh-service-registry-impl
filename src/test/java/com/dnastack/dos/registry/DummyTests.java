package com.dnastack.dos.registry;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class servers as ...
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class DummyTests {

    @Test
    public void testJSONArray() {
        List<String> list = Stream.of("te4st1", "tst2", "test3").collect(Collectors.toList());

        JSONArray jsonArray = new JSONArray(list);
        System.out.println(jsonArray);

        String jsonArrayString = jsonArray.toString();
        System.out.println(jsonArrayString);
        JSONArray jsonArray1 = null;
        try {
            jsonArray1 = new JSONArray(jsonArrayString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonArray1);
        System.out.println(jsonArray1.length());
        try {
            System.out.println(jsonArray1.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGson(){
        List<String> list = Stream.of("te4st1", "tst2", "test3").collect(Collectors.toList());
        Gson objGson = new Gson();
        String s = objGson.toJson(list);

        System.out.println(s);

        List list1 = objGson.fromJson(s, List.class);

        System.out.println(list1);
    }

}
