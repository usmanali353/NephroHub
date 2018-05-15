package usmanali.nephrohub;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.paperdb.Paper;


public class ContactUs extends AppCompatActivity {
EditText message,subject,name,email;
Button send_email;
    String reg_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Paper.init(ContactUs.this);
        String reg_num= Paper.book().read("Registration_number","Not Found").toString();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        message=(EditText) findViewById(R.id.your_message);
        email=(EditText) findViewById(R.id.your_email);
        subject=(EditText) findViewById(R.id.your_subject);
        name=(EditText) findViewById(R.id.your_name);
        send_email=(Button) findViewById(R.id.post_message);
        if(!reg_num.equals("Not Found")) {
            name.setText(Paper.book().read("Name").toString());
            name.setEnabled(false);
            email.setText(Paper.book().read("Email").toString());
            email.setEnabled(false);
        }
        send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(name.getText().toString())||TextUtils.isEmpty(subject.getText().toString())||TextUtils.isEmpty(message.getText().toString())){
                    Toast.makeText(ContactUs.this,"please provide all required information",Toast.LENGTH_LONG).show();

                }else if(!isEmailValid(email.getText().toString())){
                    Toast.makeText(ContactUs.this,"Email you entered is invalid",Toast.LENGTH_LONG).show();

                }else{
                    new sendmail(ContactUs.this,"kidneycentergujrat@gmail.com",subject.getText().toString(),"from "+name.getText().toString()+" "+"<"+email.getText().toString()+">"+"\n"+"\n"+message.getText().toString()).execute();
                }

            }
        });
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
