package usmanali.nephrohub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import usmanali.nephrohub.Model.Scanned_reports;
import usmanali.nephrohub.Model.dbhelper;

public class Scan_Reports extends AppCompatActivity {
    String DATA_PATH=Environment.getExternalStorageDirectory()+"/nephrohub_ocr/";
    public static final String lang = "eng";
    String TAG = "MainActivity";
    String resulturi;
    ImageView image;
    Bitmap bitmap;
    private TextRecognizer detector;
    DatabaseReference Scanned_reports;
    Button add_report_btn, upload_pic_btn;
    EditText doctor_name, report_title;
    ArrayList<String> wordlist, valuelist;
    dbhelper dbh;
    FirebaseDatabase db;
    String user_id;
    Gson g;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_reports);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Scan Reports by OCR");
        Paper.init(Scan_Reports.this);
        dbh=new dbhelper(Scan_Reports.this);
        g=new Gson();
        user_id=Paper.book().read("user_id","Not Found").toString();
        image = (ImageView) findViewById(R.id.report_pic);
        add_report_btn = (Button) findViewById(R.id.add_report_btn);
        upload_pic_btn = (Button) findViewById(R.id.upload_pic_btn);
        doctor_name = (EditText) findViewById(R.id.doctor_name);
        report_title = (EditText) findViewById(R.id.report_title);
        detector = new TextRecognizer.Builder(getApplicationContext()).build();
        wordlist = new ArrayList<>();
        valuelist = new ArrayList<>();
        db = FirebaseDatabase.getInstance();
        Scanned_reports = db.getReference("Scanned Reports");

        initialize_opencv();
        final Boolean is_initialized =Paper.book().read("is_initialized");
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
                PopupMenu popup=new PopupMenu(Scan_Reports.this,v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId()==R.id.scan_by_google_vision){
                            if (doctor_name.getText().toString().isEmpty() || report_title.getText().toString().isEmpty() || bitmap == null) {
                                Toast.makeText(Scan_Reports.this, "Please Provide all Required Information", Toast.LENGTH_LONG).show();
                            } else if (is_initialized) {
                                if(isBlurredImage(resulturi))
                                    Toast.makeText(Scan_Reports.this,"Your Image is Blurry",Toast.LENGTH_LONG).show();
                                    if(user_id.equals("Not Found")){
                                        add_ocr_reports_guest();
                                    }else {
                                        add_ocr_report_to_firebase();
                                    }

                            } else {
                                create_directory_in_External_storage();
                                copy_file_to_device();
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String date = df.format(c.getTime());
                                new background_task_for_tessract(Scan_Reports.this, report_title.getText().toString(), doctor_name.getText().toString(), date).execute(bitmap);
                            }

                        }else if(menuItem.getItemId()==R.id.scan_by_tesseract){
                            if (doctor_name.getText().toString().isEmpty() || report_title.getText().toString().isEmpty() || bitmap == null) {
                                Toast.makeText(Scan_Reports.this, "Please Provide all Required Information", Toast.LENGTH_LONG).show();
                            } else if (is_initialized) {
                                if(isBlurredImage(resulturi))
                                    Toast.makeText(Scan_Reports.this,"Your Image is Blurry",Toast.LENGTH_LONG).show();
                                else{
                                    create_directory_in_External_storage();
                                    copy_file_to_device();
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                    String date = df.format(c.getTime());
                                    new background_task_for_tessract(Scan_Reports.this, report_title.getText().toString(), doctor_name.getText().toString(), date).execute(bitmap);
                                }
                            } else {
                                create_directory_in_External_storage();
                                copy_file_to_device();
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String date = df.format(c.getTime());
                                new background_task_for_tessract(Scan_Reports.this, report_title.getText().toString(), doctor_name.getText().toString(), date).execute(bitmap);
                            }
                        }
                        return true;
                    }
                });
                popup.show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
             resulturi = result.getUri().getPath();
            bitmap = BitmapFactory.decodeFile(resulturi);
            image.setImageBitmap(bitmap);
        }
    }
    public void create_directory_in_External_storage()  {
        String folder_main = "nephrohub_ocr";

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            if( f.mkdirs()){
                Toast.makeText(Scan_Reports.this,"Directory Created Sucessfully",Toast.LENGTH_LONG).show();
                Log.e("make_dir","Directory Created Sucessfully");
            }
        }else{
          //  Toast.makeText(Scan_Reports.this,"Directory already Exist",Toast.LENGTH_LONG).show();
            Log.e("make_dir","Directory already Exist");
        }
        File f1 = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, "tessdata");
        if (!f1.exists()) {
            if( f1.mkdirs()){
                Log.e("make_sub_directory","Sub Directory created sucessfully");
            }
        }else{
            Log.e("make_sub_directory","Sub Directory already exists");
        }
    }
    public void copy_file_to_device() {
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }

        }

    }
    private Boolean initialize_opencv(){
        Boolean is_initialized= OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, getApplicationContext(), new BaseLoaderCallback(Scan_Reports.this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        Log.e("opcv", "OpenCV loaded successfully");
                    }
                    break;
                    case LoaderCallbackInterface.INIT_FAILED:
                    {
                        Log.e("opcv", "Initialization Failed");
                    }
                    break;
                }

            }
        });
        Paper.book().write("is_initialized",is_initialized);
        Toast.makeText(Scan_Reports.this,String.valueOf(is_initialized),Toast.LENGTH_LONG).show();
        return is_initialized;
    }
    private Boolean isBlurredImage(String resultUri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeFile(resultUri);
        int l = CvType.CV_8UC1; //8-bit grey scale image
        Mat matImage = new Mat();
        Utils.bitmapToMat(image, matImage);
        Mat matImageGrey = new Mat();
        Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY);

        Bitmap destImage;
        destImage = Bitmap.createBitmap(image);
        Mat dst2 = new Mat();
        Utils.bitmapToMat(destImage, dst2);
        Mat laplacianImage = new Mat();
        dst2.convertTo(laplacianImage, l);
        Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U);
        Mat laplacianImage8bit = new Mat();
        laplacianImage.convertTo(laplacianImage8bit, l);

        Bitmap bmp = Bitmap.createBitmap(laplacianImage8bit.cols(), laplacianImage8bit.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(laplacianImage8bit, bmp);
        int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        int maxLap = -16777216; // 16m
        for (int pixel : pixels) {
            if (pixel > maxLap)
                maxLap = pixel;
        }

        int soglia = -6118750;
        if (maxLap <= soglia) {
            return true;

        } else
            return false;
    }
    public Scanned_reports process() {
                 Scanned_reports reports = new Scanned_reports();
                try {
                         if (detector.isOperational() && bitmap != null) {
                                 Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                                 SparseArray<TextBlock> textBlocks = detector.detect(frame);
                                 String blocks = "";
                                 String lines = "";
                                 String words = "";
                                 String values = "";
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
                                                                 Toast.makeText(Scan_Reports.this, "Unable to Scan Image", Toast.LENGTH_LONG).show();
                                                             } else if (words.matches(".*\\d+.*")) {
                                                                 if (!words.isEmpty())
                                                                        valuelist.add(words);
                                                                 reports.setResults(valuelist);


                                                             } else {
                                                                 String scl = words.replaceAll("[^a-zA-Z0-9]","");
                                                                 if (!scl.isEmpty())
                                                                         wordlist.add(scl);
                                                                 reports.setTests(wordlist);
                                                             }


                                                     }


                                             }
                                    }
                               Log.e("size_of_tests",String.valueOf(wordlist.size()));
                                Log.e("size_of_results",String.valueOf(valuelist.size()));


                            } else {
                                 Toast.makeText(Scan_Reports.this, "Unable to find Text in Image", Toast.LENGTH_LONG).show();
                            }
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                         String date = df.format(c.getTime());
                        reports.setDate(date);
                         reports.setRef_by(doctor_name.getText().toString());
                        reports.setReport_title(report_title.getText().toString());
                     } catch (Exception e) {
                         Log.e("ocr_exception", e.getMessage());
                     }
                return reports;
             }
    public void add_ocr_report_to_firebase() {
                 if (doctor_name.getText().toString().isEmpty() || report_title.getText().toString().isEmpty() || bitmap == null) {
                         Toast.makeText(Scan_Reports.this, "Please Provide all Required Information", Toast.LENGTH_LONG).show();
                     } else {
                         Scanned_reports sr = process();
                         if (!wordlist.isEmpty() || !valuelist.isEmpty()) {
                                 if (wordlist.size() == valuelist.size()) {
                                         final android.app.AlertDialog waiting_dialog = new SpotsDialog(Scan_Reports.this);
                                         waiting_dialog.show();
                                         waiting_dialog.setCancelable(false);
                                         waiting_dialog.setMessage("Please Wait...");
                                         Scanned_reports.child(Paper.book().read("user_id").toString()).push().setValue(sr).addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void aVoid) {
                                                         waiting_dialog.dismiss();
                                                         Toast.makeText(Scan_Reports.this, "Report Added Sucessfully", Toast.LENGTH_LONG).show();
                                                         wordlist.clear();
                                                         valuelist.clear();
                                                     }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                                                         waiting_dialog.dismiss();
                                                         Toast.makeText(Scan_Reports.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                         wordlist.clear();
                                                         valuelist.clear();
                                                         Log.e("scan_error", e.getMessage());
                                                     }
                     });
                                     } else {
                                         Toast.makeText(Scan_Reports.this, "Image you scanned is not in proper Report format", Toast.LENGTH_LONG).show();
                                         wordlist.clear();
                                         valuelist.clear();
                                     }
                             } else {
                                 Toast.makeText(Scan_Reports.this, "Image you scanned is not in proper Report format", Toast.LENGTH_LONG).show();
                                 wordlist.clear();
                                 valuelist.clear();
                             }
                     }
             }
    public void add_ocr_reports_guest() {
                 if (doctor_name.getText().toString().isEmpty()|| report_title.getText().toString().isEmpty() || bitmap == null) {
                         Toast.makeText(Scan_Reports.this, "Please Provide all Required Information", Toast.LENGTH_LONG).show();
                     } else {
                         Scanned_reports sr = process();
                         if (!wordlist.isEmpty() || !valuelist.isEmpty()) {
                                 if (wordlist.size() == valuelist.size()) {
                                         String scanned_report_obj = g.toJson(sr);
                                         long l = dbh.insert_scanned_reports(scanned_report_obj);
                                         if (l == -1) {
                                                 Toast.makeText(Scan_Reports.this, "Report was not added Properly", Toast.LENGTH_LONG).show();
                                                 wordlist.clear();
                                                 valuelist.clear();
                                             } else {
                                                 Toast.makeText(Scan_Reports.this, "Report was added", Toast.LENGTH_LONG).show();
                                                 wordlist.clear();
                                                 valuelist.clear();
                                             }
                                     }else{
                                         Toast.makeText(Scan_Reports.this, "Some Tests may be missing results vice versa", Toast.LENGTH_LONG).show();
                                         wordlist.clear();
                                         valuelist.clear();
                                     }
                             }else{
                                 Toast.makeText(Scan_Reports.this, "Either Tests or Results of Tests are not in Picture", Toast.LENGTH_LONG).show();
                                 wordlist.clear();
                                 valuelist.clear();
                             }
                     }
             }


}

