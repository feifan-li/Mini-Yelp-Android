package com.example.yelp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {
    private ImageView imageView;
    Animation animationImage;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = findViewById(R.id.ImageSplash);
        animationImage = AnimationUtils.loadAnimation(this,R.anim.image_animation);

        imageView.setAnimation(animationImage);

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(Splash.this,MainActivity.class));
                finish();
            }
        }.start();
    }
}