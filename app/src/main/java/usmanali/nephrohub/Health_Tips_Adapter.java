package usmanali.nephrohub;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by SAJIDCOMPUTERS on 12/14/2017.
 */

public class Health_Tips_Adapter extends BaseAdapter {

int[] images={R.drawable.ico1,R.drawable.ico2,R.drawable.ico3,R.drawable.ico4,R.drawable.ico5};
String[] titles={"Keep Your Kidneys Healthy","How to Keep Kidneys Healthy","To Maintain Healthy Kidneys","Protect Your Kidneys From RA","Tips to Prevent Kidney Disease"};
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return images[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
        view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.disorder_list_layout,viewGroup,false);
        TextView tip_name=(TextView) view.findViewById(R.id.tips_name);
        ImageView imageView=(ImageView) view.findViewById(R.id.imageView_round);
        imageView.setImageResource(images[i]);
        tip_name.setText(titles[i]);
        return view;
    }

}
