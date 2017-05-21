package com.example.munak.comptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Munak on 2017. 4. 9..
 */

public class TestActivity extends AppCompatActivity {

    TextView testText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        testText = (TextView) findViewById(R.id.testText);

        Button updateButton = (Button) findViewById(R.id.updateButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testText.setText("totalScore : " + InGameStatus.getTotalScore()
                        + "\nviolationAccel : " + InGameStatus.getViolationAccel()
                        + "\nviolationVelocity : " + InGameStatus.getViolationVelocity()
                        + "\nviolationKal : " + InGameStatus.getViolationKal()
                        + "\nuseSleepinessCenter : " + InGameStatus.getUseSleepinessCenter()
                        + "\n\nvelocity : " + InGameStatus.getVelocity()
                        + "\ncurrVelocity : " + InGameStatus.getCurrVelocity()
                        + "\nprevVelocity : " + InGameStatus.getPrevVelocity()
                        + "\nacceleration : " + InGameStatus.getAcceleration()
                        + "\nlocationX : " + InGameStatus.getLocationX()
                        + "\nlocationY : " + InGameStatus.getLocationY()
                );
            }
        });






    }
}