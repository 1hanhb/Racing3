package com.example.munak.comptest;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import com.facebook.FacebookSdk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TimerTask;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Sensor accelerometer;
    SensorManager sensorManager;
    LocationManager locationManager;

    //permissionRequestCode
    private static final int REQUEST_EXTERNAL_STORAGE_CODE = 1;
    boolean permissionCheck = false;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent LoginToMainIntent = getIntent();
        email = LoginToMainIntent.getStringExtra("keyEmail");

        //START
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        //가속도계
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //속도측정 (LocationManager)
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        //위치정보 받아오기
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "locationmanager update failed", Toast.LENGTH_SHORT).show();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);

        //Edit Button Click Event
        Button mainEditButton = (Button) findViewById(R.id.mainEditButton);
        mainEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MainToEditIntent = new Intent(MainActivity.this, EditActivity.class);
                MainToEditIntent.putExtra("keyEmail",email);
                startActivity(MainToEditIntent);
            }
        });

        //Start Button Click Event
        Button mainStartButton = (Button) findViewById(R.id.mainStartButton);
        mainStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Racing racing = new Racing();

                InGameThread inGameThread = new InGameThread();
                InGameStatus.setStart(true);
                inGameThread.start();
                //racing.start();

                /*try {
                    InGameStatus.setStart(true);
                } catch(Exception e) {
                    Toast.makeText(MainActivity.this, "test1", Toast.LENGTH_SHORT).show();
                }

                try {
                    inGameThread.start();
                } catch(Exception e) {
                    Toast.makeText(MainActivity.this, "test2", Toast.LENGTH_SHORT).show();
                }

                try {
                    racing.start();
                } catch(Exception e) {
                    Toast.makeText(MainActivity.this, "test3", Toast.LENGTH_SHORT).show();
                }*/


            }
        });

        //Tmap Button Click Event
        Button mainTmapButton = (Button) findViewById(R.id.mainTmapButton);
        mainTmapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appPackageName = "com.skt.tmap.ku"; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

            }
        });

        //Record Button Click Event
        Button mainRecordButton = (Button) findViewById(R.id.mainRecordButton);
        mainRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MainToRecordIntent = new Intent(MainActivity.this, RecordActivity.class);
                MainToRecordIntent.putExtra("keyEmail",email);
                startActivity(MainToRecordIntent);
            }
        });

        //Rank Button Click Event
        Button mainRankButton = (Button) findViewById(R.id.mainRankButton);
        mainRankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MainToRankIntent = new Intent(MainActivity.this, RankActivity.class);
                MainToRankIntent.putExtra("keyEmail",email);
                startActivity(MainToRankIntent);
            }
        });

        //Twitter Button Click Event
        ImageButton mainTwitter = (ImageButton) findViewById(R.id.mainTwitter);
        mainTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareTwitter();
            }
        });

        //Line Button Click Event
        ImageButton mainLine = (ImageButton) findViewById(R.id.mainLine);
        mainLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLine();
            }
        });

        //Instagram Button Click Event
        ImageButton mainInstagram = (ImageButton) findViewById(R.id.mainInstagram);
        mainInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareInstagram();
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        ImageView profile = (ImageView) findViewById(R.id.profile);
        profile.setBackground(new ShapeDrawable(new OvalShape()));
        profile.setClipToOutline(true);
    }

    //START
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            InGameStatus.setVelocity(location.getSpeed()); //속도 값 저장
            InGameStatus.setLocationX(location.getLongitude()); //위도 경도 값 저장
            InGameStatus.setLocationY(location.getLatitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {

        InGameStatus.setAcceleration(event.values[1], event.values[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class InGameThread extends Thread{
        public void run(){
            //Toast.makeText(MainActivity.this, "ingamethread 실행", Toast.LENGTH_SHORT).show();
            //급가속 급감속에 따라 점수 차감 (-50점)
            //속도를 측정하고 위반하면 점수 차감 (-10점) / 적정속도일 경우 +1점 -> 제한속도 불러오기
            //졸음쉼터 (+1점) -> 졸음쉼터 위치 불러오기
            //칼치기 감지 쓰레드(국도일 때, 지정 시간동안 칼치기 감지함, -50점) -> IC

            while (InGameStatus.getStart()) {

                try{
                    Thread.sleep(1000);

                    //1. 급가속
                    //급가속 : 초당 11km/h ~ 25km/h를 넘었을 때 점수 차감
                    if( 3.05f <=InGameStatus.getAccelerationZ() ||  InGameStatus.getAccelerationZ() <=7.9f ) {
                        InGameStatus.setTotalScore(0, -50); // 점수 50 감소
                        InGameStatus.setViolationAccel(1); //가속도 위반 횟수 1 증가
                    }
                    //급감속 : 초당 7.5km/h ~ 40km/h 감속 운행한 경우
                    else if( InGameStatus.getAccelerationZ() <=- 2.08f || 11.1f<= InGameStatus.getAccelerationZ()  ) {
                        InGameStatus.setTotalScore(0, -50); // 점수 50 감소
                        InGameStatus.setViolationAccel(1); //가속도 위반 횟수 1 증가
                    }

                    //2. 과속
                    //현재 위치를 통해 도로 별 제한속도를 구한다(공공데이터 사용).
                    //임시로 110km/h로 지정하였음.
                    if(110.0f <= InGameStatus.getVelocity()){

                        InGameStatus.setTotalScore(0, -10); //점수 10 감소
                        InGameStatus.setViolationVelocity(1); // 속도 위반 횟수 1 증가

                    }
                    else if(15.0f <= InGameStatus.getVelocity()){
                        //규정 속도롤 지켰을 때는 1초마다 점수가 올라간다.
                        InGameStatus.setTotalScore(0, +1);
                    } // 위의 상황이 아닌 경우 점수 변동 없음


                    //3.졸음쉼터
                    //현재 졸음쉼터에 있는지 판단하는 메서드(isAtSleepinessArea) 호출
                    if(LocationOfSleepinessArea.isAtSleepinessArea(InGameStatus.getLocationX(),InGameStatus.getLocationY())){
                        InGameStatus.setTotalScore(0, +1); //1점 증가
                        InGameStatus.setUseSleepinessCenter(1); // 졸음쉼터 이용 횟수 1 증가
                    }


                    //4. 칼치기
                    //현재 국도에 위치한 경우
                    if(LocationOfIC.isOnNationalHighway(InGameStatus.getLocationX(),InGameStatus.getLocationY())){

                        // 옆쪽으로 가속하고 있는 경우 칼치기 카운트 증가
                        // 옆쪽 방향으로 가속이 3.0m/s^2 이상일 경우
                        if(2.0f<=Math.abs(InGameStatus.getAccelerationY())){
                            InGameStatus.setKalCount(1);
                        } else {
                            //옆으로 가속하지 않는 경우, 칼치기 카운트 감소.
                            //0 미만으로는 감소되지 않는다.
                            InGameStatus.setKalCount(-1);
                        }

                        //총 칼치기 카운트가 10 이상인 경우 칼치기로 간주하고, 점수 차감 및 칼치기 횟수 증가
                        if(10 <= InGameStatus.getKalCount()){
                            InGameStatus.setTotalScore(0, -50);
                            InGameStatus.setViolationKal(1);
                        }

                    }


                } catch (InterruptedException e){


                }

            }
        }
    }

    //Kakao Button Click Event
    public void shareKakao(View v) {
        try {
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            kakaoTalkLinkMessageBuilder.addText("[착한 레이싱]" +
                    "\n전국은 지금 착한 레이싱 열풍!" +
                    "\n신명나게 한 판 놀아보세~!" +
                    "\n아래 버튼을 터치하고, 착한" +
                    " 레이싱을 시작해보세요. 짜릿한 승부가" +
                    " 당신을 기다리고 있습니다!");

            kakaoTalkLinkMessageBuilder.addAppButton("착한 레이싱 다운받기");

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
    }

    //Facebook Button Click Event
    public void shareFacebook(View v)
    {
        ShareLinkContent content = new ShareLinkContent.Builder()
                //링크의 콘텐츠 제목
                .setContentTitle("페이스북 공유 링크입니다.")

                //게시물에 표시될 썸네일 이미지의 URL
                .setImageUrl(Uri.parse("https://lh3.googleusercontent.com/hmVeH1KmKDy1ozUlrjtYMHpzSDrBv9NSbZ0DPLzR8HdBip9kx3wn_sXmHr3wepCHXA=rw"))

                //공유될 링크
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.handykim.nbit.everytimerfree"))

                //게일반적으로 2~4개의 문장으로 구성된 콘텐츠 설명
                .setContentDescription("문장1, 문장2, 문장3, 문장4")
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(content, ShareDialog.Mode.FEED);   //AUTOMATIC, FEED, NATIVE, WEB 등이 있으며 이는 다이얼로그 형식을 말합니다.
    }

    public void shareTwitter() {
        String strLink = null;
        try {
            strLink = String.format("http://twitter.com/intent/tweet?text=%s", URLEncoder.encode("공유할 텍스트를 입력하세요", "utf-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strLink));
        startActivity(intent);
    }

    public void shareLine()
    {
        try {
            String text = "line://msg/text/" + "메시지를 입력하세요";
            text = text.replaceAll("\n", "");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(text));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //앱 미설치 시
            String appPackageName = "jp.naver.line.android"; // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shareInstagram()
    {
        //외부저장 권한 요청(안드로이드 6.0 이후 필수)
        onRequestPermission();

        if(permissionCheck) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.image);
            String storage = Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "이미지명.png";

            String folderName = "/폴더명/";
            String fullPath = storage + folderName;
            File filePath;

            try {
                filePath = new File(fullPath);
                if (!filePath.isDirectory()) {
                    filePath.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(fullPath + fileName);
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }


            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            Uri uri = Uri.fromFile(new File(fullPath, fileName));
            try {
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.putExtra(Intent.EXTRA_TEXT, "텍스트는 지원하지 않음!");
                share.setPackage("com.instagram.android");
                startActivity(share);
            } catch (ActivityNotFoundException e) {
                String appPackageName = "com.instagram.android"; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));

                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));

                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    public void onRequestPermission()
    {
        int permissionReadStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionReadStorage == PackageManager.PERMISSION_DENIED || permissionWriteStorage == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_CODE);
        }
        else {
            permissionCheck = true; //이미 허용되어 있으므로 PASS
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            //Toast.makeText(this, "허용했으니 가능함", Toast.LENGTH_LONG).show();
                            permissionCheck = true;
                        } else {
                            //Toast.makeText(this, "허용하지 않으면 공유 못함 ㅋ", Toast.LENGTH_LONG).show();
                            permissionCheck = false;
                        }
                    }
                }
                break;
        }
    }

    //
    public class Racing extends Thread implements TextToSpeech.OnInitListener{
        int count=0;
        int stop=0;
        boolean myWin = false;
        int myScore=0;
        int yourScore=0;
        Player player;
        TextToSpeech ttsClient;

        public void run() {
            player = new Player(email);
            ttsClient = new TextToSpeech(getApplicationContext(),this);

            speakTTS("Start the game now");


            while(true){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                player.getData();
                yourScore = yourScore + (int)((player.getTotalScore()-myScore) * Math.random() * 2);
                myScore = player.getTotalScore();

                if(count == 5){
                    //본인 스코어, 상대 스코어 음성으로
                    stop++;

                    if(stop==20) {
                        if(myWin) {
                            speakTTS("game is over. you are winner");
                        }
                        else {
                            speakTTS("game is over. you are loser");
                        }

                        InGameStatus.setStart(false);
                        break;
                    }

                }
                else {
                    //역전시 음성
                    if(myWin && (myScore<yourScore)) {//본인이 이기고있다가 역전당한경우
                        speakTTS("You have less score");
                    }
                    else if(myScore == yourScore) {//동점이 된 경우
                        speakTTS("you have equal score");
                    }
                    else if(!myWin && (myScore > yourScore)) {//본인이 지고있다가 역전한경우
                        speakTTS("You reversed the game");
                    }
                }
                count = (count+1)%6;
            }

        }

        @Override
        public void onInit(int i) {
            //ttsClient.speak("TTS",TextToSpeech.QUEUE_FLUSH, null);
        }

        public void speakTTS(String text) {
            AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
            ttsClient.speak(text, TextToSpeech.QUEUE_FLUSH,null);
/*
            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

            if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                int ttsResult = ttsClient.speak(text, TextToSpeech.QUEUE_FLUSH,null);

                TimerTask task = new TimerTask(){
                    @Override
                    public void run(){
                        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
                        am.abandonAudioFocus(null);
                    }
                };
                new Timer().schedule(task, 3000);
            }*/
        }
    }
}
