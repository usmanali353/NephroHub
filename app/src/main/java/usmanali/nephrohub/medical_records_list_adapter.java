package usmanali.nephrohub;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SAJIDCOMPUTERS on 1/29/2018.
 */

public class medical_records_list_adapter extends BaseAdapter {
    ArrayList<Reports> reportslist;
    Context context;
public medical_records_list_adapter(Context context,ArrayList<Reports> reportsArrayList){
this.context=context;
    reportslist=reportsArrayList;
}
    @Override
    public int getCount() {
        return reportslist.size();
    }

    @Override
    public Object getItem(int i) {
        return reportslist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final Reports medical_records=reportslist.get(i);
        LayoutInflater inflater=LayoutInflater.from(context);
        if(view==null) {
        view=inflater.inflate(R.layout.medical_records_list_layout, viewGroup, false);
        }
        TextView doctor_name=(TextView) view.findViewById(R.id.doctor_name);
        TextView report_date=(TextView) view.findViewById(R.id.report_date);
        TextView report_title=(TextView) view.findViewById(R.id.report_title);
        ImageView report_pic=(ImageView) view.findViewById(R.id.report_pic);
        report_title.setText(medical_records.getReport_title());
        doctor_name.setText(medical_records.getRef_by());
        report_date.setText(medical_records.getReport_date());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater=LayoutInflater.from(context);
                View pv = inflater.inflate(R.layout.report_image_layout,null);
                AlertDialog.Builder report_pic_dialog =new AlertDialog.Builder(context);
                       report_pic_dialog .setTitle(medical_records.getReport_title());
                       PhotoView report_img=(PhotoView) pv.findViewById(R.id.report_pic);
                Picasso.with(context).load(medical_records.getImage_url()).into(report_img);
                        report_pic_dialog .setView(pv);
                        report_pic_dialog.show();

            }
        });
        return view;
    }
}
