package com.example.munak.comptest;

/**
 * Created by Munak on 2017. 4. 10..
 */

public class LocationOfIC {

    //IC 위치
    public static class locationOfIC {
        double x;
        double y;

        locationOfIC(double x, double y){
            this.x = x;
            this.y = y;
        }

    }
    //IC 위치 임시 정의
    static locationOfIC[] locationOfICs= {
            new locationOfIC(27.0000f, 27.0000f),
            new locationOfIC(1.0f, 2.0f)
    };


    public static boolean isOnNationalHighway(double x, double y){

        for(int i=0; i < locationOfICs.length; i++){
            if((x < locationOfICs[i].x+0.005 && locationOfICs[i].x-0.005< x  )&& (y < locationOfICs[i].y+0.005 && locationOfICs[i].y-0.005< y))
                return true;

        }

        return false;
    }

}
