package com.example.munak.comptest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Munak on 2017. 3. 29..
 */

public class LoginActivity extends AppCompatActivity {

    final private String DBNAME = "playerinfo.db";
    final private String PLAYERTABLE = "player";
    SQLiteDatabase db;
    boolean createdDB = false;

    EditText editTextId, editTextPassword;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent signUpToLoginIntent = getIntent();
        email = signUpToLoginIntent.getStringExtra("keyEmail");
        if(email != null) {
            EditText editTextId = (EditText) findViewById(R.id.login_id);
            editTextId.setText(email);
        }

        Button sign_in_button = (Button) findViewById(R.id.login_button_sign_in);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editTextId = (EditText) findViewById(R.id.login_id);
                editTextPassword = (EditText) findViewById(R.id.login_password);

                String id = editTextId.getText().toString();
                String password = editTextPassword.getText().toString();

                Player player = new Player(id, password);

                if(loginCheck(player)){
                    Intent LoginToMainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginToMainIntent.putExtra("keyEmail",id);
                    startActivity(LoginToMainIntent);
                }
            }
        });

        Button sign_up_button = (Button) findViewById(R.id.login_button_sign_up);
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent2);
            }
        });
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
        try{

            db = openOrCreateDatabase(name, MODE_ENABLE_WRITE_AHEAD_LOGGING,null);
            createdDB = true;
            try{
                if(createdDB) {
                    createTable(PLAYERTABLE);
                }
            }catch(Exception e){}
        }catch(Exception ex){
            Toast.makeText(this, "db 생성 안됨", Toast.LENGTH_SHORT).show();
        }
    }

    //table 생성
    private void createTable(String name) {

        if(createdDB) {
            db.execSQL("create table " + name + "("
                    + "email text primary key,"
                    + "name text,"
                    + "password text,"
                    + "totalScore integer,"
                    + "violationAccel integer,"
                    + "violationVelocity integer,"
                    + "violationKal integer,"
                    + "useSleepinessCenter integer);"
            );
        }

    }

    //table에 data 넣기
    private void insertData(String name,Player p){
        if(createdDB) {
            try {
                String sql ="insert into " + name
                        + "(email, name, password, totalScore, violationAccel, violationVelocity, violationKal, useSleepinessCenter) values("
                        + "'" + p.getEmail() + "',"
                        + "'" + p.getName() + "',"
                        + "'" + p.getPassword() + "',"
                        + "'" + p.getTotalScore() + "',"
                        + "'" + p.getViolationAccel() + "',"
                        + "'" + p.getViolationVelocity() + "',"
                        + "'" + p.getViolationKal() + "',"
                        + "'" + p.getUseSleepinessCenter() + "');";
                db.execSQL(sql);
            }catch(Exception e){
                Toast.makeText(this, "이미 존재하는 email입니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //table 제거
    private void removeTable(String tableName){
        if(createdDB){
            String sql = "drop table " + tableName;
            try {
                db.execSQL(sql);
            }catch(Exception e){}
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
                                + cursor.getString(7);
                        Toast.makeText(this, i + "번째 : " + data, Toast.LENGTH_SHORT).show();
                    }
                }
            }catch(Exception e){}
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
                    + " SET totalScore = totalScore + '" + p.getTotalScore()
                    + "', violationAccel = violationAccel + '" + p.getViolationAccel()
                    + "', violationVelocity = violationVelocity + '" + p.getViolationVelocity()
                    + "', violationKal = violationKal '" + p.getViolationKal()
                    + "', useSleepinessCenter = useSleepinessCenter '" + p.getUseSleepinessCenter() + "'"
                    + " WHERE email = '"+p.getEmail() +"';";
            try {
                db.execSQL(sql);
            }catch(Exception e){}
        }
    }
}
