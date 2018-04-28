package usmanali.nephrohub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class Home extends AppCompatActivity implements View.OnClickListener {
CardView scan_reports,health_tips,contact_us,medical_records,diet_plan;
NavigationView nv;
DrawerLayout drawerlayout;
ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Paper.init(Home.this);
        scan_reports=(CardView) findViewById(R.id.scan_reports);
        health_tips=(CardView) findViewById(R.id.health_tips);
        contact_us=(CardView) findViewById(R.id.contact_us);
        medical_records=(CardView) findViewById(R.id.medical_records);
        diet_plan=(CardView) findViewById(R.id.diet_plan);
        drawerlayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        nv=(NavigationView) findViewById(R.id.nav_view);
        scan_reports.setOnClickListener(this);
        health_tips.setOnClickListener(this);
        contact_us.setOnClickListener(this);
        medical_records.setOnClickListener(this);
        diet_plan.setOnClickListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(Home.this, drawerlayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerlayout.setDrawerListener(actionBarDrawerToggle);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.share){
                    String message = "http://google.com";
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(share, "Where you want to share it"));


                }else if(item.getItemId()==R.id.Log_Out){
                  Paper.book().destroy();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent=new Intent(Home.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.scan_reports){
            Intent i=new Intent(Home.this,add_reports.class);
            startActivity(i);
        }else if(view.getId()==R.id.health_tips){
            Intent i=new Intent(Home.this,HealthTips.class);
            startActivity(i);
        }else if(view.getId()==R.id.medical_records){
            Intent i=new Intent(Home.this,medical_records.class);
            startActivity(i);
        }else if(view.getId()==R.id.contact_us){
            Intent i=new Intent(Home.this,ContactUs.class);
            startActivity(i);
        }else if(view.getId()==R.id.diet_plan){
            Intent i=new Intent(Home.this,Diet_Plan.class);
            startActivity(i);
        }
    }
}
