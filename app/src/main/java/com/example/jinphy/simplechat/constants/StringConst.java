package com.example.jinphy.simplechat.constants;

import com.example.jinphy.simplechat.utils.Encrypt;

/**
 * Created by jinphy on 2017/8/9.
 */

public class StringConst {

    public static final String PREFERENCES_NAME_USER = Encrypt.md5("user");
    public static final String PREFERENCES_KEY_REMEMBER_PASSWORD = Encrypt.md5("remember_password");
    public static final String PREFERENCES_KEY_CURRENT_ACCOUNT = Encrypt.md5("current_account");
    public static final String PREFERENCES_KEY_HAS_LOGIN = Encrypt.md5("has_login");
    public static final String PREFERENCES_KEY_PASSWORD = "password";

    public static final String MD5 = "md5";
    public static final String UTF_8 = "UTF-8";

}
