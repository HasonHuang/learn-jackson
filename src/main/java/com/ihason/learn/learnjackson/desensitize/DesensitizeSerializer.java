package com.ihason.learn.learnjackson.desensitize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.ihason.learn.learnjackson.desensitize.annotation.JsonDesensitize;

import java.io.IOException;
import java.util.Objects;

/**
 * 对值进行脱敏的序列化器
 *
 * @author Huanghs
 * @since 2.0
 * @date 2023-07-04
 */
public class DesensitizeSerializer extends JsonSerializer<CharSequence> implements ContextualSerializer {

    private final JsonDesensitize annotation;

    public DesensitizeSerializer() {
        // for jackson
        this(null);
    }

    public DesensitizeSerializer(JsonDesensitize annotation) {
        this.annotation = annotation;
    }

    @Override
    public void serialize(CharSequence value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeString(annotation.strategy().apply(value, annotation));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        JsonDesensitize annotation = beanProperty.getAnnotation(JsonDesensitize.class);
        if (!beanProperty.getType().isTypeOrSubTypeOf(CharSequence.class)) {
            // 不支持的数据类型
            throw new UnsupportedOperationException("Unsupported desensitization type: " + beanProperty.getType() + ", name: " + beanProperty.getFullName());
        }
        Objects.requireNonNull(annotation, "Unable to find any annotation of " + JsonDesensitize.class);
        return new DesensitizeSerializer(annotation);
    }
}
