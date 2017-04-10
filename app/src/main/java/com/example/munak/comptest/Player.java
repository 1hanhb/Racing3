package com.example.munak.comptest;

/**
 * Created by Munak on 2017. 3. 29..
 */


public class Player {
    private String name;
    private String email;
    private String password;

    //게임 내에서 실시간으로 측정되는 값들
    private int totalScore = 0; // 총 점수

    //위반사항 또는 졸음쉼터 이용횟수 등 'record' 기능에 더해지는 값들
    private int violationAccel = 0; //가속도 위반 횟수
    private int violationVelocity = 0; //속도 위반 횟수
    private int violationKal = 0; //칼치기 횟수
    private int useSleepinessCenter = 0; //졸음쉼터 이용 횟수

    public Player(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Player(){

    }

    public Player(String email, String password){
        this.email = email;
        this.password = password;
    }

    public Player(String email){
        this.email = email;
    }

    public Player(Player p){
        name = p.getName();
        email = p.getEmail();
        password = p.getPassword();

        totalScore = p.getTotalScore();

        violationAccel = p.getViolationAccel();
        violationVelocity = p.getViolationVelocity();
        violationKal = p.getViolationKal();
        useSleepinessCenter = p.getUseSleepinessCenter();
    }


    public void getData(){
        this.totalScore = InGameStatus.getTotalScore();

        this.violationAccel = InGameStatus.getViolationAccel();
        this.violationVelocity = InGameStatus.getViolationVelocity();
        this.violationKal = InGameStatus.getViolationKal();
        this.useSleepinessCenter = InGameStatus.getUseSleepinessCenter();
    }

    public void getData(Player p){
        name = p.getName();
        email = p.getEmail();
        password = p.getPassword();

        totalScore = p.getTotalScore();

        violationAccel = p.getViolationAccel();
        violationVelocity = p.getViolationVelocity();
        violationKal = p.getViolationKal();
        useSleepinessCenter = p.getUseSleepinessCenter();
    }

    public String getName(){return name;}
    public String getEmail(){return email;}
    public String getPassword(){return password;}

    public int getTotalScore(){return totalScore;}

    public int getViolationAccel(){return violationAccel;}
    public int getViolationVelocity(){return violationAccel;}
    public int getViolationKal(){return violationAccel;}
    public int getUseSleepinessCenter(){return violationAccel;}


}