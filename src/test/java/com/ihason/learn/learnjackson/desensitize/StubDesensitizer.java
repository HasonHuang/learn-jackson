package com.ihason.learn.learnjackson.desensitize;

import com.ihason.learn.learnjackson.desensitize.annotation.JsonDesensitize;

/**
 * Stub desensitizer
 *
 * @author Huanghs
 * @since 2.0
 * @date 2023-07-04
 */
public class StubDesensitizer implements Desensitizer {
    @Override
    public String apply(CharSequence source, JsonDesensitize desensitize) {
        return "mock";
    }
}
