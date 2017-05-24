package com.example.munak.comptest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

/**
 * Created by Munak on 2017. 4. 9..
 */

public class RankActivity extends AppCompatActivity {
    String email;

    final private String DBNAME = "playerinfo.db";
    final private String PLAYERTABLE = "player";
    SQLiteDatabase db;
    boolean createdDB = false;

    String name;
    String photoPath;
    String photoFile;
    int mmr;

    byte[] getImage2;
    Bitmap saveImage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        //Title Bar Back Button Visible
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent MainToRecordIntent = getIntent();
        email = MainToRecordIntent.getStringExtra("keyEmail");
        getImage2 = MainToRecordIntent.getByteArrayExtra("image3");

        TextView rankName06 = (TextView) findViewById(R.id.rankName06);
        TextView rankTier06 = (TextView) findViewById(R.id.rankTier06);

        ImageView rank01 = (ImageView) findViewById(R.id.rankProfImg01);
        ImageView rank02 = (ImageView) findViewById(R.id.rankProfImg02);
        ImageView rank03 = (ImageView) findViewById(R.id.rankProfImg03);
        ImageView rank04 = (ImageView) findViewById(R.id.rankProfImg04);
        ImageView rank05 = (ImageView) findViewById(R.id.rankProfImg05);
        ImageView rank06 = (ImageView) findViewById(R.id.rankProfImg06);

        ImageView rankEdge06 = (ImageView) findViewById(R.id.rankProfEdgeImg06);


        if(getImage2 != null) {
            //Toast.makeText(this, "ok1 : " + getImage.toString(), Toast.LENGTH_SHORT).show();
            saveImage2 = BitmapFactory.decodeByteArray(getImage2, 0, getImage2.length);
            rank06.setImageBitmap(saveImage2);
        }


        createDatabase(DBNAME);
        getDataFromDB();

        rankName06.setText(name);
        if(mmr<=100) {
            rankTier06.setText("BRONZE");
            rankEdge06.setImageResource(R.drawable.rank_img_bronze);
        }
        else if(mmr<=200) {
            rankTier06.setText("SILVER");
            rankEdge06.setImageResource(R.drawable.rank_img_silver);
        }
        else if(mmr<=300) {
            rankTier06.setText("GOLD");
            rankEdge06.setImageResource(R.drawable.rank_img_gold);
        }
        else if(mmr<=400) {
            rankTier06.setText("PLATINUM");
            rankEdge06.setImageResource(R.drawable.rank_img_platinum);
        }
        else {
            rankTier06.setText("MASTER");
            rankEdge06.setImageResource(R.drawable.rank_img_master);
        }

        rank01.setBackground(new ShapeDrawable(new OvalShape()));
        rank01.setClipToOutline(true);

        rank02.setBackground(new ShapeDrawable(new OvalShape()));
        rank02.setClipToOutline(true);

        rank03.setBackground(new ShapeDrawable(new OvalShape()));
        rank03.setClipToOutline(true);

        rank04.setBackground(new ShapeDrawable(new OvalShape()));
        rank04.setClipToOutline(true);

        rank05.setBackground(new ShapeDrawable(new OvalShape()));
        rank05.setClipToOutline(true);

        rank06.setBackground(new ShapeDrawable(new OvalShape()));
        rank06.setClipToOutline(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //db 생성
    private void createDatabase(String name){
        try {
            db = openOrCreateDatabase(name, MODE_ENABLE_WRITE_AHEAD_LOGGING,null);
            Toast.makeText(this, "db생성 성공", Toast.LENGTH_SHORT).show();
            createdDB = true;
        } catch(Exception ex) {
            Toast.makeText(this, "db 생성 안됨", Toast.LENGTH_SHORT).show();
        }
    }

    //table 생성
    private void createTable() {
        if(createdDB) {
            try {
                db.execSQL("create table player ("
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

    private void getDataFromDB(){
        if(createdDB) {
            String sql = "select * from " + PLAYERTABLE;
            try {
                Cursor cursor = db.rawQuery(sql, null);

                if (cursor != null) {
                    int count = cursor.getCount();

                    for (int i = 0; i < count; i++) {
                        cursor.moveToNext();

                        if(cursor.getString(0).equals(email)){
                            name = cursor.getString(1);
                            mmr = cursor.getInt(8);
                            photoPath = cursor.getString(11);
                            photoFile = cursor.getString(12);

                            break;
                        }
                    }
                }
            }catch(Exception e){
                Toast.makeText(this, "query 실패", Toast.LENGTH_SHORT).show();
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

            //kakaoTalkLinkMessageBuilder.addAppButton("착한 레이싱 다운받기");

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
    }
}