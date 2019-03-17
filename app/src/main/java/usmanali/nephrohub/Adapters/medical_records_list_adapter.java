package usmanali.nephrohub.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.paperdb.Paper;
import usmanali.nephrohub.R;
import usmanali.nephrohub.Model.Reports;

/**
 * Created by SAJIDCOMPUTERS on 1/29/2018.
 */

public class medical_records_list_adapter extends BaseAdapter {
    ArrayList<Reports> reportslist;
    Context context;
    String img;
    String reg_num;
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
        Paper.init(context);
        final Reports medical_records=reportslist.get(i);
        medical_records_list_viewholder holder;
        reg_num=Paper.book().read("Registration_number","Not Found");
        LayoutInflater inflater=LayoutInflater.from(context);
        if(view==null) {
        view=inflater.inflate(R.layout.medical_records_list_layout, viewGroup, false);
            holder=new medical_records_list_viewholder();
            holder.doctor_name=(TextView) view.findViewById(R.id.doctor_name);
            holder.report_date=(TextView) view.findViewById(R.id.report_date);
            holder.report_title=(TextView) view.findViewById(R.id.report_title);
            holder.report_pic=(ImageView) view.findViewById(R.id.report_pic);
            view.setTag(holder);
        }else{
                holder=(medical_records_list_viewholder) view.getTag();
            }
        holder.report_title.setText(medical_records.getReport_title());
        holder.doctor_name.setText(medical_records.getRef_by());
        holder.report_date.setText(medical_records.getReport_date());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View pv = inflater.inflate(R.layout.report_image_layout, null);
                AlertDialog.Builder report_pic_dialog = new AlertDialog.Builder(context);
                report_pic_dialog.setTitle(medical_records.getReport_title());
                PhotoView report_img = (PhotoView) pv.findViewById(R.id.report_pic);
                if(!reg_num.equals("Not Found")&&isNetworkAvailable()) {
                    Picasso.with(context).load(medical_records.getImage_url()).into(report_img);
                }else{
                    Bitmap b=StringToBitMap(medical_records.getImage_url());
                    report_img.setImageBitmap(b);
                }
                report_pic_dialog.setView(pv);
                report_pic_dialog.show();

            }
        });
        return view;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

}
class medical_records_list_viewholder{
    TextView doctor_name;
    TextView report_date;
    TextView report_title;
    ImageView report_pic;
}