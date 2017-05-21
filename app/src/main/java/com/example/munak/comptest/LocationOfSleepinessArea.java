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
            new sleepinessArea(37.592820, 126.767587), //김포
            new sleepinessArea(37.451004, 126.803376), //시흥
            new sleepinessArea(37.355950, 126.863597), //서서울
            new sleepinessArea(37.453095, 127.255840), //번천
            new sleepinessArea(37.156197, 127.086810), //오산
            new sleepinessArea(37.392906, 127.025123), //청계
            new sleepinessArea(37.320246, 126.983762), //이목
            new sleepinessArea(37.589687, 127.156122), //구리
            new sleepinessArea(37.288382, 127.139069), //용인
            new sleepinessArea(37.448037, 127.274694), //상번천
            new sleepinessArea(37.070020, 126.888579), //향남
            new sleepinessArea(37.323129, 127.338600), //도척

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