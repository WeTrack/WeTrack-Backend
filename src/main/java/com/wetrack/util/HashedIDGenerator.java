package com.wetrack.util;

import java.util.Random;

public class HashedIDGenerator {
    private static final Random random = new Random(System.currentTimeMillis());

    public static String get(String... args) {
        StringBuilder builder = new StringBuilder(String.valueOf(random.nextInt()));
        for (String arg : args)
            builder.append('-').append(arg).append('-').append(random.nextInt());
        return CryptoUtils.md5Digest(builder.toString());
    }

}
