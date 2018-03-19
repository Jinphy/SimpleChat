package com.example.jinphy.simplechat.models.event_bus;

/**
 * DESC:
 * Created by jinphy on 2018/1/10.
 */

public class EBBase<T> {

    public boolean ok;

    public T data;

    public EBBase(boolean ok, T data) {
        this.ok = ok;
        this.data = data;
    }

}
