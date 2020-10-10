package com.example.onebatis.util;

import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author zyred
 * @createTime 2020/9/24 16:48
 **/
public class ParseUtil {

    public static boolean revert(String bool) {
        if (Objects.equals("true", bool)) {
            return true;
        }
        return false;
    }

}
