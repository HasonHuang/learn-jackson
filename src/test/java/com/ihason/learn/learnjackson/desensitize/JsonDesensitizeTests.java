package com.ihason.learn.learnjackson.desensitize;

import cn.hutool.core.annotation.AnnotationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ihason.learn.learnjackson.desensitize.annotation.JsonDesensitize;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * {@link JsonDesensitize} test cases
 *
 * @author Huanghs
 * @since 2.0
 * @date 2023-07-04
 */
public class JsonDesensitizeTests {

    static JsonMapper mapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    /**
     * 标记测试用例使用过的注解
     */
    static Annotation desensitizeAnno;

    /**
     * 测试用例修改前的注解值
     */
    static Map<String, Object> personDesensitizeAnnoValues;

    @BeforeEach
    void setup() throws NoSuchFieldException {
        desensitizeAnno = null;
        personDesensitizeAnnoValues = AnnotationUtil.getAnnotationValueMap(Person.class.getDeclaredField("value"), JsonDesensitize.class);
    }

    @AfterEach
    void teardown() {
        if (desensitizeAnno != null) {
            // 恢复测试用例前的注解状态
            for (Map.Entry<String, Object> entry : personDesensitizeAnnoValues.entrySet()) {
                AnnotationUtil.setValue(desensitizeAnno, entry.getKey(), entry.getValue());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "abc", "123", "   ", " " })
    void should_hide_when_all_given_strings(String text) throws JsonProcessingException, JSONException, NoSuchFieldException {
        // given
        Person person = new Person(text);
        setStrategy(person, JsonDesensitize.Strategy.ALL);
        // when
        String json = toJson(person);
        // then
        String expected = String.format("{value: '%s'}", repeat(text.length()));
        JSONAssert.assertEquals(expected, json, true);
    }

    @ParameterizedTest
    @ValueSource(strings = { "huang@ihason.com", "hs@ihason.com" })
    void should_hide_when_email_given_strings(String text) throws JsonProcessingException, JSONException, NoSuchFieldException {
        // given
        Person person = new Person(text);
        setStrategy(person, JsonDesensitize.Strategy.EMAIL);
        // when
        String json = toJson(person);
        // then
        String expected = String.format("{value: 'h%s@ihason.com'}", repeat(text.length() - "h@ihason.com".length()));
        JSONAssert.assertEquals(expected, json, true);
    }

    @ParameterizedTest
    @ValueSource(strings = { "huang@ihason.com", "hs@ihason.com" })
    void should_hide_when_custom_given_strings(String text) throws JsonProcessingException, JSONException, NoSuchFieldException {
        // given
        Person person = new Person(text);
        setStrategy(person, JsonDesensitize.Strategy.CUSTOM);
        setHandler(person, StubDesensitizer.class);
        // when
        String json = toJson(person);
        // then
        JSONAssert.assertEquals("{value:  'mock'}", json, true);
    }

    private static void setStrategy(Person person, JsonDesensitize.Strategy strategy) throws NoSuchFieldException {
        setAnnotationValue(person, "strategy", strategy);
    }

    private static void setValue(Person person, String value) throws NoSuchFieldException {
        setAnnotationValue(person, "value", value);
    }

    private static void setHandler(Person person, Class<?> clazz) throws NoSuchFieldException {
        setAnnotationValue(person, "handler", clazz);
    }

    private static void setAnnotationValue(Object object, String annotationField, Object annotationFieldValue) throws NoSuchFieldException {
        setAnnotationValue(object, "value", annotationField, annotationFieldValue);
    }

    private static void setAnnotationValue(Object object, String property, String annotationField, Object annotationFieldValue) throws NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(property);
        JsonDesensitize annotation = field.getAnnotation(JsonDesensitize.class);
        desensitizeAnno = annotation;
        AnnotationUtil.setValue(annotation, annotationField, annotationFieldValue);
    }

    private String toJson(Person person) throws JsonProcessingException {
        return mapper.writeValueAsString(person);
    }

    private String repeat(int size) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append("*");
        }
        return builder.toString();
    }

}
