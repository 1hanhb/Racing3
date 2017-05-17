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
        recordText = (TextView) findViewById(R.id.recordText);

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

        recordText.setText("name : " +  name+
                "\nname : " +  totalScore+
                "\nviolationAccel : " + violationAccel +
                "\nviolationVelocity : " + violationVelocity +
                "\nviolationKal : " + violationKal+
                "\nuseSleepinessCenter : " +  useSleepinessCenter+
                "\nmmr : " +  mmr+
                "\ncompetitionCount : " +  competitionCount+
                "\nwinCount : " +  winCount
        );



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