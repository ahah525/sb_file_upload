package com.ll.exam.app10.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class Util {
    public static class date {
        // 현재 일자 조회
        public static String getCurrentDateFormatted(String pattern) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(new Date());
        }
    }

    public static class file {
        // 파일 확장자 조회
        public static String getExt(String filename) {
            return Optional.ofNullable(filename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                    .orElse("");
        }
    }
}