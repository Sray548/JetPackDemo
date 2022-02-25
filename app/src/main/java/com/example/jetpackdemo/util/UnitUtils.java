/*
 * *****************************************************************************
 *  Copyright (c) 2020 VIA Technologies, Inc. All Rights Reserved.
 *  This PROPRIETARY SOFTWARE is the property of VIA Technologies, Inc.
 *  and may contain trade secrets and/or other confidential information of
 *  VIA Technologies, Inc. This file shall not be disclosed to any third
 *  party, in whole or in part, without prior written consent of VIA.
 *  THIS PROPRIETARY SOFTWARE AND ANY RELATED DOCUMENTATION ARE PROVIDED AS IS,
 *  WITH ALL FAULTS, AND WITHOUT WARRANTY OF ANY KIND EITHER EXPRESS OR IMPLIED,
 *  AND VIA TECHNOLOGIES, INC. DISCLAIMS ALL EXPRESS OR IMPLIED
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, QUIET
 *  ENJOYMENT OR NON-INFRINGEMENT.
 * ****************************************************************************
 */

package com.example.jetpackdemo.util;


/**
 * Created by Enzo Cotter on 2020/3/26.
 */
public class UnitUtils {

    private static final double MILE2KM = 1.609344;
    private static final double M2FT = 3.2808398;
    private static final double IMGALLON2LITTER = 4.546092;
    private static final double USGALLON2LITTER = 3.785412;
    private static final double T2KG = 1000;
    private static final double IMT2KG = 1016.05;
    private static final double UST2KG = 907.2;
    private static final double CID2LITTER = 0.01639;
    private static final double M2INCHES = 39.3700787;
    private static final double M2CM = 100;
    private static final double MM2INCHES = 0.0393701;
    private static final double MM2M = 0.001;
    private static final double MM2CM = 0.1;

    public static final int METRIC = 0;
    public static final int IMPERIAL = 1;
    public static final int US = 2;

    private static final String METRIC_BOUNDARY = " m";
    private static final String METRIC_FUEL_CON_RATE = " Km / L";
    private static final String METRIC_FUEL_CON = " L";
    private static final String METRIC_SPEED = " Km / Hr";
    private static final String METRIC_WEIGHT = " T";
    private static final String METRIC_DISPLACEMENT = METRIC_FUEL_CON;
    private static final String METRIC_TANK_SIZE = METRIC_FUEL_CON;
    private static final String METRIC_RADAR_DISTANCE = METRIC_BOUNDARY;
    private static final String METRIC_CAM_TO_ROOF_DIS = METRIC_RADAR_DISTANCE;
    private static final String METRIC_CAR_WIDTH = METRIC_RADAR_DISTANCE;
    private static final String METRIC_CAM_TO_FRONT_DIS = METRIC_RADAR_DISTANCE;
    private static final String METRIC_TIRE_DIMENSION = " cm";

    private static final String IMPERIAL_BOUNDARY = " ft";
    private static final String IMPERIAL_FUEL_CON_RATE = " Mpg";
    private static final String IMPERIAL_FUEL_CON = " Gal";
    private static final String IMPERIAL_SPEED = " Mph";
    private static final String IMPERIAL_WEIGHT = METRIC_WEIGHT;
    private static final String IMPERIAL_DISPLACEMENT = " CID";
    private static final String IMPERIAL_TANK_SIZE = IMPERIAL_FUEL_CON;
    private static final String IMPERIAL_RADAR_DISTANCE = IMPERIAL_BOUNDARY;
    private static final String IMPERIAL_CAM_TO_ROOF_DIS = IMPERIAL_RADAR_DISTANCE;
    private static final String IMPERIAL_CAR_WIDTH = IMPERIAL_RADAR_DISTANCE;
    private static final String IMPERIAL_CAM_TO_FRONT_DIS = IMPERIAL_RADAR_DISTANCE;
    private static final String IMPERIAL_TIRE_DIMENSION = " in";

    private static final String US_BOUNDARY = IMPERIAL_BOUNDARY;
    private static final String US_FUEL_CON_RATE = IMPERIAL_FUEL_CON_RATE;
    private static final String US_FUEL_CON = IMPERIAL_FUEL_CON;
    private static final String US_SPEED = IMPERIAL_SPEED;
    private static final String US_WEIGHT = METRIC_WEIGHT;
    private static final String US_DISPLACEMENT = IMPERIAL_DISPLACEMENT;
    private static final String US_TANK_SIZE = IMPERIAL_TANK_SIZE;
    private static final String US_RADAR_DISTANCE = IMPERIAL_RADAR_DISTANCE;
    private static final String US_CAM_TO_ROOF_DIS = US_RADAR_DISTANCE;
    private static final String US_CAR_WIDTH = US_RADAR_DISTANCE;
    private static final String US_CAM_TO_FRONT_DIS = US_RADAR_DISTANCE;
    private static final String US_TIRE_DIMENSION = IMPERIAL_TIRE_DIMENSION;


