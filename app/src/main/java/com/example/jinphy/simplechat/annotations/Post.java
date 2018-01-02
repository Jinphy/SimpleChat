package com.example.jinphy.simplechat.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * DESC: 网络请求方法：POST
 * Created by jinphy on 2017/12/31.
 */

@Documented
@Target(value = {FIELD})
@Retention(value = RUNTIME)
public @interface Post {

}
