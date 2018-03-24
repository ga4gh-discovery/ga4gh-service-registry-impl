package com.dnastack.dos.registry.util;

import com.dnastack.dos.registry.model.KeyValuePair;
import com.google.gson.Gson;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class servers as ...
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class KeyValuePairFromStringTest {

    private final static Gson gson = new Gson();
    @Test
    public void testKeyValuePairGeneration() {

        String json1 = "{\"key\":\"category\", \"value\": \"cancer\"}";
        String json2 = "{\"key\":\"kind\", \"value\": \"kids\"}";

        List<String> stringInput = Arrays.asList(json1, json2);

        //form teh meta object
        LinkedHashMap<String, String> meta = stringInput.stream()
                .map(m -> {
                    return gson.fromJson(m, KeyValuePair.class);
                })
                .collect(Collectors.toMap(KeyValuePair::getKey, KeyValuePair::getValue,
                        (oldValue, newValue) -> oldValue,       // if same key, take the old key
                        LinkedHashMap::new
                ));

        System.out.println(meta);
    }

}
