package usmanali.nephrohub;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import usmanali.nephrohub.Model.Prescription;
import usmanali.nephrohub.Model.dbhelper;

public class Add_prescription extends AppCompatActivity {
TextInputLayout medicine_name_textinputlayout,dosage_textinputlayout,doctor_instructions_textinputlayout;
    TextInputEditText medicine_name,dosage,doctor_instructions;
    AppCompatButton add_prescription_btn;
    dbhelper dbh;
    Gson g;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prescription);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Prescription");
        dbh=new dbhelper(Add_prescription.this);
        g=new Gson();
        medicine_name_textinputlayout =(TextInputLayout) findViewById(R.id.medicine_name_textinputlayout);
        dosage_textinputlayout=(TextInputLayout) findViewById(R.id.dosage_textinputlayout);
        doctor_instructions_textinputlayout=(TextInputLayout) findViewById(R.id.doctor_instructions_textinputlayout);
        medicine_name=(TextInputEditText) findViewById(R.id.medicine_name);
        dosage=(TextInputEditText) findViewById(R.id.dosage);
        doctor_instructions=(TextInputEditText) findViewById(R.id.doctor_instructions);
        add_prescription_btn=(AppCompatButton) findViewById(R.id.btn_add_prescription);
        add_prescription_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(medicine_name.getText().toString().isEmpty()){
                  medicine_name_textinputlayout.setError("Please enter Medicine name");
               }else if(dosage.getText().toString().isEmpty()){
                 dosage_textinputlayout.setError("Please Enter dosage value");
               }else if(doctor_instructions.getText().toString().isEmpty()){
                   doctor_instructions_textinputlayout.setError("Please Enter Doctor instructions");
               }else{
                   Prescription p=new Prescription();
                   p.setMedicine_name(medicine_name.getText().toString());
                   p.setDosage(dosage.getText().toString());
                   p.setDoctor_instructions(doctor_instructions.getText().toString());
                   String prescription_obj=g.toJson(p);
                   Log.e("Prescription",prescription_obj);
                   add_prescription(prescription_obj);
               }
            }
        });
    }
public void add_prescription(String prescription_obj){

   long l= dbh.insert_prescription(prescription_obj);
    if(l==-1){
        Toast.makeText(Add_prescription.this,"Prescription is not inserted",Toast.LENGTH_LONG).show();
    }else{
        Toast.makeText(Add_prescription.this,"Prescription is inserted",Toast.LENGTH_LONG).show();
    }
}
}