    /**
     * @param data "2.05-2.20"
     * @param unit
     * @return
     */
    public static String getCameraHeight(String data, int unit) {
        String[] split = data.split("-");
        double start = Double.parseDouble(split[0]);
        double end = Double.parseDouble(split[1]);
        return getBoundary(start, unit) + "-" + getBoundary(end, unit);
    }

    public static String getBoundary(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = formatData(data) + METRIC_BOUNDARY;
                break;
            case IMPERIAL:
                result = formatData(data * M2FT) + IMPERIAL_BOUNDARY;
                break;
            case US:
                result = formatData(data * M2FT) + US_BOUNDARY;
                break;
        }

        return result;
    }

    public static String getSpeed(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = Math.round(data) + METRIC_SPEED;
                break;
            case IMPERIAL:
                result = Math.round(data / MILE2KM) + IMPERIAL_SPEED;
                break;
            case US:
                result = Math.round(data / MILE2KM) + US_SPEED;
                break;
        }

        return result;
    }

    public static String getSpeedData(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = String.valueOf(Math.round(data));
                break;
            case IMPERIAL:
            case US:
                result = String.valueOf(Math.round(data / MILE2KM));
                break;
        }

        return result;
    }

    public static double setSpeed(double data, int unit) {
        double result = 0.0;

        switch (unit) {
            case METRIC:
                result = data;
                break;
            case IMPERIAL:
                result = data * MILE2KM;
                break;
            case US:
                result = data * MILE2KM;
                break;
        }

        return result;
    }

    public static double setTireDimension(double data, int unit) {
        double result = 0.0;

        switch (unit) {
            case METRIC:
                result = data / MM2CM;
                break;
            case IMPERIAL:
            case US:
                result = data / MM2INCHES;
                break;
        }

        return result;
    }

    public static double setWaytronicTireDimension(double data, int unit) {
        double result = 0.0;

        switch (unit) {
            case METRIC:
                result = data;
                break;
            case IMPERIAL:
            case US:
                result = data / MM2INCHES * MM2CM;
                break;
        }

        return result;
    }

    public static String getTireDimension(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = Math.round(data * MM2CM) + METRIC_TIRE_DIMENSION;
                break;
            case IMPERIAL:
                result = Math.round(data * MM2INCHES) + IMPERIAL_TIRE_DIMENSION;
                break;
            case US:
                result = Math.round(data * MM2INCHES) + US_TIRE_DIMENSION;
                break;
        }

        return result;
    }

    public static String getWaytronicTireDimension(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = Math.round(data) + METRIC_TIRE_DIMENSION;
                break;
            case IMPERIAL:
                result = Math.round(data / MM2CM * MM2INCHES) + IMPERIAL_TIRE_DIMENSION;
                break;
            case US:
                result = Math.round(data / MM2CM * MM2INCHES) + US_TIRE_DIMENSION;
                break;
        }

        return result;
    }

    public static String getTireDimensionData(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = String.valueOf(Math.round(data * MM2CM));
                break;
            case IMPERIAL:
            case US:
                result = String.valueOf(Math.round(data * MM2INCHES));
                break;
        }

        return result;
    }

    public static String getWaytronicTireDimensionData(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = String.valueOf(Math.round(data));
                break;
            case IMPERIAL:
            case US:
                result = String.valueOf(Math.round(data / MM2CM * MM2INCHES));
                break;
        }

        return result;
    }

    public static String getFuelConRate(double data, int unit, boolean hasError) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = formatData(data) + METRIC_FUEL_CON_RATE;
                break;
            case IMPERIAL:
                result = formatData(data * IMGALLON2LITTER / MILE2KM) + IMPERIAL_FUEL_CON_RATE;
                break;
            case US:
                result = formatData(data * USGALLON2LITTER / MILE2KM) + US_FUEL_CON_RATE;
                break;
        }
        if (hasError) {
//            result += "?";
        }

        return result;
    }

    public static String getFuelCon(double data, int unit, boolean hasError) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = formatData(data) + METRIC_FUEL_CON;
                break;
            case IMPERIAL:
                result = formatData(data / IMGALLON2LITTER) + IMPERIAL_FUEL_CON;
                break;
            case US:
                result = formatData(data / USGALLON2LITTER) + US_FUEL_CON;
                break;
        }
        if (hasError) {
//            result += "?";
        }

        return result;
    }

    public static String getDisplacement(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = formatData(data) + METRIC_DISPLACEMENT;
                break;
            case IMPERIAL:
                result = formatData(data / CID2LITTER) + IMPERIAL_DISPLACEMENT;
                break;
            case US:
                result = formatData(data / CID2LITTER) + US_DISPLACEMENT;
                break;
        }

        return result;
    }

    public static String getDisplacementNum(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = formatData(data);
                break;
            case IMPERIAL:
            case US:
                result = formatData(data / CID2LITTER);
                break;
        }

        return result;
    }

    public static double setDisplacement(double data, int unit) {
        double result = 0;

        switch (unit) {
            case METRIC:
                result = data;
                break;
            case IMPERIAL:
            case US:
                result = data * CID2LITTER;
                break;
        }

        return result;
    }

    public static String getTankSize(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = formatData(data) + METRIC_TANK_SIZE;
                break;
            case IMPERIAL:
                result = formatData(data / IMGALLON2LITTER) + IMPERIAL_TANK_SIZE;
                break;
            case US:
                result = formatData(data / USGALLON2LITTER) + US_TANK_SIZE;
                break;
        }

        return result;
    }

    public static String getTankSizeNum(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = formatData(data);
                break;
            case IMPERIAL:
                result = formatData(data / IMGALLON2LITTER);
                break;
            case US:
                result = formatData(data / USGALLON2LITTER);
                break;
        }

        return result;
    }

    public static double setTankSize(double data, int unit) {
        double result = 0;

        switch (unit) {
            case METRIC:
                result = data;
                break;
            case IMPERIAL:
                result = data * IMGALLON2LITTER;
                break;
            case US:
                result = data * USGALLON2LITTER;
                break;
        }

        return result;
    }

    public static String getWeight(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = formatData(data) + METRIC_WEIGHT;
                break;
            case IMPERIAL:
                result = formatData(data * T2KG / IMT2KG) + IMPERIAL_WEIGHT;
                break;
            case US:
                result = formatData(data * T2KG / UST2KG) + US_WEIGHT;
                break;
        }

        return result;
    }

    //特别声明小数点分隔符为"."，不根据系统语言变化而变化
    private static String formatData(double data) {
        double value = (double) Math.round(data * 100) / 100;

        String str = String.format("%.2f", value);

        return str;
    }

    public static String getSpeedUnit(int unit) {
        String result = "";
        switch (unit) {
            case METRIC:
                result = METRIC_SPEED;
                break;
            case IMPERIAL:
                result = IMPERIAL_SPEED;
                break;
            case US:
                result = US_SPEED;
                break;
        }
        return result;
    }

    public static String getTireDimensionUnit(int unit) {
        String result = "";
        switch (unit) {
            case METRIC:
                result = METRIC_TIRE_DIMENSION;
                break;
            case IMPERIAL:
                result = IMPERIAL_TIRE_DIMENSION;
                break;
            case US:
                result = US_TIRE_DIMENSION;
                break;
        }
        return result;
    }


    public static String getRadarDistanceUnit(int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = METRIC_RADAR_DISTANCE;
                break;
            case IMPERIAL:
                result = IMPERIAL_RADAR_DISTANCE;
                break;
            case US:
                result = US_RADAR_DISTANCE;
                break;
        }

        return result;
    }

    public static String getCamToRoofDisUnit(int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = METRIC_CAM_TO_ROOF_DIS;
                break;
            case IMPERIAL:
                result = IMPERIAL_CAM_TO_ROOF_DIS;
                break;
            case US:
                result = US_CAM_TO_ROOF_DIS;
                break;
        }

        return result;
    }

    public static String getCarWidthUnit(int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = METRIC_CAR_WIDTH;
                break;
            case IMPERIAL:
                result = IMPERIAL_CAR_WIDTH;
                break;
            case US:
                result = US_CAR_WIDTH;
                break;
        }

        return result;
    }

    public static String getCamToFrontDisUnit(int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = METRIC_CAM_TO_FRONT_DIS;
                break;
            case IMPERIAL:
                result = IMPERIAL_CAM_TO_FRONT_DIS;
                break;
            case US:
                result = US_CAM_TO_FRONT_DIS;
                break;
        }

        return result;
    }

    public static double setRadarDistance(double data, int unit) {
        double result = 0;

        switch (unit) {
            case METRIC:
                result = data * M2CM;
                break;
            case IMPERIAL:
            case US:
                result = data / M2FT * M2CM;
                break;
        }

        return result;
    }

    public static String getRadarDistance(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = formatData(data / M2CM) + METRIC_RADAR_DISTANCE;
                break;
            case IMPERIAL:
            case US:
                result = formatData(data / M2CM * M2FT) + IMPERIAL_RADAR_DISTANCE;
                break;
        }
        return result;
    }

    public static String getRadarDistanceData(double data, int unit) {
        String result = "";

        switch (unit) {
            case METRIC:
                result = formatData(data / M2CM);
                break;
            case IMPERIAL:
            case US:
                result = formatData(data / M2CM * M2FT);
                break;
        }
        return result;
    }
}
