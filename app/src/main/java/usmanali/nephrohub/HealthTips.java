package usmanali.nephrohub;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class HealthTips extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView tips_list=(ListView) findViewById(R.id.tips_list);
        tips_list.setAdapter(new Health_Tips_Adapter());
        tips_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(HealthTips.this,TipsDetail.class);
                switch(i){
                    case 0:
                        intent.putExtra("file_name","content1");
                        startActivity(intent);
                        break;
                    case 1:
                        intent.putExtra("file_name","content2");
                        startActivity(intent);
                        break;
                    case 2:
                        intent.putExtra("file_name","content3");
                        startActivity(intent);
                        break;
                    case 3:
                        intent.putExtra("file_name","content4");
                        startActivity(intent);
                        break;
                    case 4:
                        intent.putExtra("file_name","content5");
                        startActivity(intent);
                        break;
                }

            }
        });
    }

}
