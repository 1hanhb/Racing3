package com.example.munak.comptest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

/**
 * Created by Munak on 2017. 4. 9..
 */

public class RecordActivity extends AppCompatActivity {

    // 자랑하기
    //
    final private String DBNAME = "playerinfo.db";
    final private String PLAYERTABLE = "player";
    SQLiteDatabase db;
    boolean createdDB = false;

    TextView nameText;
    TextView rankText;
    TextView velocityText;
    TextView sleepText;
    TextView violationText;
    TextView missionText;

    String email;
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
    int mission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Intent MainToRecordIntent = getIntent();
        email = MainToRecordIntent.getStringExtra("keyEmail");

        nameText = (TextView) findViewById(R.id.nameText);
        rankText = (TextView) findViewById(R.id.rankText);
        velocityText = (TextView) findViewById(R.id.velocityText);
        sleepText = (TextView) findViewById(R.id.sleepText);
        violationText = (TextView) findViewById(R.id.violationText);
        missionText = (TextView) findViewById(R.id.missionText);

        createDatabase(DBNAME);
        String sql = "select * from " + PLAYERTABLE;
        Cursor cursor = db.rawQuery(sql, null);

        int count = cursor.getCount();
        String emailFromDB="";
        for (int i = 0; i < count; i++) {
            cursor.moveToNext();

            emailFromDB = cursor.getString(0);
            if(emailFromDB.equals(email)) {
                totalScore = cursor.getString(3);
                name = cursor.getString(1);
                violationAccel = cursor.getString(4);
                violationVelocity = cursor.getString(5);
                violationKal = cursor.getString(6);
                useSleepinessCenter = cursor.getString(7);
                mmr = cursor.getString(8);
                competitionCount = cursor.getString(9);
                winCount = cursor.getString(10);
                mission = cursor.getInt(12);

                break;
            }
        }


        nameText.setText("이름 : "+name+"\n이메일 : "+email);
        //계급 구하기
        int integerMmr = Integer.valueOf(mmr);
        if(integerMmr<100){
            tier = "BRONZE";
            tierDef = "\n하위 10% 정도의 초보 레이서 입니다. 분발하세요!!";
            rankText.setTextColor(Color.rgb(197,135,52));
        }
        else if(integerMmr<200){
            tier = "SILVER";
            tierDef = "\n하위 30% 정도의 미숙한 레이서 입니다.\n노력 하실 거라 믿습니다!";
            rankText.setTextColor(Color.rgb(192,192,192));
        }
        else if(integerMmr<300) {
            tier = "GOLD";
            tierDef = "\n상위 50% 정도의 일반적인 레이서입니다.";
            rankText.setTextColor(Color.rgb(244,228,83));
        }
        else if(integerMmr<400) {
            tier = "PLATINUM";
            tierDef = "\n상위 30% 정도의 우수한 레이서 입니다. 굉장하군요!";
            rankText.setTextColor(Color.rgb(108,244,170));
        }
        else if(integerMmr<500) {
            tier = "MASTER";
            tierDef = "\n상위 10% 정도의 '착한 레이서' 입니다.\n완벽한 운전실력을 가지셨군요!";
            rankText.setTextColor(Color.rgb(65,49,255));
        }
        else
            tier = "ERROR";


        nameText.setText("이름 :"+ name +"\n이메일 : "+emailFromDB + "\n전적 : "+Integer.valueOf(competitionCount)+"전 "+
                Integer.valueOf(winCount) +"승 "+(Integer.valueOf(competitionCount)-Integer.valueOf(winCount)) +"패"
                +"("+ (Integer.valueOf(competitionCount) == 0 ? "0.0" : String.format("%.1f", (double)Integer.valueOf(winCount)/Integer.valueOf(competitionCount)*100))+ "%)");
        rankText.setText("계급 : " + tier);

        double avgAccelViolation = Integer.valueOf(competitionCount) == 0 ?  0.0f: (double)Integer.valueOf(violationAccel)/Integer.valueOf(competitionCount);
        double avgVelViolation = Integer.valueOf(competitionCount) == 0 ?  0.0f: (double)Integer.valueOf(violationVelocity)/Integer.valueOf(competitionCount);
        double avgKalViolation = Integer.valueOf(competitionCount) == 0 ?  0.0f: (double)Integer.valueOf(violationKal)/Integer.valueOf(competitionCount);
        double avgSleep = Integer.valueOf(competitionCount) == 0 ?  0.0f: (double)Integer.valueOf(useSleepinessCenter)/Integer.valueOf(competitionCount);

        velocityText.setText("\n총 속도 위반 횟수 : "+ Integer.valueOf(violationVelocity) +"회\n총 가속도 위반 횟수 : "+Integer.valueOf(violationAccel)+
                "회\n평균 속도 위반 횟수 : "+ String.format("%.2f", avgVelViolation)+"회\n평균 가속도 위반 횟수 :"+ String.format("%.2f", avgAccelViolation) +"회");
        sleepText.setText("총 졸음쉼터 이용 횟수 : "+ Integer.valueOf(useSleepinessCenter) +"회\n평균 졸음쉼터 이용 횟수 :"+ String.format("%.2f", avgSleep) +"회");
        violationText.setText("총 칼치기 횟수 : "+ Integer.valueOf(violationKal) +"회\n평균 칼치기 횟수 :"+ String.format("%.2f", avgKalViolation)+"회");
        missionText.setText("오늘의 미션! 완수 :"+ mission +"회");

        //Title Bar Back Button Visible
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //Kakao Button Click Event
    public void shareKakao(View v) {
        try {
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            kakaoTalkLinkMessageBuilder.addText("[착한 레이싱] " +
                    name +"님의 운전등급은 \"" + tier +"\" 입니다." + tierDef + "\n올바른 운전문화를 만들어가는 착한 레이싱!\n모두 동참해주세요!"
            );

            kakaoTalkLinkMessageBuilder.addAppButton("착한 레이싱 다운받기");

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}