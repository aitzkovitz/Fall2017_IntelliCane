package project2017.intellic;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.InterruptedIOException;

public class SplashScreenActivity extends AppCompatActivity implements ViewSwitcher.ViewFactory {

    private ImageSwitcher imageSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen2);
        startAnimation();

    }

    public void startAnimation(){
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        in.setDuration(3000);
        in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // do whatever loading needs to be done
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // not needed
            }
        });
        imageSwitcher = (ImageSwitcher)findViewById(R.id.mySwitcher);

        imageSwitcher.setInAnimation(in);
        imageSwitcher.setFactory(this);
        imageSwitcher.setImageResource(R.drawable.intellicane);


    }

    @Override
    public View makeView(){
        ImageView myView = new ImageView(getApplicationContext());
        myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return myView;
    }

    public void finish(){
        super.finish();
    }
}
