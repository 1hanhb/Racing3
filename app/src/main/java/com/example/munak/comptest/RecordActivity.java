package com.example.munak.comptest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView recordText;

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

                break;
            }
        }


        nameText.setText("이름 : "+name+"\n이메일 : "+email);
        //계급 구하기
        int integerMmr = Integer.valueOf(mmr);
        String tier;
        if(integerMmr<100)
            tier = "BRONZE";
        else if(integerMmr<200)
            tier = "SILVER";
        else if(integerMmr<300)
            tier ="GOLD";
        else if(integerMmr<400)
            tier = "PLATINUM";
        else if(integerMmr<500)
            tier = "MASTER";
        else
            tier = "ERROR";


        nameText.setText("이름 :"+ name +"\n이메일 : "+emailFromDB);
        rankText.setText("계급 : " + tier);
        velocityText.setText("\n총 속도 위반 횟수 : "+ Integer.valueOf(violationVelocity)/60 +"(분)\n총 가속도 위반 횟수 : "+Integer.valueOf(violationAccel)/60+"(분)\n게임 당 평균 속도 위반 횟수 : "+
                Integer.valueOf(violationVelocity)/Integer.valueOf(competitionCount)/60+"(분)\n게임 당 평균 가속도 위반 횟수 :"+
                Integer.valueOf(violationAccel)/Integer.valueOf(competitionCount)/60+"(분)");
        sleepText.setText("총 졸음쉼터 이용 횟수 : "+ Integer.valueOf(useSleepinessCenter)/60 +"(분)\n 게임 당 평균 졸음쉼터 이용 횟수 :"+
                Integer.valueOf(useSleepinessCenter)/Integer.valueOf(competitionCount)/60 +"(분)");
        violationText.setText("총 칼치기 횟수 : "+ Integer.valueOf(violationKal)/60 +"(분)\n게임 당 평균 칼치기 횟수 :"+Integer.valueOf(violationKal)/Integer.valueOf(competitionCount)/60+"(분)");


        //Title Bar Back Button Visible
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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