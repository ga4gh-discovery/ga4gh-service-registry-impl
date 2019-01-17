package com.dnastack.discovery.registry.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

@JsonComponent
public class PageImplJacksonSerializer extends JsonSerializer<PageImpl> {

    @Override
    public void serialize(PageImpl page, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        writePageMetadata(gen, page);
        gen.writeEndObject();
    }

    private void writePageMetadata(JsonGenerator gen, PageImpl page) throws IOException {
        gen.writeObjectField("content", page.getContent());
        gen.writeBooleanField("first", page.isFirst());
        gen.writeBooleanField("last", page.isLast());
        gen.writeBooleanField("empty", page.isEmpty());
        gen.writeNumberField("totalPages", page.getTotalPages());
        gen.writeNumberField("totalElements", page.getTotalElements());
        gen.writeNumberField("numberOfElements", page.getNumberOfElements());
        gen.writeNumberField("size", page.getSize());
        gen.writeNumberField("number", page.getNumber());
        writeSortField(gen, page);
    }

    private void writeSortField(JsonGenerator gen, PageImpl page) throws IOException {
        gen.writeArrayFieldStart("sort");
        for (Sort.Order order : page.getSort()) {
            gen.writeStartObject();
            gen.writeStringField("property", order.getProperty());
            gen.writeStringField("direction", order.getDirection().name());
            gen.writeBooleanField("ignoreCase", order.isIgnoreCase());
            gen.writeStringField("nullHandling", order.getNullHandling().name());
            gen.writeEndObject();
        }
        gen.writeEndArray();
    }
}
