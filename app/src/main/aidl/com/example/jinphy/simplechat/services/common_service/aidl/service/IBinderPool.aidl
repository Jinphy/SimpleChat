// IBinderPool.aidl
package com.example.jinphy.simplechat.services.common_service.aidl.service;

// Declare any non-default types here with import statements

interface IBinderPool {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

        IBinder getBinder(int type);
}
