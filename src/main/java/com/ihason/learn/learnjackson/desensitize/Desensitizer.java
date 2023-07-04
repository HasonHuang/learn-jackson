package com.ihason.learn.learnjackson.desensitize;

import com.ihason.learn.learnjackson.desensitize.annotation.JsonDesensitize;

import java.util.Objects;

/**
 * 脱敏器
 *
 * @author Huanghs
 * @since 2.0
 * @date 2023-07-04
 */
@FunctionalInterface
public interface Desensitizer {

    /**
     * 脱敏
     *
     * @param source 敏感内容
     * @param desensitize 注解
     * @return 脱敏后的字符串
     */
    String apply(CharSequence source, JsonDesensitize desensitize);

    /**
     * 返回新的脱敏器，这个脱敏器执行完成后，结果作为另一个脱敏器 {@code after} 的参数执行。
     *
     * @param after 另一个脱敏器
     * @return 新的脱敏器
     */
    default Desensitizer then(Desensitizer after) {
        Objects.requireNonNull(after, "The after desensitizer cannot be null");
        return (source, desensitize) -> after.apply(apply(source, desensitize), desensitize);
    }

    /**
     * 返回新的脱敏器，前一个脱敏器 {@code before} 执行完成后，结果作为该脱敏器的参数执行。
     *
     * @param before 前一个脱敏器
     * @return 新的脱敏器
     */
    default Desensitizer compose(Desensitizer before) {
        Objects.requireNonNull(before, "The before desensitizer cannot be null");
        return (source, desensitize) -> apply(before.apply(source, desensitize), desensitize);
    }

}
