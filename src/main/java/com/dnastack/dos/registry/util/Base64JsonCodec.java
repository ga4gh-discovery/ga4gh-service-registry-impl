package com.dnastack.dos.registry.util;

import com.dnastack.dos.registry.exception.ServiceException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;

import java.io.IOException;
import java.util.Base64;
import java.util.function.Function;

/**
 * This utility class provides methods of encoding/decoding JSON objects
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class Base64JsonCodec {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Base64JsonCodec() {
    }

    public static <T> String encode(T t) {
        return encode(t, Functions::identity);
    }

    public static <T> String encode(T t, Function<byte[], byte[]> encrypter) {
        try {
            String json = MAPPER.writeValueAsString(t);
            byte[] transformed = (byte[])encrypter.apply(json.getBytes(Charsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(transformed);
        } catch (JsonProcessingException jpe) {
            throw new ServiceException(jpe);
        }
    }

    public static <T> T decode(String encoded, Class<T> type) {
        return decode(encoded, type, Functions::identity);
    }

    public static <T> T decode(String encoded, Class<T> type, Function<byte[], byte[]> decrypter) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(encoded);
            byte[] decrypted = (byte[])decrypter.apply(decoded);
            return MAPPER.readValue(decrypted, type);
        } catch (IOException ioe) {
            throw new ServiceException(ioe);
        }
    }

    static {
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS).configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }
}
