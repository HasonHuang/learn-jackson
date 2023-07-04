package com.ihason.learn.learnjackson.desensitize;

import com.ihason.learn.learnjackson.desensitize.annotation.JsonDesensitize;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 模拟 DTO 对象
 *
 * @author Huanghs
 * @since 2.0
 * @date 2023-07-04
 */
@Data
@AllArgsConstructor
public class Person {

    @JsonDesensitize(strategy = JsonDesensitize.Strategy.ALL)
    private String value;

}
