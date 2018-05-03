package usmanali.nephrohub;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HelloWorldSolution on 4/24/2018.
 */

public class food_list_adapter extends BaseExpandableListAdapter {
    ArrayList<String> headings_list;
    HashMap<String,ArrayList<Food>> child_list;

    public food_list_adapter(ArrayList<String> headings_list, HashMap<String, ArrayList<Food>> child_list) {
        this.headings_list = headings_list;
        this.child_list = child_list;
    }

    @Override
    public int getGroupCount() {
        return headings_list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child_list.get(headings_list.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headings_list.get(groupPosition);
    }

    @Override
    public Food getChild(int groupPosition, int childPosition) {

        return child_list.get(headings_list.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandible_food_list_header_view, parent, false);
        }
        TextView heading=(TextView) convertView.findViewById(R.id.heading);
        heading.setText(headings_list.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_plan_list_layout,parent,false);
        }
        TextView food_name=convertView.findViewById(R.id.food_name);
        ImageView food_image=convertView.findViewById(R.id.food_img);
         food_name.setText(getChild(groupPosition,childPosition).food_name);
        food_image.setImageResource(getChild(groupPosition,childPosition).image);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
