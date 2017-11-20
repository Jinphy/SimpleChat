package com.example.jinphy.simplechat.model.event_bus;

import java.io.Serializable;

/**
 * Created by jinphy on 2017/11/18.
 */

public class EBLoginInfo implements Serializable{

    public boolean isNewAccount;

    public String account;

    public EBLoginInfo(String account) {
        this(true, account);
    }

    public EBLoginInfo(boolean isNewAccount, String account) {
        this.isNewAccount = isNewAccount;
        this.account = account;
    }
}
