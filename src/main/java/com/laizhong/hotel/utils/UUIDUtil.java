package com.laizhong.hotel.utils;

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
}
