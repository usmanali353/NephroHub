package usmanali.nephrohub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Skew;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import usmanali.nephrohub.Model.Scanned_reports;
import usmanali.nephrohub.Model.dbhelper;

/**
 * Created by HelloWorldSolution on 6/25/2018.
 */

public class background_task_for_tessract extends AsyncTask<Bitmap,Void,String> {
    String DATA_PATH = Environment.getExternalStorageDirectory() + "/nephrohub_ocr/";
    public static final String lang = "eng";
    ArrayList<Rect> rectArrayList;
    Bitmap bit = null;
    ArrayList<String> testlist, valueslist;
    String report_title, ref_by, date, reg_num;
    FirebaseDatabase db;
    DatabaseReference Scanned_reports;
    dbhelper dbh;
    Gson g = new Gson();

    public background_task_for_tessract(Context context, String report_title, String ref_by, String date) {
        this.context = context;
        testlist = new ArrayList<>();
        valueslist = new ArrayList<>();
        this.report_title = report_title;
        this.ref_by = ref_by;
        this.date = date;
        Paper.init(context);
        dbh = new dbhelper(context);
        reg_num = Paper.book().read("user_id", "Not Found");
        db = FirebaseDatabase.getInstance();
        Scanned_reports = db.getReference("Scanned Reports");
    }

    Context context;

    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        bit = bitmaps[0];
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(bit);
        Log.e("skew_angle",String.valueOf(Skew.findSkew(baseApi.getThresholdedImage())));
        rectArrayList = baseApi.getWords().getBoxRects();
        String recognized_text = baseApi.getUTF8Text();
        baseApi.end();
        return recognized_text;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s.length() > 0 && bit != null && rectArrayList.size() > 0) {
            String lines[] = s.split("\\r?\\n");
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            Log.e("lines", String.valueOf(lines.length));
            if (lines.length > 0) {
                for (int i = 0; i < lines.length; i++) {
                    if (lines[i].matches(".*\\d+.*")) {
                        String number = lines[i].replaceAll("[^0-9?!\\.]", "");
                        valueslist.add(number);
                    }
                    String words = lines[i].replaceAll("\\P{L}", "");
                    testlist.add(words);
                }
                Log.e("words", testlist.get(0));
                Log.e("number", String.valueOf(valueslist.size()));
            }
            //  String number = lines[1].replaceAll("[^0-9?!\\.]","");
            // Log.e("number",number);
            AlertDialog.Builder imagedilog = new AlertDialog.Builder(context);
            imagedilog.setTitle("Preview");
            //custom_imageview imageview = new custom_imageview(context, bit, rectArrayList);
            View v = LayoutInflater.from(context).inflate(R.layout.preview_layout, null);
            ImageView img = (ImageView) v.findViewById(R.id.img);
            Bitmap bitm = Bitmap.createBitmap(bit.getWidth(), bit.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas();
            canvas.setBitmap(bitm);
            canvas.drawBitmap(bit, 0, 0, null);
            for (int i = 0; i < rectArrayList.size(); i++) {
                Paint p = new Paint();
                p.setAlpha(R.color.colorPrimary);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(context.getResources().getColor(R.color.colorPrimary));
                p.setStrokeWidth(3);
                // p.setStrokeWidth();
                canvas.drawRect(rectArrayList.get(i).left, rectArrayList.get(i).top, rectArrayList.get(i).right, rectArrayList.get(i).bottom, p);
            }
            // imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            img.setImageBitmap(bitm);
            imagedilog.setView(v);
            imagedilog.setCancelable(false);
            imagedilog.setPositiveButton("add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    if (!reg_num.equals("Not Found")) {
                        add_reports_to_firebase();
                    } else if (reg_num.equals("Not Found")) {
                        add_reports_guest();
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            imagedilog.show();
        } else {
            Toast.makeText(context, "bitmap not found", Toast.LENGTH_LONG).show();
        }
    }

    private void add_reports_to_firebase() {
        usmanali.nephrohub.Model.Scanned_reports sr = new Scanned_reports();
        sr.setDate(date);
        sr.setRef_by(ref_by);
        sr.setReport_title(report_title);
        sr.setTests(testlist);
        sr.setResults(valueslist);
        if (!testlist.isEmpty() && !valueslist.isEmpty()) {
            if (testlist.size() == valueslist.size()) {
                final android.app.AlertDialog waiting_dialog = new SpotsDialog(context);
                waiting_dialog.show();
                waiting_dialog.setCancelable(false);
                waiting_dialog.setMessage("Please Wait...");
                Scanned_reports.child(Paper.book().read("user_id").toString()).push().setValue(sr).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        waiting_dialog.dismiss();
                        Toast.makeText(context, "Report Added Sucessfully", Toast.LENGTH_LONG).show();
                        testlist.clear();
                        valueslist.clear();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waiting_dialog.dismiss();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        testlist.clear();
                        valueslist.clear();
                        Log.e("scan_error", e.getMessage());
                    }
                });

            } else {

                Toast.makeText(context, "Not a Proper Report", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Not a Proper Report", Toast.LENGTH_LONG).show();
        }
    }

    private void add_reports_guest() {
        usmanali.nephrohub.Model.Scanned_reports sr = new Scanned_reports();
        sr.setDate(date);
        sr.setRef_by(ref_by);
        sr.setReport_title(report_title);
        sr.setTests(testlist);
        sr.setResults(valueslist);
        if (!testlist.isEmpty() && !valueslist.isEmpty()) {
            if (testlist.size() == valueslist.size()) {
                String scanned_report_obj = g.toJson(sr);
                long l = dbh.insert_scanned_reports(scanned_report_obj);
                if (l == -1) {
                    Toast.makeText(context, "Report was not added Properly", Toast.LENGTH_LONG).show();
                    testlist.clear();
                    valueslist.clear();
                } else {
                    Toast.makeText(context, "Report was added", Toast.LENGTH_LONG).show();
                    testlist.clear();
                    valueslist.clear();
                }

            } else {
                Toast.makeText(context, "Not a Proper Report", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Not a Proper Report", Toast.LENGTH_LONG).show();
        }
    }

}
