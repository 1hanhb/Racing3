package com.example.munak.comptest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static android.R.id.list;

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

    public Player(String email, String password){
        this.email = email;
        this.password = password;
    }

    public Player(){

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

    public void writeData(){
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;

        Player player = new Player(this);

        ArrayList list = new ArrayList();

        getData();
        list.add(player);

        /*
        Book b1 = new Book("a0001","자바완성","홍길동",10000);
        Book b2 = new Book("a0002","스트럿츠","김유신",20000);
        Book b3 = new Book("a0003","기초 EJB","김성박",25000);

        list.add(b1);
        list.add(b2);
        list.add(b3);
        */

        try {
            fout = new FileOutputStream("datalist.dat");
            oos = new ObjectOutputStream(fout);

            oos.writeObject(list);
            oos.reset();
        }catch(Exception e){

        }finally{
            try{
                oos.close();
                fout.close();
            }catch(Exception ioe){}
        }
    }

    boolean isThisEmail(String email){
        if(this.email.equals(email))
            return true;
        else
            return false;
    }

    public void readData(){
        FileInputStream fin = null;
        ObjectInputStream ois = null;
        Player player = new Player();
        try{
            int i = 0;
            fin = new FileInputStream("datalist.dat");
            ois = new ObjectInputStream(fin);

            ArrayList list = (ArrayList)ois.readObject();


            //datalist에서 email을 통해 데이터 얻어오기
            while(true){
                if(!list.isEmpty()){
                    player = (Player)list.get(i);
                    if(player.isThisEmail(email)) { //이메일을 비교하여 같은경우 break
                        break;
                    }
                }else {//list가 빈경우 break
                    break;
                }
                i++;
            }

            /*
            Book b1 = (Book)list.get(0);
            Book b2 = (Book)list.get(1);
            Book b3 = (Book)list.get(2);
            */


        }catch(Exception ex){

        }finally{
            try{
                ois.close();
                fin.close();
            }catch(IOException ioe){}
        }

        this.name = player.getName();
        this.totalScore = player.getTotalScore();

        this.violationAccel = player.getViolationAccel();
        this.violationVelocity = player.getViolationVelocity();
        this.violationKal = player.getViolationKal();
        this.useSleepinessCenter = player.getUseSleepinessCenter();

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
