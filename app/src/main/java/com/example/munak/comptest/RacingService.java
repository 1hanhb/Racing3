package com.example.munak.comptest;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class RacingService extends Service{

    String email;

    //SoundManager 정의
    SoundManager sManager;
    boolean restartable = true;
    boolean first = true;

    boolean test = true;

    int count=0;
    boolean myWin = false;
    int myScore=0;
    int yourScore=0;
    int stop=0;

    public RacingService() {

    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what ==1){
                Toast.makeText(RacingService.this, "상대방 찾는중", Toast.LENGTH_SHORT).show();
            }else if(msg.what ==2) {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                }

                int a = (int) (Math.random() * 4);
                switch (a) {// 송중기, 남궁민, 원빈, Emma1, Emma2
                    case 0:
                        Toast.makeText(RacingService.this, "송중기님과 게임을 시작합니다", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(RacingService.this, "남궁민님과 게임을 시작합니다", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(RacingService.this, "원빈님과 게임을 시작합니다", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(RacingService.this, "Emma님과 게임을 시작합니다", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent,int flag ,int startId) {
        Log.d("slog","onStartCommand()");
        super.onStartCommand(intent,flag, startId);

        //효과음 파일 로드
        sManager = SoundManager.getInstance();
        sManager.init(this);

        sManager.addSound(0, R.raw.sonar); //매칭
        sManager.addSound(1, R.raw.victory2); //승리 시
        sManager.addSound(2, R.raw.lose3); //패배 시
        sManager.addSound(3, R.raw.ding); //위반1
        sManager.addSound(4, R.raw.up); //증가
        sManager.addSound(5, R.raw.down); //빠져나감

        //내용
        email = intent.getStringExtra("keyEmail");
        //Toast.makeText(this, "email : " + email, Toast.LENGTH_SHORT).show();

        StartThread startThread = new StartThread(mHandler);
        startThread.start();

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy(){
        Log.d("slog","onDestroy()");
        super.onDestroy();

        InGameStatus.setStart(false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }


    public class Racing extends Thread implements TextToSpeech.OnInitListener{


        final private String DBNAME = "playerinfo.db";
        final private String PLAYERTABLE = "player";
        SQLiteDatabase db;
        boolean createdDB = false;

        Player player;
        TextToSpeech ttsClient;

        public void run() {
            ttsClient = new TextToSpeech(getApplicationContext(),this);

            try{
                sManager.play(0);
                Thread.sleep(3000);
            }catch(Exception e){}

            if(first) {
                speakTTS("착한 레이싱을 시작하겠습니다");
                first = false;
            }

            player = new Player(email);



            while(InGameStatus.getStart()){
                if(InGameStatus.getStopSwitch()){
                    try{
                        Thread.sleep(1000);
                    }catch(Exception e){}

                    speakTTS("게임을 종료합니다");
                    break;
                }


                try {
                    Thread.sleep(2000);
                    if (InGameStatus.getStopSwitch()) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                        }

                        speakTTS("게임을 종료합니다");
                        break;
                    }
                    Thread.sleep(2000);
                    if (InGameStatus.getStopSwitch()) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                        }

                        speakTTS("게임을 종료합니다");
                        break;
                    }
                    Thread.sleep(2000);
                    if (InGameStatus.getStopSwitch()) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                        }

                        speakTTS("게임을 종료합니다");
                        break;
                    }
                    Thread.sleep(2000);
                    if (InGameStatus.getStopSwitch()) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                        }

                        speakTTS("게임을 종료합니다");
                        break;
                    }
                    Thread.sleep(2000);
                    if (InGameStatus.getStopSwitch()) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                        }

                        speakTTS("게임을 종료합니다");
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                player.getData();
                if(test){
                    test = false;
                    yourScore = 30;
                }
                yourScore = yourScore + (int)((player.getTotalScore()-yourScore) * Math.random() * 2);
                myScore = player.getTotalScore();

                if(count==4){
                    //본인 스코어, 상대 스코어 음성으로
                    int dif = myScore - yourScore;

                    try{
                        Thread.sleep(3000);
                    }catch(Exception ee){}

                    speakTTS(myScore +"점 대 " + yourScore +"점");


                    if(InGameStatus.getStopSwitch()){
                        try{
                            Thread.sleep(1000);
                        }catch(Exception e){}

                        speakTTS("게임을 종료합니다");
                        break;
                    }


                    stop++;
                    if(stop==4){//stop 1당 1분
                        InGameStatus.setStart(false);

                        if(myWin) {
                            createDatabase(DBNAME);
                            updateData(player, true);
                            try {
                                Thread.sleep(3000);
                                sManager.play(1);
                            } catch (Exception e) {

                            }
                            speakTTS("승리");
                        }

                        else {
                            createDatabase(DBNAME);
                            updateData(player,false);
                            try {
                                Thread.sleep(3000);
                                sManager.play(2);
                            } catch(Exception e){

                            }
                            speakTTS("패배");
                        }

                        break;
                    }


                } else {
                    //역전시 음성
                    if(myScore == yourScore ){//동점이 된 경우
                        try{
                            sManager.play(3);
                            Thread.sleep(2000);
                        }catch(Exception e){}
                        equalScoring();
                        myWin = false;
                    }else if(myWin && (myScore<yourScore)){//본인이 이기고있다가 역전당한경우
                        try{
                            sManager.play(5);
                            Thread.sleep(2000);
                        }catch(Exception e){}
                        losingGame();
                        myWin = false;
                    }else if(!myWin && (myScore > yourScore) ){//본인이 지고있다가 역전한경우
                        try{
                            sManager.play(4);
                            Thread.sleep(2000);
                        }catch(Exception e){}
                        winningGame();

                        myWin = true;
                    }
                    if(InGameStatus.getStopSwitch()){
                        try{
                            Thread.sleep(1000);
                        }catch(Exception e){}

                        speakTTS("게임을 종료합니다");
                        break;
                    }
                }
                count = (count+1)%5;
            }
            restartable = true;
        }

        public void losingGame(){
            //myScore < yourScore
            int a;
            a = (int)(Math.random()*4)+1;
            switch(a) {
                case 1:
                    speakTTS("상대방 점수는 " + yourScore+ "점으로 역전당했습니다");
                    break;
                case 2:
                    speakTTS("뒷좌석에 할머니 할아버지가 타고계신다 생각하고 운전해보세요");
                    break;
                case 3:
                    speakTTS("패배의 기운이 느껴집니다");
                    break;
                case 4:
                    speakTTS("분발하십시오");
                    break;
            }




        }
        public void winningGame(){
            // myScore > yourScore
            int a;
            a = (int)(Math.random()*4)+1;
            switch(a) {
                case 1:
                    speakTTS("당신의 점수는 " + myScore + "점으로 역전했습니다");
                    break;
                case 2:
                    speakTTS("이대로만 간다면 승리는 문제 없습니다");
                    break;
                case 3:
                    speakTTS("승리의 기운이 느껴집니다");
                    break;
                case 4:
                    speakTTS("방심은 금물입니다");
                    break;
            }

        }
        public void equalScoring(){
            //myScore == yourScore
            int a;
            a = (int)(Math.random()*1)+1;
            switch(a) {
                case 1:
                    speakTTS("동점입니다");
                    break;
                case 2:
                    speakTTS("");
                    break;
                case 3:
                    speakTTS("");
                    break;
                case 4:
                    speakTTS("");
                    break;
            }

        }


        @Override
        public void onInit(int i){
            //ttsClient.speak("TTS",TextToSpeech.QUEUE_FLUSH, null);
        }

        private void speakTTS(String text){
            AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);

            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

            if( result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                int ttsResult = ttsClient.speak(text, TextToSpeech.QUEUE_FLUSH,null);

                TimerTask task = new TimerTask(){
                    @Override
                    public void run(){
                        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
                        am.abandonAudioFocus(null);
                    }
                };
                new Timer().schedule(task, 3000);
            }
        }


        //db 생성
        private void createDatabase(String name){
            try {
                db = openOrCreateDatabase(name, MODE_ENABLE_WRITE_AHEAD_LOGGING,null);
                createdDB = true;
                try {
                    if(createdDB) {
                        createTable(PLAYERTABLE);
                    }
                } catch(Exception e){}
            } catch(Exception ex) {}
        }

        //table 생성
        private void createTable(String name) {
            if(createdDB) {
                try {
                    db.execSQL("create table " + name + "("
                            + "email text primary key,"
                            + "name text,"
                            + "password text,"
                            + "totalScore integer,"
                            + "violationAccel integer,"
                            + "violationVelocity integer,"
                            + "violationKal integer,"
                            + "useSleepinessCenter integer,"
                            + "mmr integer,"
                            + "conpetitionCount integer,"
                            + "winCount integer,"
                            + "image blob,"
                            + "mission integer)"
                    );
                }catch(Exception e){}
            }
        }

        //data 변경하기
        private void updateData(Player p, boolean winner){
            if(winner){
                String sql = "UPDATE " + PLAYERTABLE
                        + " SET totalScore = totalScore + '" + InGameStatus.getTotalScore()
                        + "', violationAccel = violationAccel + '" + InGameStatus.getViolationAccel()
                        + "', violationVelocity = violationVelocity + '" + InGameStatus.getViolationVelocity()
                        + "', violationKal = violationKal +'" + InGameStatus.getViolationKal()
                        + "', useSleepinessCenter = useSleepinessCenter +'" + InGameStatus.getUseSleepinessCenter()
                        + "', mmr = mmr +'" + 10
                        + "', conpetitionCount = conpetitionCount + '" + 1
                        + "', winCount = winCount + '" + 1
                        + "', mission = mission + '" + InGameStatus.getMission() + "'"
                        + " WHERE email = '"+ email +"';";
                try {
                    db.execSQL(sql);
                }catch(Exception e){}
            }
            else if(!winner){
                String sql = "UPDATE " + PLAYERTABLE
                        + " SET totalScore = totalScore + '" + InGameStatus.getTotalScore()
                        + "', violationAccel = violationAccel + '" + InGameStatus.getViolationAccel()
                        + "', violationVelocity = violationVelocity + '" + InGameStatus.getViolationVelocity()
                        + "', violationKal = violationKal +'" + InGameStatus.getViolationKal()
                        + "', useSleepinessCenter = useSleepinessCenter +'" + InGameStatus.getUseSleepinessCenter()
                        + "', mmr = mmr -'" + 5
                        + "', conpetitionCount = conpetitionCount + '" + 1
                        + "', winCount = winCount + '" + 0
                        + "', mission = mission + '" + InGameStatus.getMission() + "'"
                        + " WHERE email = '"+ email +"';";
                try {
                    db.execSQL(sql);
                }catch(Exception e){}
            }
        }
    }

    class InGameThread extends Thread {
        public void run() {

            //Toast.makeText(MainActivity.this, "ingamethread 실행", Toast.LENGTH_SHORT).show();
            //급가속 급감속에 따라 점수 차감 (-50점)
            //속도를 측정하고 위반하면 점수 차감 (-10점) / 적정속도일 경우 +1점 -> 제한속도 불러오기
            //졸음쉼터 (+1점) -> 졸음쉼터 위치 불러오기
            //칼치기 감지 쓰레드(국도일 때, 지정 시간동안 칼치기 감지함, -50점) -> IC
            int timeCount = 0;
            int driveCount = 0;
            while (InGameStatus.getStart()) {

                try{
                    timeCount++;

                    InGameStatus.setPrevVelocity(InGameStatus.getCurrVelocity());
                    InGameStatus.setCurrVelocity(InGameStatus.getVelocity());
                    InGameStatus.setAcceleration(InGameStatus.getCurrVelocity()-InGameStatus.getPrevVelocity());
                    //가속도지정

                    Thread.sleep(1000);

                    //1. 급가속
                    // 급가속 : 초당 11km/h ~ 25km/h를 넘었을 때 점수 차감
                    //  (5.21.)최소 15km/h로 지정
                    if(4 < timeCount &&  4.05f <=InGameStatus.getAcceleration() &&  InGameStatus.getAcceleration() <=7.9f  ) {
                        InGameStatus.setTotalScore(0, -50); // 점수 50 감소
                        InGameStatus.setViolationAccel(1); //가속도 위반 횟수 1 증가
                    }
                    //급감속 : 초당 7.5km/h ~ 40km/h 감속 운행한 경우
                    //  (5.21.) 최소 -11km/h로 지정
                    else if(4 < timeCount &&  InGameStatus.getAcceleration() <=- 3.08f && -11.1f<= InGameStatus.getAcceleration() ) {
                        InGameStatus.setTotalScore(0, -50); // 점수 50 감소
                        InGameStatus.setViolationAccel(1); //가속도 위반 횟수 1 증가
                    }

                    //2. 과속
                    // 현재 위치를 통해 도로 별 제한속도를 구한다(공공데이터 사용).
                    // 임시로 110km/h로 지정하였음.
                    // 110km/h = 30.5555m/s
                    if(30.5555f <= InGameStatus.getVelocity()) {
                        InGameStatus.setTotalScore(0, -10); //점수 10 감소
                        InGameStatus.setViolationVelocity(1); // 속도 위반 횟수 1 증가
                    }
                    //15km/h = 4.1666 m/s
                    else if(4.16666f <= InGameStatus.getVelocity()) {
                        //규정 속도롤 지켰을 때는 1초마다 점수가 올라간다.
                        InGameStatus.setTotalScore(0, +10);
                        driveCount++;
                    } // 위의 상황이 아닌 경우 점수 변동 없음

                    //3.졸음쉼터
                    //현재 졸음쉼터에 있는지 판단하는 메서드(isAtSleepinessArea) 호출
                    if(LocationOfSleepinessArea.isAtSleepinessArea(InGameStatus.getLocationX(),InGameStatus.getLocationY())) {
                        InGameStatus.setTotalScore(0, +10); //1점 증가
                        InGameStatus.setUseSleepinessCenter(1); // 졸음쉼터 이용 횟수 1 증가
                    }

                    //4. 칼치기
                    //현재 국도에 위치한 경우
                    if(LocationOfIC.isOnNationalHighway(InGameStatus.getLocationX(),InGameStatus.getLocationY())) {

                        // 옆쪽으로 가속하고 있는 경우 칼치기 카운트 증가
                        // 옆쪽 방향으로 가속이 3.0m/s^2 이상일 경우
                        if(2.0f<=Math.abs(InGameStatus.getAccelerationY())) {
                            InGameStatus.setKalCount(1);
                        } else {
                            //옆으로 가속하지 않는 경우, 칼치기 카운트 감소.
                            //0 미만으로는 감소되지 않는다.
                            InGameStatus.setKalCount(-1);
                        }

                        //총 칼치기 카운트가 10 이상인 경우 칼치기로 간주하고, 칼치기 횟수 증가
                        if(10 <= InGameStatus.getKalCount()){
                            InGameStatus.setViolationKal(1);
                        }
                    }

                    //미션 성공 조건 : 5분 이상의 주행 동안 가속도 위반 횟수 5번을 넘지 않는다.
                    if(InGameStatus.getMission() == 0 && InGameStatus.getViolationAccel() < 5 &&  300 < driveCount) {
                        InGameStatus.setMission();
                    }

                } catch (InterruptedException e) {

                }
            }
        }
    }



    public class StartThread extends Thread{
        Handler handler;
        public StartThread(Handler mhandler){
            handler = mhandler;
        }
        public void run() {
            initializeVariables1();

            while(!InGameStatus.getStopSwitch()){
                if (restartable) {
                    initializeVariables2();
                    searchingGame();

                    InGameThread inGameThread = new InGameThread();
                    Racing racing = new Racing();

                    InGameStatus.setStart(true);
                    InGameStatus.initialize();

                    inGameThread.start();
                    racing.start();
                }else{
                    try{
                        Thread.sleep(1000);
                    }catch(Exception e){}
                }
            }
        }
        public void searchingGame(){
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
            try{
                Thread.sleep(5000);
            }catch(Exception e){}

            Message msg2 = new Message();
            msg2.what = 2;
            handler.sendMessage(msg2);
            try{
                Thread.sleep(3000);
            }catch(Exception ee){}
        }

        public void initializeVariables1(){
            InGameStatus.setStopSwitch(false);
            restartable = true;
            first = true;

            count=0;
            myWin = false;
            myScore=0;
            yourScore=0;
            stop=0;
        }

        public void initializeVariables2(){
            restartable = false;

            count=0;
            myWin = false;
            myScore=0;
            yourScore=0;
            stop=0;
        }

    }
}