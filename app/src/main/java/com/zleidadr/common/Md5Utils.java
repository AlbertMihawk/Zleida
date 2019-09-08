package com.zleidadr.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xiaoxuli on 16/1/18.
 */
public class Md5Utils {

    /**
     * MessageDigest digester = MessageDigest.getInstance("MD5");
     * byte[] bytes = new byte[8192];
     * int byteCount;
     * while ((byteCount = in.read(bytes)) > 0) {
     * digester.update(bytes, 0, byteCount);
     * }
     * byte[] digest = digester.digest();
     *
     * @return
     */
    public static String computeMd5(String in) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(in.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }
}
