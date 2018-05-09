package usmanali.nephrohub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class add_report_picture extends AppCompatActivity {
    Button Upload_Picture_btn, add_report_btn;
    ImageView report_pic;
    TextView report_title, ref_by;
    Bitmap bitmap;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase db;
    DatabaseReference image_reports;
    Uri image_report_uri;
    String str_img;
    dbhelper dbh;
    String reg_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Paper.init(add_report_picture.this);
        dbh=new dbhelper(add_report_picture.this);
        reg_num=Paper.book().read("Registration_number","Not Found");
        Upload_Picture_btn = (Button) findViewById(R.id.upload_pic_btn);
        report_title = (TextView) findViewById(R.id.report_title);
        ref_by = (TextView) findViewById(R.id.doctor_name);
        add_report_btn = (Button) findViewById(R.id.add_report_btn);
        report_pic = (ImageView) findViewById(R.id.report_pic);
        Upload_Picture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_img();
            }
        });
        add_report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!reg_num.equals("Not Found")) {
                    add_report_to_firebase();
                }else{
                    add_image_reports_guest();
                }
            }
        });
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseDatabase.getInstance();
        image_reports = db.getReference("Image Reports");
    }

    private void choose_img() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            String resultUri = result.getUri().getPath();
            image_report_uri = result.getUri();
            bitmap = BitmapFactory.decodeFile(resultUri);
            report_pic.setImageBitmap(bitmap);
        }
    }

    private void add_report_to_firebase() {
        String report_id = UUID.randomUUID().toString();
        final StorageReference reports_folder = storageReference.child("Reports/" + report_id);
        if (image_report_uri != null && !report_title.getText().toString().isEmpty() && !ref_by.getText().toString().isEmpty()) {
             final android.app.AlertDialog waiting_dialog=new SpotsDialog(add_report_picture.this);
            waiting_dialog.show();
            waiting_dialog.setMessage("Please Wait...");
            waiting_dialog.setCancelable(false);
             reports_folder.putFile(image_report_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     reports_folder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                             Reports r = new Reports();
                             Calendar c = Calendar.getInstance();
                             SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                             String date = df.format(c.getTime());
                             r.setReport_date(date);
                             r.setReport_title(report_title.getText().toString());
                             r.setRef_by(ref_by.getText().toString());
                             r.setImage_url(uri.toString());
                             image_reports.child(Paper.book().read("Registration_number").toString()).push().setValue(r).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     waiting_dialog.dismiss();
                                     Toast.makeText(add_report_picture.this, "Report Added Sucessfully",Toast.LENGTH_LONG).show();
                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     waiting_dialog.dismiss();
                                     Toast.makeText(add_report_picture.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                 }
                             });
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             waiting_dialog.dismiss();
                             Toast.makeText(add_report_picture.this,e.getMessage(),Toast.LENGTH_LONG).show();
                         }
                     });

                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     waiting_dialog.dismiss();
                     Toast.makeText(add_report_picture.this,e.getMessage(),Toast.LENGTH_LONG).show();
                 }
             });
        }else{
            Toast.makeText(add_report_picture.this, "Please add Required Information",Toast.LENGTH_LONG).show();
        }
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public void add_image_reports_guest(){
        if (bitmap != null && !report_title.getText().toString().isEmpty() && !ref_by.getText().toString().isEmpty()) {
            Reports r = new Reports();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String date = df.format(c.getTime());
            r.setReport_date(date);
            r.setReport_title(report_title.getText().toString());
            r.setRef_by(ref_by.getText().toString());
            str_img=BitMapToString(bitmap);
            r.setImage_url(str_img);
            Gson g=new Gson();
            String json=g.toJson(r);
            long l=dbh.insert_image_reports(json);
            if(l==-1){
                Toast.makeText(add_report_picture.this,"Report was not Added",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(add_report_picture.this,"Report was Added Successfully",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(add_report_picture.this, "Please add Required Information",Toast.LENGTH_LONG).show();
        }
    }
}


