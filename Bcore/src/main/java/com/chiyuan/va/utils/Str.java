package com.chiyuan.va.utils;


public final class Str {
    private Str() {}

    
    private static final byte[] XOR_KEY = {
        (byte)0x3A, (byte)0x71, (byte)0xC5, (byte)0x92, (byte)0xE8,
        (byte)0x4F, (byte)0xB3, (byte)0x1D, (byte)0x67, (byte)0xFA,
        (byte)0x84, (byte)0x5C, (byte)0xD9, (byte)0x26, (byte)0x0B,
        (byte)0xA1
    };

    
    public static String dec(byte[] encoded) {
        byte[] result = new byte[encoded.length];
        for (int i = 0; i < encoded.length; i++) {
            result[i] = (byte) (encoded[i] ^ XOR_KEY[i % XOR_KEY.length]);
        }
        return new String(result);
    }

    
    public static byte[] enc(String plain) {
        byte[] bytes = plain.getBytes();
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = (byte) (bytes[i] ^ XOR_KEY[i % XOR_KEY.length]);
        }
        return result;
    }

    
    public static boolean eq(byte[] encoded, String plain) {
        if (encoded.length != plain.length()) return false;
        for (int i = 0; i < encoded.length; i++) {
            if ((byte)(encoded[i] ^ XOR_KEY[i % XOR_KEY.length]) != (byte)plain.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
