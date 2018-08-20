package com.conveyal.r5.profile.mcrr;

import java.util.Arrays;

final class IntUtils {
    /** protect this class from being instantiated. */
    private IntUtils() {};

    static int[] newIntArray(int size, int initalValue) {
        int [] array = new int[size];
        Arrays.fill(array, initalValue);
        return array;
    }

    static String intToString(int value, int notSetValue) {
        return value == notSetValue ? "" : Integer.toString(value);
    }
}
