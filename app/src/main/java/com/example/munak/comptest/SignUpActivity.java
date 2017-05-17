package com.example.munak.comptest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Munak on 2017. 3. 29..
 */

public class SignUpActivity extends AppCompatActivity {

    final private String DBNAME = "playerinfo.db";
    final private String PLAYERTABLE = "player";
    SQLiteDatabase db;
    boolean createdDB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Title Bar Back Button Visible
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button sign_up_button = (Button) findViewById(R.id.sign_up_button);
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText1 = (EditText) findViewById(R.id.sign_up_id);
                EditText editText2 = (EditText) findViewById(R.id.sign_up_password);
                EditText editText3 = (EditText) findViewById(R.id.sign_up_name);

                String email = editText1.getText().toString();
                String password = editText2.getText().toString();
                String name = editText3.getText().toString();

                Player player = new Player(name, email, password);

                createDatabase(DBNAME);

                if(insertData(PLAYERTABLE,player)) {
                    queryData();
                    Intent signUpToLoginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                    signUpToLoginIntent.putExtra("keyEmail",email);
                    startActivity(signUpToLoginIntent);
                }
            }
        });

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

    //login 체크
    private boolean loginCheck(Player p){
        createDatabase(DBNAME);
        if(createdDB) {
            String sql = "select * from " + PLAYERTABLE;
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor != null) {
                int count = cursor.getCount();
                String email="";
                String password="";
                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();

                    email = cursor.getString(0);
                    password = cursor.getString(2);

                    if(p.getEmail().equals(email))
                        break;
                }

                if(p.getEmail().equals(email)){
                    if(p.getPassword().equals(password)){
                        return true;
                    }else{
                        Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                }else{
                    Toast.makeText(this, "일치하는 아이디가 없습니다", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }else
                return false;
        }else
            return false;
    }

    //db 생성
    private void createDatabase(String name){
        try {
            db = openOrCreateDatabase(name, MODE_ENABLE_WRITE_AHEAD_LOGGING,null);
            Toast.makeText(this, "db생성 성공", Toast.LENGTH_SHORT).show();
            createdDB = true;
            try {
                if(createdDB) {
                    createTable(PLAYERTABLE);
                }
            } catch(Exception e){}
        } catch(Exception ex) {
            Toast.makeText(this, "db 생성 안됨", Toast.LENGTH_SHORT).show();
        }
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
                        + "winCount integer)"
                );
            }catch(Exception e){}
        }
    }

    //table에 data 넣기
    private boolean insertData(String name,Player p){
        if(createdDB) {
            try {
                String sql ="insert into " + name
                        + "(email, name, password, totalScore, violationAccel, violationVelocity, violationKal, useSleepinessCenter, mmr, conpetitionCount, winCount ) values("
                        + "'" + p.getEmail() + "',"
                        + "'" + p.getName() + "',"
                        + "'" + p.getPassword() + "',"
                        + "'" + p.getTotalScore() + "',"
                        + "'" + p.getViolationAccel() + "',"
                        + "'" + p.getViolationVelocity() + "',"
                        + "'" + p.getViolationKal() + "',"
                        + "'" + p.getUseSleepinessCenter() + "',"
                        + "'" + 0 + "',"
                        + "'" + 0 + "',"
                        + "'" + 0 + "')";
                db.execSQL(sql);
                return true;
            } catch(Exception e) {
                Toast.makeText(this, "이미 존재하는 아이디입니다", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return false;
        }
    }

    //table 제거
    private void removeTable(String tableName){
        if(createdDB){
            String sql = "drop table " + tableName;
            try {
                db.execSQL(sql);
                Toast.makeText(this, "테이블 제거", Toast.LENGTH_SHORT).show();
            }catch(Exception e){Toast.makeText(this, "테이블 제거 실패", Toast.LENGTH_SHORT).show();}
        }
    }

    //data 조회하기
    private void queryData(){
        if(createdDB) {
            String sql = "select * from " + PLAYERTABLE;
            try {
                Cursor cursor = db.rawQuery(sql, null);

                if (cursor != null) {
                    int count = cursor.getCount();

                    for (int i = 0; i < count; i++) {
                        cursor.moveToNext();

                        String data = cursor.getString(0) + "/"
                                + cursor.getString(1) + "/"
                                + cursor.getString(2) + "/"
                                + cursor.getString(3) + "/"
                                + cursor.getString(4) + "/"
                                + cursor.getString(5) + "/"
                                + cursor.getString(6) + "/"
                                + cursor.getString(7) + "/"
                                + cursor.getInt(8)  + "/"
                                + cursor.getInt(9) + "/"
                                + cursor.getInt(10);
                        Toast.makeText(this, i + "번째 : " + data, Toast.LENGTH_SHORT).show();
                    }
                }
            }catch(Exception e){
                Toast.makeText(this, "query 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //table에서 data제거
    private void removeData(String email){
        if(createdDB) {
            String sql = "delete from " + PLAYERTABLE + " where email = " + email + ";";
            try {
                db.execSQL(sql);
            }catch(Exception e){}

        }
    }

    //data 변경하기
    private void updateData(Player p){
        if(createdDB){
            String sql = "UPDATE " + PLAYERTABLE
                    + " SET totalScore = totalScore + '" + 100
                    + "', violationAccel = violationAccel + '" + p.getViolationAccel()
                    + "', violationVelocity = violationVelocity + '" + p.getViolationVelocity()
                    + "', violationKal = violationKal +'" + p.getViolationKal()
                    + "', useSleepinessCenter = useSleepinessCenter +'" + p.getUseSleepinessCenter()
                    + "', mmr = mmr +'" + 10
                    + "', conpetitionCount = conpetitionCount + '" + 10
                    + "', winCount = winCount + '" + 10 + "'"
                    + " WHERE email = '"+p.getEmail() +"';";
            try {
                db.execSQL(sql);
            }catch(Exception e){
                Toast.makeText(this, "update 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
