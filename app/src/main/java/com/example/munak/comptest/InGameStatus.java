package com.example.munak.comptest;

/**
 * Created by Munak on 2017. 3. 29..
 */
//속도, 가속도, 점수 등의 게임 내의 요소를 저장하는 클래스
//여기서 정보 가져가면 됨
class InGameStatus {
    //게임 온/오프
    private static boolean start;
    private static boolean stopSwitch = false;
    //게임 내에서 실시간으로 측정되는 값들
    private static int totalScore =0; // 총 점수
    private static double velocity = 0.0f; //속도 (센서에 의해 측정
    private static double currVelocity = 0.0f; //(5.14.추가됨) 현재속도(가속도 구하는 데 필요)
    private static double prevVelocity = 0.0f; //(5.14.추가됨) 이전속도(가속도 구하는 데 필요)
    private static double acceleration = 0.0f; //(5.14. 추가됨) 가속도
    private static double accelerationY = 0.0f; // 가속도 (옆 방향)
    private static double accelerationZ = 0.0f; // 가속도 (앞 방향)
    private static double locationX = 0.0f; // 현재 위치(위도)
    private static double locationY = 0.0f; // 현재 위치(경도)
    private static int kalCount = 0;

    //위반사항 또는 졸음쉼터 이용횟수 등 'record' 기능에 더해지는 값들
    //초 단위
    private static int violationAccel = 0; //가속도 위반 횟수
    private static int violationVelocity = 0; //속도 위반 횟수
    private static int violationKal = 0; //칼치기 횟수
    private static int useSleepinessCenter = 0; //졸음쉼터 이용 횟수
    private static int mission = 0;

    //initializer(5.18. 추가됨)
    public static synchronized void initialize(){

        totalScore =0; // 총 점수
        velocity = 0.0f; //속도 (센서에 의해 측정
        currVelocity = 0.0f; //(5.14.추가됨) 현재속도(가속도 구하는 데 필요)
        prevVelocity = 0.0f; //(5.14.추가됨) 이전속도(가속도 구하는 데 필요)
        acceleration = 0.0f; //(5.14. 추가됨) 가속도
        accelerationY = 0.0f; // 가속도 (옆 방향)
        accelerationZ = 0.0f; // 가속도 (앞 방향)
        locationX = 0.0f; // 현재 위치(위도)
        locationY = 0.0f; // 현재 위치(경도)
        kalCount = 0;
        violationAccel = 0; //가속도 위반 횟수
        violationVelocity = 0; //속도 위반 횟수
        violationKal = 0; //칼치기 횟수
        useSleepinessCenter = 0; //졸음쉼터 이용 횟수
        mission = 0;
    }

    //setters
    public static synchronized void setAcceleration(double y, double z){
        accelerationY = y;
        accelerationZ = z;
    }

    //5.14. 추가됨
    public static synchronized void setCurrVelocity(double v){ currVelocity = v;}

    public static synchronized void setPrevVelocity(double v){ prevVelocity = v;}

    public static synchronized void setAcceleration(double a){acceleration = a;}

    public static synchronized void setVelocity(double v){
        velocity = v;
    }

    public static synchronized void setLocationX(double x){
        locationX = x;
    }

    public static synchronized void setLocationY(double y){
        locationY = y;
    }

    public static synchronized void setTotalScore(int ts){
        totalScore = ts;
    }

    public static synchronized void setTotalScore(int a, int num){
        totalScore+=num;
    }

    public static synchronized void setViolationAccel(int i){
        violationAccel+=i;
    }

    public static synchronized void setViolationVelocity(int i){
        violationVelocity+=i;
    }

    public static synchronized void setViolationKal(int i){
        violationKal+=i;
    }

    public static synchronized void setUseSleepinessCenter(int i){
        useSleepinessCenter+=i;
    }

    public static synchronized void setKalCount(int i){
        if((kalCount + i ) >= 0){ //칼치기 카운트는 항상 양수다.
            kalCount+=i;
        }
    }

    public static synchronized void setStart(boolean b){
        start = b;
    }
    public static synchronized void setStopSwitch(boolean b) { stopSwitch = b; }
    public static synchronized void setMission() {
        mission = 1;
    }
    //getters

    //5.14. 추가됨
    public static synchronized double getCurrVelocity() { return currVelocity; }

    public static synchronized double getPrevVelocity(){ return prevVelocity; }

    public static synchronized double getAcceleration() { return acceleration; }

    public static synchronized int getTotalScore(){
        return totalScore;
    }

    public static synchronized double getVelocity() {
        return velocity;
    }

    public static synchronized double getAccelerationY(){
        return accelerationY;
    }

    public static synchronized double getAccelerationZ(){
        return accelerationZ;
    }

    public static synchronized double getLocationX(){
        return locationX;
    }

    public static synchronized double getLocationY(){
        return locationY;
    }

    public static synchronized int getViolationAccel(){
        return violationAccel;
    }

    public static synchronized int getViolationVelocity(){
        return violationVelocity;
    }

    public static synchronized int getViolationKal(){
        return violationKal;
    }

    public static synchronized int getUseSleepinessCenter(){
        return useSleepinessCenter;
    }

    public static synchronized int getKalCount() {
        return kalCount;
    }

    public static synchronized boolean getStart() {
        return start;
    }

    public static synchronized boolean getStopSwitch() {
        return stopSwitch;
    }

    public static synchronized int getMission(){ return mission;}
}