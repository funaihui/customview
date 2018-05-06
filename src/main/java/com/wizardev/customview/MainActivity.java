package com.wizardev.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.wizardev.customview.view.SlideButton;

public class MainActivity extends AppCompatActivity {

    private SlideButton mSlideButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSlideButton = findViewById(R.id.slideButton);
        mSlideButton.setOnSlideCallback(new SlideButton.OnSlideCallback() {
            @Override
            public void onComplete() {
                Toast.makeText(MainActivity.this,"滑动完成!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
