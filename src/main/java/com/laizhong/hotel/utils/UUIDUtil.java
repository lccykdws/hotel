package com.laizhong.hotel.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.UUID;

@Slf4j
public class UUIDUtil {
    private static final String[] chars = new String[]{"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
            "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    /**
     * 返回22位无重复UUID串
     *
     * @return uid
     */
    public static String getUid() {
        return getUUIDStr();
    }

    /**
     * 返回前缀+22位无重复UUID串
     *
     * @param prefix 前缀
     * @return pre + uid
     */
    public static String getUid(String prefix) {
        return prefix + getUUIDStr();
    }
    

    private static BigInteger unsigned2BigInt(long value) {
        if (value >= 0)
            return BigInteger.valueOf(value);
        long lowValue = value & 0x7fffffffffffffffL;
        return BigInteger.valueOf(lowValue).add(BigInteger.valueOf(Long.MAX_VALUE)).add(BigInteger.valueOf(1));
    }

    private static String getUUIDStr() {
        UUID uuid = UUID.randomUUID();
        BigInteger pt1 = unsigned2BigInt(uuid.getMostSignificantBits());
        BigInteger pt2 = unsigned2BigInt(uuid.getLeastSignificantBits());

        StringBuilder sb = new StringBuilder();
        while (pt1.longValue() != 0) {
            BigInteger[] s = pt1.divideAndRemainder(BigInteger.valueOf(chars.length));
            sb.append(chars[s[1].intValue()]);
            pt1 = s[0];
        }
        sb.append(StringUtils.repeat(chars[0], 11 - sb.length()));
        while (pt2.longValue() != 0) {
            BigInteger[] s = pt2.divideAndRemainder(BigInteger.valueOf(chars.length));
            sb.append(chars[s[1].intValue()]);
            pt2 = s[0];
        }
        sb.append(StringUtils.repeat(chars[0], 22 - sb.length()));
        return StringUtils.reverse(sb.toString());
    }
    
    public static String generate() {
        UUID uuid = UUID.randomUUID();
        return compressedUUID(uuid);
    }

    protected static String compressedUUID(UUID uuid) {
        byte[] byUuid = new byte[16];
        long least = uuid.getLeastSignificantBits();
        long most = uuid.getMostSignificantBits();
        long2bytes(most, byUuid, 0);
        long2bytes(least, byUuid, 8);
        String compressUUID = Base64.encodeBase64URLSafeString(byUuid);
        return compressUUID;
    }

    protected static void long2bytes(long value, byte[] bytes, int offset) {
        for (int i = 7; i > -1; i--) {
            bytes[offset++] = (byte) ((value >> 8 * i) & 0xFF);
        }
    }

    private static String compress(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        return compressedUUID(uuid);
    }

    private static String uncompress(String compressedUuid) {
        if (compressedUuid.length() != 22) {
            throw new IllegalArgumentException("Invalid uuid!");
        }
        byte[] byUuid = Base64.decodeBase64(compressedUuid + "==");
        long most = bytes2long(byUuid, 0);
        long least = bytes2long(byUuid, 8);
        UUID uuid = new UUID(most, least);
        return uuid.toString();
    }

    protected static long bytes2long(byte[] bytes, int offset) {
        long value = 0;
        for (int i = 7; i > -1; i--) {
            value |= (((long) bytes[offset++]) & 0xFF) << 8 * i;
        }
        return value;
    }
}
