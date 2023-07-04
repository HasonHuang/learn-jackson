package com.ihason.learn.learnjackson.desensitize.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ihason.learn.learnjackson.desensitize.DesensitizeSerializer;
import com.ihason.learn.learnjackson.desensitize.DesensitizeStrategies;
import com.ihason.learn.learnjackson.desensitize.Desensitizer;

import java.lang.annotation.*;

/**
 * 脱敏注解，对被标注的属性值进行脱敏
 *
 * @author Huanghs
 * @since 2.0
 * @date 2023-07-04
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = DesensitizeSerializer.class)
public @interface JsonDesensitize {

    /**
     * 脱敏策略，将策略匹配的内容替换为 {@link #replacement()} 指定的符号。
     */
    Strategy strategy();

    /**
     * 脱敏策略值，与 {@link #strategy()} 搭配使用，提供参数给脱敏器。
     *
     * <p>示例一：使用正则表达式，对包含 abc 的字符串脱敏。</p>
     * <pre>
     *     // 对于 name = "hey, abc!"，脱敏结果为："hey,*!"
     *     {@code @JsonDesensitize(strategy = Strategy.REGEX, value = "abc")}
     *     private String name;
     * </pre>
     */
    String value() default "";

    /**
     * 替换敏感内容的字符，默认值 {@code *} 符号。
     *
     * <p>如果此值是单字符，每个敏感字符替换为此字符。即 N 个敏感字符将会替换为 N 个符号。</p>
     * <p>如果此值是多字符，全部敏感字符替换为此字符。即 N 个敏感字符将会替换为 1 个符号集。</p>
     *
     * <p>如果 {@link #strategy()} 是 {@link Strategy#REGEX} 时，此值与 {@link String#replaceAll(String, String)} 的效果相同。</p>
     */
    String replacement() default "*";

    /**
     * 是否重复 {@link #replacement()} 的符号，此属性只对单字符生效。
     */
    boolean repeat() default true;

    /**
     * 使用指定的脱敏器进行脱敏，被指定的类必须提供一个无参构造函数。
     *
     * <p>注意：仅当 {@link #strategy()} 等于 {@link Strategy#CUSTOM} 时生效。</p>
     */
    Class<? extends Desensitizer> handler() default Desensitizer.class;

    /**
     * 脱敏策略
     */
    enum Strategy implements Desensitizer {

        /**
         * 全部脱敏
         *
         * <p>例子：</p>
         * <pre>
         *     null  -> null
         *     ""    -> ""
         *     " "   -> *
         *     smile -> *****
         * </pre>
         */
        ALL(DesensitizeStrategies::all),

        /**
         * 地址
         *
         * <p>脱敏最后 8 个字符，不足 8 个字符时全部脱敏</p>
         *
         * <p>例子：</p>
         * <pre>
         *     null  -> null
         *     ""    -> ""
         *     " "   -> *
         *     广东省深圳市 -> ******
         *     广东省深圳市南山区高新园万象天地 -> 广东省深圳市南山********
         * </pre>
         */
        ADDRESS(DesensitizeStrategies::address),

        /**
         * 自定义脱敏。需要同时通过 {@link #handler()} 指定脱敏器
         */
        CUSTOM(DesensitizeStrategies::custom),

        /**
         * 电子邮件地址
         *
         * <p>对 @ 左侧的字符脱敏，保留第一个字符</p>
         *
         * <p>例子：</p>
         * <pre>
         *     null               -> null
         *     ""                 -> ""
         *     " "                -> " "
         *     h@ihason.com       -> h@ihason.com
         *     huanghs@ihason.com -> h******@ihason.com
         * </pre>
         */
        EMAIL(DesensitizeStrategies::email),

        /**
         * 正则表达式，将匹配到的内容作为敏感内容处理。
         *
         * <p>使用 {@link #value()} 表示正则表达式</p>
         */
        REGEX(DesensitizeStrategies::regex),

        /**
         * 指定长度脱敏。
         *
         * <p>使用 {@link #value()} 指定长度；正数表示从头部开始计数，负数表示从尾部开始计数</p>
         *
         * <p>例子：对于文本 smile</p>
         * <pre>
         *     0  -> smile
         *     3  -> ***le
         *     6  -> *****
         *     -3 -> sm***
         *     -6 -> *****
         * </pre>
         */
        STRIP(DesensitizeStrategies::strip),

        ;

        private final Desensitizer delegate;

        Strategy(Desensitizer delegate) {
            this.delegate = delegate;
        }


        @Override
        public String apply(CharSequence source, JsonDesensitize desensitize) {
            return delegate.apply(source, desensitize);
        }
    }

}
