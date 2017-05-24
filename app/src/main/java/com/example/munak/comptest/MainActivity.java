package com.example.munak.comptest;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import com.facebook.FacebookSdk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TimerTask;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    int RESULT_FROM_EDIT = 11;

    Sensor accelerometer;
    SensorManager sensorManager;
    LocationManager locationManager;

    //permissionRequestCode
    private static final int REQUEST_EXTERNAL_STORAGE_CODE = 1;
    boolean permissionCheck = false;

    String email;
    boolean isStart = false;

    Button mainStartButton;

    Bitmap image;
    ImageView profile;
    byte[] arr;
    byte[] byteArray = null;

    boolean sendOk = false;

    final private String DBNAME = "playerinfo.db";
    final private String PLAYERTABLE = "player";
    SQLiteDatabase db;
    boolean createdDB = false;


    String name;
    String totalScore;
    String violationAccel;
    String violationVelocity;
    String violationKal;
    String useSleepinessCenter;
    String mmr;
    String competitionCount;
    String winCount;
    String tier;
    String tierDef;
    byte[] photo;

    TextView mainRankTV;
    TextView mainNameTV;
    ImageView profileEdge;

    //Slide Menu
    private final String[] navItems = {"Brown", "Cadet Blue", "Dark Olive Green", "Dark Orange", "Golden Rod"};
    private ListView lvNavList;
    private LinearLayout flContainer;
    private DrawerLayout dlDrawer;
/*
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);

        finish();

        android.os.Process.killProcess(android.os.Process.myPid());

//        if (dlDrawer.isDrawerOpen(lvNavList)) {
//            dlDrawer.closeDrawer(lvNavList);
//
//        } else {
//            super.onBackPressed();
//        }
    }
*/

    /*public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed(); //지워야 실행됨

        Builder d = new AlertDialog.Builder(this);

        d.setMessage("정말 종료하시겠습니까?");

        d.setPositiveButton("예", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // process전체 종료
                finish();
            }
        });

        d.setNegativeButton("아니요", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        d.show();
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Slide Menu
        lvNavList = (ListView)findViewById(R.id.lv_activity_main_nav_list);
        flContainer = (LinearLayout)findViewById(R.id.fl_activity_main_container);

        //keyE-Mail
        Intent LoginToMainIntent = getIntent();
        email = LoginToMainIntent.getStringExtra("keyEmail");

        profile = (ImageView) findViewById(R.id.profile);

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

                if(sendOk == true) {
                    Bitmap sendBitmap = ((BitmapDrawable) profile.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    sendBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byteArray = stream.toByteArray();
                    MainToEditIntent.putExtra("image1", byteArray);
                }

                startActivityForResult(MainToEditIntent, RESULT_FROM_EDIT);
                //startActivity(MainToEditIntent);
            }
        });


        //Start Button Click Event
        mainStartButton = (Button) findViewById(R.id.mainStartButton);
        mainStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isStart) {
                    isStart = true;

                    Toast.makeText(MainActivity.this, "game start", Toast.LENGTH_SHORT).show();

                    mainStartButton.setBackgroundResource(R.drawable.test_button_img_12);

                    Intent Service = new Intent(MainActivity.this, RacingService.class);
                    Service.putExtra("keyEmail",email);
                    startService(Service);

                } else {
                    isStart = false;

                    mainStartButton.setBackgroundResource(R.drawable.test_button_img_11);
                    Intent Service = new Intent(MainActivity.this, RacingService.class);
                    Service.putExtra("keyEmail",email);
                    stopService(Service);
                }
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

        createDatabase(DBNAME);
        String sql = "select * from " + PLAYERTABLE;
        Cursor cursor = db.rawQuery(sql, null);

        int count = cursor.getCount();
        String emailFromDB="";
        for (int i = 0; i < count; i++) {
            cursor.moveToNext();

            emailFromDB = cursor.getString(0);
            if(emailFromDB.equals(email)) {
                name = cursor.getString(1);
                mmr = cursor.getString(8);
                competitionCount = cursor.getString(9);
                winCount = cursor.getString(10);
                photo = cursor.getBlob(11);

                break;
            }
        }

        mainNameTV = (TextView) findViewById(R.id.mainNameTV);
        mainNameTV.setText(String.valueOf(name));

        /*
        if(photo != null) {
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
            Bitmap bm = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            profile.setImageBitmap(bm);
        }
        else {
            Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
        }
        */

        mainRankTV = (TextView) findViewById(R.id.mainRankTV);
        profileEdge = (ImageView) findViewById(R.id.profileEdge);

        int integerMmr = Integer.valueOf(mmr);
        if(integerMmr<100){
            tier = "BRONZE";
            profileEdge.setImageResource(R.drawable.rank_img_bronze);
            mainRankTV.setTextColor(Color.rgb(197,135,52));
        }
        else if(integerMmr<200){
            tier = "SILVER";
            profileEdge.setImageResource(R.drawable.rank_img_silver);
            mainRankTV.setTextColor(Color.rgb(192,192,192));
        }
        else if(integerMmr<300) {
            tier = "GOLD";
            profileEdge.setImageResource(R.drawable.rank_img_gold);
            mainRankTV.setTextColor(Color.rgb(244,228,83));
        }
        else if(integerMmr<400) {
            tier = "PLATINUM";
            profileEdge.setImageResource(R.drawable.ranl_img_platinum);
            mainRankTV.setTextColor(Color.rgb(108,244,170));
        }
        else if(integerMmr<500) {
            tier = "MASTER";
            profileEdge.setImageResource(R.drawable.rank_img_master);
            mainRankTV.setTextColor(Color.rgb(65,49,255));
        }
        else
            tier = "ERROR";

        mainRankTV.setText(tier);

        profile.setBackground(new ShapeDrawable(new OvalShape()));
        profile.setClipToOutline(true);




        //test button click event
        Button testButton = (Button) findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });
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

    private void createDatabase(String name){
        try{

            db = openOrCreateDatabase(name, MODE_ENABLE_WRITE_AHEAD_LOGGING,null);
            createdDB = true;

        }catch(Exception ex){
            Toast.makeText(this, "db 생성 안됨", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            //Toast.makeText(MainActivity.this, "결과가 성공이 아님.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == RESULT_FROM_EDIT) {
            arr = data.getByteArrayExtra("image2");

            image = BitmapFactory.decodeByteArray(arr, 0, arr.length);

            profile.setImageBitmap(image);

            sendOk = true;


        } else {
            //Toast.makeText(MainActivity.this, "RESULT_FROM_EDIT 아님", Toast.LENGTH_SHORT).show();
        }
    }

}
