package usmanali.nephrohub;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class add_reports extends AppCompatActivity {
Button scan_by_ocr_btn;
Button add_picture_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reports);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        scan_by_ocr_btn=(Button) findViewById(R.id.btn1);
        add_picture_btn=(Button) findViewById(R.id.btn2);
        scan_by_ocr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(add_reports.this,Scan_Reports.class);
                startActivity(i);
            }
        });
        add_picture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(add_reports.this,add_report_picture.class);
                startActivity(i);
            }
        });
    }

}
