package com.yrwcy.carpool.util;

import java.lang.annotation.*;

//延时删除注解
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeleteDelay {

    /**
     *  删除 key id
     */
    String name() default "";
}
