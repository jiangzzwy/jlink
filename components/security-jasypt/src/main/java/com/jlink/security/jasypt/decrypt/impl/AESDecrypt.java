package com.jlink.security.jasypt.decrypt.impl;

import com.jlink.security.jasypt.decrypt.Decrypt;
import com.jlink.security.jasypt.utils.EncryptAesUtil;

public class AESDecrypt implements Decrypt {
    @Override
    public String decrypt(String value) {
        return EncryptAesUtil.aesDecrypt(value);
    }
}
