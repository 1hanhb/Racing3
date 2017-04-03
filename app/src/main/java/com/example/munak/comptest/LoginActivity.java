package com.example.munak.comptest;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button sign_in_button = (Button) findViewById(R.id.login_button_sign_in);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                EditText editText1 = (EditText) findViewById(R.id.login_id);

                EditText editText2 = (EditText) findViewById(R.id.login_password);

                String id = editText1.getText().toString();
                String password = editText1.getText().toString();

                Player player = new Player(id, password);
                player.readData();

                Toast.makeText(LoginActivity.this, player.getName().toString(), Toast.LENGTH_SHORT).show();
*/

                Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent1);
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
}
