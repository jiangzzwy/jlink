package com.jlink.security.jasypt.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Slf4j
public class EncryptAesUtil {
    /**
     * 密钥 AES加解密要求key必须要128个比特位（这里需要长度为16，否则会报错）
     */
    private static final String KEY = "jlink_app_123456";
    private static final String ALGORITHMS = "AES/ECB/PKCS5Padding";
    private static final String AES = "AES";
    public static void main(String[] args) {
        log.info("加密密钥和解密密钥：{}", KEY);

        String content = "nebula";//"0gvzJr66iNs5";
        System.out.println("加密前：" + content);

        String encrypt = aesEncrypt(content);
        System.out.println("加密后：" + encrypt);

        String decrypt = aesDecrypt(encrypt);
        System.out.println("解密后：" + decrypt);
    }

    /**
     * 将字符串【AES加密】为base 64 code
     *
     * @param content 待加密的内容
     * @return 加密后的base 64 code
     */
    public static String aesEncrypt(String content) {
        try {
            // 创建密码器
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            // 初始化为加密模式的密码器
            Cipher cipher = Cipher.getInstance(ALGORITHMS);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(EncryptAesUtil.KEY.getBytes(), EncryptAesUtil.AES));

            byte[] bytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            // 使用base64解码
            return Base64.encodeBase64String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将base 64 code 【AES解密】为字符串
     *
     * @param encryptStr 待解密的base 64 code
     * @return 解密后的String
     */
    public static String aesDecrypt(String encryptStr) {
        try {
            if (StringUtils.isEmpty(encryptStr)) {
                return null;
            }
            // 将字符串转为byte，返回解码后的byte[]
            byte[] encryptBytes = new BASE64Decoder().decodeBuffer(encryptStr);

            // 创建密码器
            KeyGenerator kgen = KeyGenerator.getInstance(EncryptAesUtil.AES);
            kgen.init(128);
            // 初始化为解密模式的密码器
            Cipher cipher = Cipher.getInstance(ALGORITHMS);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(EncryptAesUtil.KEY.getBytes(), EncryptAesUtil.AES));
            byte[] decryptBytes = cipher.doFinal(encryptBytes);

            return new String(decryptBytes);
        } catch (Exception e) {
            log.error(e.getMessage() + e);
        }
        return null;
    }
}

