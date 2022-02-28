package com.example.jetpackdemo.util;

import java.text.DecimalFormat;

public class Utils {
    public static String formatSize(double sdsize) {
        DecimalFormat df = new DecimalFormat("0.00");
        String sdsizeStr;
        if (sdsize > 1024) {
            sdsize = sdsize / 1024;//KB
            if (sdsize > 1024) {
                sdsize = sdsize / 1024;//MB
                if (sdsize > 1024) {
                    sdsize = sdsize / 1024;//GB
                    sdsizeStr = df.format(sdsize) + "G";
                } else {//MB
                    sdsizeStr = df.format(sdsize) + "M";
                }
            } else { //KB
                sdsizeStr = df.format(sdsize) + "KB";
            }
        } else { // Byte
            sdsizeStr = df.format(sdsize) + "B";
        }
        return sdsizeStr;
    }
}
