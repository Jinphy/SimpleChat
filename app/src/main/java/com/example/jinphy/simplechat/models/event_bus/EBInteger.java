package com.example.jinphy.simplechat.models.event_bus;

/**
 * DESC:
 * Created by jinphy on 2018/3/11.
 */

public class EBInteger extends EBBase<Integer> {
    public EBInteger(int x) {
        super(true, x);
    }

    public synchronized void add() {
        data++;
    }
}
