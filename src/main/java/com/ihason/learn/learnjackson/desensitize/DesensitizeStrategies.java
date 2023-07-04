package com.ihason.learn.learnjackson.desensitize;

import com.ihason.learn.learnjackson.desensitize.annotation.JsonDesensitize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 脱敏策略工厂。提供几个典型示例，可以补充更多的功能。
 *
 * @author Huanghs
 * @since 2.0
 * @date 2023-07-04
 */
public class DesensitizeStrategies {

    public static String all(CharSequence source, JsonDesensitize desensitize) {
        if (isEmpty(source)) {
            return str(source);
        }
        return replace(source, 0, source.length(), desensitize);
    }

    public static String address(CharSequence source, JsonDesensitize desensitize) {
        if (isEmpty(source)) {
            return str(source);
        }
        int start = Math.max(0, source.length() - 8);
        return replace(source, start, source.length(), desensitize);
    }

    public static String email(CharSequence source, JsonDesensitize desensitize) {
        if (isEmpty(source)) {
            return str(source);
        }
        String sourceString = str(source);
        int atIndex = sourceString.indexOf("@");
        if (atIndex <= 1) {
            // 没有 @ 符号或其左边没有字符
            return sourceString;
        }
        return replace(source, 1, atIndex, desensitize);
    }

    public static String custom(CharSequence source, JsonDesensitize desensitize) {
        Class<? extends Desensitizer> handler = desensitize.handler();
        try {
            Desensitizer desensitizer = handler.newInstance();
            return desensitizer.apply(source, desensitize);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new UnsupportedOperationException("Desensitize failed! nested exception is " + e.getMessage(), e);
        }
    }

    public static String regex(CharSequence source, JsonDesensitize desensitize) {
        if (isEmpty(source)) {
            return str(source);
        }
        Matcher matcher = Pattern.compile(desensitize.value()).matcher(source);
        return matcher.replaceAll(desensitize.replacement());
    }

    public static String strip(CharSequence source, JsonDesensitize desensitize) {
        if (isEmpty(source)) {
            return str(source);
        }
        int length = formatInt(desensitize.value());
        if (length < 0) {
            // 从尾部往前脱敏
            int start = Math.max(source.length() + length, 0);
            return replace(source, start, source.length(), desensitize);
        }
        int end = Math.min(length, source.length());
        return replace(source, 0, end, desensitize);
    }

    private static int formatInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Unable to parse string to int, string: " + value);
        }
    }

    private static String replace(CharSequence source, int startInclude, int endExclude, JsonDesensitize desensitize) {
        return replace(source, startInclude, endExclude, desensitize.replacement(), desensitize.repeat());
    }

    private static String replace(CharSequence source, int startInclude, int endExclude, String replacement, boolean repeat) {
        if (replacement.length() == 1) {
            // 单字符可以选择是否重复脱敏符号
            if (repeat) {
                return CharSequenceUtil.replace(source, startInclude, endExclude, replacement.charAt(0));
            }
        }
        // 脱敏符号不重复
        return CharSequenceUtil.replace(source, startInclude, endExclude, replacement);
    }

    private static String str(CharSequence source) {
        return CharSequenceUtil.str(source);
    }

    private static boolean isEmpty(CharSequence source) {
        return CharSequenceUtil.isEmpty(source);
    }

}
