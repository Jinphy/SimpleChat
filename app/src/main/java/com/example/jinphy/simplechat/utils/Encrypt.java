package com.example.jinphy.simplechat.utils;

import android.support.annotation.IntRange;

import com.example.jinphy.simplechat.constants.StringConst;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.example.jinphy.simplechat.constants.StringConst.UTF_8;

/**
 * Created by jinphy on 2017/8/9.
 */

public class Encrypt {


    private Encrypt(){}


    /**
     * 将一段文本信息按指定次数进行MD5加密
     *
     * @param msg 待加密的文本信息
     * @param times 加密次数
     * */
    public static String md5(String msg, @IntRange(from = 1) int times) {
        if (times < 1) {
            times=1;
        }
        for (int i = 0; i < times; i++) {
            msg = md5(msg);
        }
        return msg;
    }

    /**
     * 将一段文本信息进行MD5加密，加密一个
     *
     * @param msg 待加密的文本信息
     * */
    public static String md5(String msg) {
        try{
            MessageDigest digest = MessageDigest.getInstance(StringConst.MD5);
            byte[] encryptedMsg = digest.digest(msg.getBytes(UTF_8));

            return parseBytes(encryptedMsg);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return  null;
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

//    将用md5加密后的字节数组进行解析
    private static String parseBytes(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length*2);
        for (byte aByte : bytes) {
            builder.append(parseByte(aByte));
        }
        return builder.toString().toUpperCase();
    }

//    解析每个字节
    private static String parseByte(byte b) {
        String temp = Integer.toHexString(b & 0xff);
        return temp.length()==1?0+temp:temp;
    }
}
