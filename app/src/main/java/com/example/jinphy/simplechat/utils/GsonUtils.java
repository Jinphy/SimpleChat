package com.example.jinphy.simplechat.utils;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by jinphy on 2017/12/4.
 */

public class GsonUtils {

    public static Gson gson = new Gson();

    /**
     * DESC: 把json转换成JavaBean
     * Created by jinphy, on 2017/12/4, at 22:52
     */
    public static <T> T toBean(String str, Class<T> clazz) {
        return gson.fromJson(str, clazz);
    }


    /**
     * DESC: 把json转换成JavaBean
     * Created by jinphy, on 2017/12/4, at 22:52
     */
    public static <T> T toBean(String str, Type type) {
        return gson.fromJson(str, type);
    }

    /**
     * DESC: 把JavaBean转换成json
     * Created by jinphy, on 2017/12/4, at 22:55
     */
    public static<T> String toJson(T bean ) {
        return gson.toJson(bean);
    }


    /**
     * DESC: 获取泛型类的Type类型
     *
     * 例如： Map<String,Integer>这个类，其中rawType表示Map，typeArgs数组表示String，Integer
     *
     *
     * @param rawType   泛型外部包装类
     * @param typeArgs  类型参数
     * Created by jinphy, on 2018/1/5, at 14:39
     */
    public static Type getType(Class rawType, Type...typeArgs) {
        return GenericType.get(rawType, typeArgs);
    }


    /**
     * DESC: 泛型类的类型信息
     * Created by jinphy, on 2018/1/5, at 14:44
     */
    public static class GenericType implements ParameterizedType {
        private Class raw;
        private Type[] args;

        private GenericType(Class rawType, Type... typeArgs) {
            this.raw = rawType;
            this.args = typeArgs;
        }

        /**
         * DESC:
         * Created by jinphy, on 2018/1/5, at 9:46
         */
        public static GenericType get(Class raw, Type[] args) {
            return new GenericType(raw, args);
        }



        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }

        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }



}
