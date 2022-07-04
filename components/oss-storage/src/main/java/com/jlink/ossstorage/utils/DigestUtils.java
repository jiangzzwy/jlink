package com.jlink.ossstorage.utils;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.jlink.ossstorage.config.CheckSumAlgoType;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DigestUtils {
    public static String genfileId(File file, CheckSumAlgoType checkSumAlgoType) throws IOException {
        String checksum = null;
        switch (checkSumAlgoType) {
            case CRC32:
                checksum = Files.asByteSource(file).hash(Hashing.crc32()).toString();
                break;
            case SHA_256:
                checksum =  Files.asByteSource(file).hash(Hashing.sha256()).toString();
                break;
            default:
                checksum =  Files.asByteSource(file).hash(Hashing.crc32()).toString();
        }
        return checksum;
    }
    public static String genUniqueFileId(){
       return  UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
