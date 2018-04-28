package usmanali.nephrohub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Scan_Reports extends AppCompatActivity {
ArrayList<String> wordlist,valuelist;
    ImageView image;
    Bitmap bitmap;
    Button add_report_btn,upload_pic_btn;
    ProgressDialog pd;
    FirebaseDatabase db;
    EditText doctor_name,report_title;
    DatabaseReference Scanned_reports;
    private TextRecognizer detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_reports);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Scan Reports by OCR");
        Paper.init(Scan_Reports.this);
        detector = new TextRecognizer.Builder(getApplicationContext()).build();
        wordlist=new ArrayList<>();
        valuelist=new ArrayList<>();
        db=FirebaseDatabase.getInstance();
        Scanned_reports=db.getReference("Scanned Reports");
        image=(ImageView)findViewById(R.id.report_pic);
        add_report_btn=(Button) findViewById(R.id.add_report_btn);
        upload_pic_btn=(Button) findViewById(R.id.upload_pic_btn);
        doctor_name=(EditText) findViewById(R.id.doctor_name);
        report_title=(EditText) findViewById(R.id.report_title);

        upload_pic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Scan_Reports.this);
            }
        });
        add_report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_ocr_report_to_firebase();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            String resultUri = result.getUri().getPath();
            bitmap = BitmapFactory.decodeFile(resultUri);
            image.setImageBitmap(bitmap);
        }
    }
    public Scanned_reports process() {
        usmanali.nephrohub.Scanned_reports reports=new Scanned_reports();
        try {
            if (detector.isOperational()&&bitmap!=null) {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> textBlocks = detector.detect(frame);
                String blocks = "";
                String lines = "";
                String words = "";
                String values="";
                for (int index = 0; index < textBlocks.size(); index++) {
                    //extract scanned text blocks here
                    TextBlock tBlock = textBlocks.valueAt(index);
                    blocks = blocks + tBlock.getValue() + "\n" + "\n";
                    for (Text line : tBlock.getComponents()) {
                        //extract scanned text lines here
                        lines = lines + line.getValue() + "\n";
                        for (Text element : line.getComponents()) {
                            //extract scanned text words here
                            words = element.getValue();
                            if (textBlocks.size() == 0) {
                               Toast.makeText(Scan_Reports.this,"Unable to Scan Image",Toast.LENGTH_LONG).show();
                            } else if (words.matches(".*\\d+.*")) {
                              if(words.length()>0)
                             valuelist.add(words);
                                reports.setResults(valuelist);

                            } else {
                                String scl=words.replaceAll("[^a-zA-Z0-9]","");
                                if(scl.length()>0)
                                wordlist.add(scl);
                                reports.setTests(wordlist);
                            }

                        }

                    }
                }


            } else {
               Toast.makeText(Scan_Reports.this,"Unable to find Text in Image",Toast.LENGTH_LONG).show();
            }
            Calendar c=Calendar.getInstance();
            SimpleDateFormat df=new SimpleDateFormat("dd-MMM-yyyy");
            String date=df.format(c.getTime());
            reports.setDate(date);
            reports.setRef_by(doctor_name.getText().toString());
            reports.setReport_title(report_title.getText().toString());
        } catch (Exception e) {
            Log.e("ocr_exception",e.getMessage());
        }
        return reports;
    }
    public void add_ocr_report_to_firebase(){
        if(doctor_name.getText()==null||report_title.getText().toString()==null||bitmap==null) {
            Toast.makeText(Scan_Reports.this,"Please Provide all Required Information",Toast.LENGTH_LONG).show();
        }else {
            usmanali.nephrohub.Scanned_reports sr = process();
            if(!wordlist.isEmpty()||!valuelist.isEmpty()) {
                if (wordlist.size() == valuelist.size()) {
                    final android.app.AlertDialog waiting_dialog=new SpotsDialog(Scan_Reports.this);
                    waiting_dialog.show();
                    waiting_dialog.setCancelable(false);
                    waiting_dialog.setMessage("Please Wait...");
                    Scanned_reports.child(Paper.book().read("Registration_number").toString()).push().setValue(sr).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            waiting_dialog.dismiss();
                            Toast.makeText(Scan_Reports.this, "Report Added Sucessfully", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waiting_dialog.dismiss();
                            Toast.makeText(Scan_Reports.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("scan_error", e.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(Scan_Reports.this, "Image you scanned is not in proper Report format", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(Scan_Reports.this, "Image you scanned is not in proper Report format", Toast.LENGTH_LONG).show();
            }
        }
        }
}
