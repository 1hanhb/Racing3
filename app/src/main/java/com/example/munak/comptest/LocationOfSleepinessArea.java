package com.example.munak.comptest;

/**
 * Created by Munak on 2017. 4. 10..
 */

public class LocationOfSleepinessArea {

    //졸음쉼터 위치 임시
    //
    public static class sleepinessArea {
        double x;
        double y;

        sleepinessArea(double x, double y){
            this.x = x;
            this.y = y;
        }

    }
    //졸음쉼터 임시 정의
    static sleepinessArea[] sleepinessAreas= {
            new sleepinessArea(27.0000f, 27.0000f),
            new sleepinessArea(1.0f, 2.0f)
    };


    //경도와 위도를 받고
    public static boolean isAtSleepinessArea(double x, double y){

        //현재 위치와 졸음쉼터 위치를 비교 후,
        //졸음쉼터에 있다고 판단되면 true 리턴
        for(int i=0; i < sleepinessAreas.length; i++){
            if((x < sleepinessAreas[i].x+0.005 && sleepinessAreas[i].x-0.005< x  )&& (y < sleepinessAreas[i].y+0.005 && sleepinessAreas[i].y-0.005< y))
                return true;
        }

        return false;
    }



}