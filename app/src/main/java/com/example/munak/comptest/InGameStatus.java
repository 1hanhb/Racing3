package com.example.munak.comptest;

/**
 * Created by Munak on 2017. 3. 29..
 */

//속도, 가속도, 점수 등의 게임 내의 요소를 저장하는 클래스
//여기서 정보 가져가면 됨
class InGameStatus {
    //게임 온/오프
    public static boolean start;

    //게임 내에서 실시간으로 측정되는 값들
    private static int totalScore = 0; // 총 점수
    private static double velocity = 0.0f; //속도
    private static double accelerationY = 0.0f; // 가속도 (옆 방향)
    private static double accelerationZ = 0.0f; // 가속도 (앞 방향)
    private static double locationX = 0.0f; // 현재 위치(위도)
    private static double locationY = 0.0f; // 현재 위치(경도)

    //위반사항 또는 졸음쉼터 이용횟수 등 'record' 기능에 더해지는 값들
    private static int violationAccel = 0; //가속도 위반 횟수
    private static int violationVelocity = 0; //속도 위반 횟수
    private static int violationKal = 0; //칼치기 횟수
    private static int useSleepinessCenter = 0; //졸음쉼터 이용 횟수


    //Definition for getter and setter
    //setters
    public static synchronized void setAcceleration(double y, double z) {
        accelerationY = y;
        accelerationZ = z;
    }

    public static synchronized void setVelocity(double v) {
        velocity = v;
    }

    public static synchronized void setLocation(double x, double y) {
        locationX = x;
        locationY = y;
    }

    public static synchronized void setTotalScore(int ts) {
        totalScore = ts;
    }

    public static synchronized void setTotalScore(int a, int num) {
        totalScore += num;
    }

    public static synchronized void setViolationAccel(int i) {
        violationAccel += i;
    }

    public static synchronized void setViolationVelocity(int i) {
        violationVelocity += i;
    }

    public static synchronized void setViolationKal(int i) {
        violationKal += i;
    }

    public static synchronized void setUseSleepinessCenter(int i) {
        useSleepinessCenter += i;
    }


    //getters
    public static synchronized int getTotalScore() {
        return totalScore;
    }

    public static synchronized double getVelocity() {
        return velocity;
    }

    public static synchronized double getAccelerationY() {
        return accelerationY;
    }

    public static synchronized double getAccelerationZ() {
        return accelerationZ;
    }

    public static synchronized double getLocationX() {
        return locationX;
    }

    public static synchronized double getLocationY() {
        return locationY;
    }

    public static synchronized int getViolationAccel() {
        return violationAccel;
    }

    public static synchronized int getViolationVelocity() {
        return violationVelocity;
    }

    public static synchronized int getViolationKal() {
        return violationKal;
    }

    public static synchronized int getUseSleepinessCenter() {
        return useSleepinessCenter;
    }
}