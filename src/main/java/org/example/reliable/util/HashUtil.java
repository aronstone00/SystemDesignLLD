package org.example.reliable.util;

public class HashUtil {

    public static String convertIntoHash(Object payload){
        return String.valueOf(payload.hashCode());
    }
}
