package usmanali.nephrohub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import io.paperdb.Paper;

public class Splash_Screen extends AppCompatActivity {
 ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        image = (ImageView) findViewById(R.id.image);
        Paper.init(Splash_Screen.this);
        final String regnum= Paper.book().read("user_id","Not Found").toString();
        Animation splash_screen_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_screen_animation);
        image.setAnimation(splash_screen_animation);
        splash_screen_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!regnum.equals("Not Found")) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), Home.class));
                }else{
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    }

