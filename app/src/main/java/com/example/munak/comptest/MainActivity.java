package com.example.munak.comptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public  void shareKakao(View v) {
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
}
