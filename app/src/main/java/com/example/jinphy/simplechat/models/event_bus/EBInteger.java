package com.example.jinphy.simplechat.models.event_bus;

/**
 * DESC:
 * Created by jinphy on 2018/3/11.
 */

public class EBInteger extends EBBase<Integer> {
    public EBInteger(int x) {
        super(false, x);
    }

    public EBInteger(boolean ok, int x) {
        super(ok, x);
    }

    public synchronized void add() {
        data++;
    }
}
