package usmanali.nephrohub;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    Button btnSignin,btnRegister;
    MaterialEditText email,password,name,phone,registration_number;
    RelativeLayout rootlayout;
    TextView txt_forgot_password;
    FirebaseAuth auth;//fire authentication object
    FirebaseDatabase db;//firebase db object
    DatabaseReference users;//store reference of node of firebase
    String rn;//storing registration number
    long count;// storing count of paatients currently registered
     User user;//object of user class
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Arkhip_font.ttf").setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        auth= FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance();
        users=db.getReference("Patients");
        Paper.init(MainActivity.this);
        txt_forgot_password=(TextView) findViewById(R.id.txt_forgot_password);
        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {show_forgot_password_dialog();
            }
        });
        btnSignin=(Button)findViewById(R.id.btnSignin);
        btnRegister=(Button)findViewById(R.id.btnRegister);
        rootlayout=(RelativeLayout) findViewById(R.id.rootlayout);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_last_registration_number();
            }
        });
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_login_dialog();
            }
        });
    }
private String get_last_registration_number(){
users.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        count = dataSnapshot.getChildrenCount();

        rn=String.valueOf(count);
      show_register_dialog();

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
      Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
    }
});

return rn;
}
    private void show_forgot_password_dialog(){
        AlertDialog.Builder forgot_password_dialog=new AlertDialog.Builder(MainActivity.this);
        forgot_password_dialog .setTitle("Forgot Password");
        forgot_password_dialog .setMessage("Please Enter Your Email");
        LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
        View v=inflater.inflate(R.layout.forgot_password_layout,null);
        forgot_password_dialog.setView(v);
        final MaterialEditText emailtxt=(MaterialEditText) v.findViewById(R.id.emailtxt);

        forgot_password_dialog .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                final android.app.AlertDialog waiting_dialog=new SpotsDialog(MainActivity.this);
                waiting_dialog.show();
                waiting_dialog.setMessage("Please Wait...");
                waiting_dialog.setCancelable(false);
                if(!TextUtils.isEmpty(emailtxt.getText().toString())&&isEmailValid(emailtxt.getText().toString())) {
                     auth.sendPasswordResetEmail(emailtxt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()){
                                 waiting_dialog.dismiss();
                                 Toast.makeText(MainActivity.this,"Password Reset Email has been sent",Toast.LENGTH_LONG).show();
                             }
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             waiting_dialog.dismiss();
                             Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                         }
                     });
                }else{
                    waiting_dialog.dismiss();
                    Snackbar.make(rootlayout,"Please Enter Email",Snackbar.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void show_register_dialog(){
        final AlertDialog.Builder register_dialog=new AlertDialog.Builder(MainActivity.this);
        register_dialog.setTitle("Register");
        register_dialog.setMessage("Use Email to Register");
        final View v=LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_register,null);
        email=(MaterialEditText) v.findViewById(R.id.emailtxt);
        password=(MaterialEditText) v.findViewById(R.id.passwordtxt);
        name=(MaterialEditText) v.findViewById(R.id.nametxt);
        phone=(MaterialEditText) v.findViewById(R.id.phone);
        registration_number=(MaterialEditText) v.findViewById(R.id.regnum);
        registration_number.setEnabled(false);
        count=count+1;
        registration_number.setText("00"+String.valueOf(count)+"-018");
        register_dialog.setView(v);
        register_dialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {

                if(TextUtils.isEmpty(email.getText().toString())){
                    Toast.makeText(MainActivity.this,"Please Enter Email",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(MainActivity.this,"Please Enter Password",Toast.LENGTH_LONG).show();
                }else if (password.getText().toString().length() < 6){
                    Toast.makeText(MainActivity.this,"Password too short",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(phone.getText().toString())){
                    Toast.makeText(MainActivity.this,"Please Enter Phone",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(name.getText().toString())){
                    Toast.makeText(MainActivity.this,"Please Enter Name",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(registration_number.getText().toString())){
                    Toast.makeText(MainActivity.this,"Please Enter Registration Number",Toast.LENGTH_LONG).show();
                }else if(!isEmailValid(email.getText().toString())) {
                    Toast.makeText(MainActivity.this,"Email is not Valid",Toast.LENGTH_LONG).show();
                }else{
                    final android.app.AlertDialog waiting_dialog=new SpotsDialog(MainActivity.this);
                    waiting_dialog.show();
                    waiting_dialog.setMessage("Please Wait...");
                    auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                User user=new User();
                                user.setName(name.getText().toString());
                                user.setPassword(password.getText().toString());
                                user.setPhone(phone.getText().toString());
                                user.setEmail(email.getText().toString());
                                user.setRegistration_number(registration_number.getText().toString());

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this,"Registration Success",Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                        FirebaseUser user=auth.getCurrentUser();
                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    waiting_dialog.dismiss();
                                                    Toast.makeText(MainActivity.this,"Verification Email is sent Verify Your Email to use the app",Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                waiting_dialog.dismiss();
                                               Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waiting_dialog.dismiss();
                            Toast.makeText(MainActivity.this,"Registration failed "+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
                }

        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        }).show();

    }


    private void show_login_dialog(){
        AlertDialog.Builder login_dialog=new AlertDialog.Builder(MainActivity.this);
        login_dialog.setTitle("Sign In");
        login_dialog.setMessage("Use Email to Sign In");
        View v=LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_login,null);
        email=(MaterialEditText) v.findViewById(R.id.emailtxt);
        password=(MaterialEditText) v.findViewById(R.id.passwordtxt);
        String Username= Paper.book().read("Email");
        String Password= Paper.book().read("Password");
        if(Username!=null&&Password!=null){
            if(!TextUtils.isEmpty(Username)&&!TextUtils.isEmpty(Password)){
                email.setText(Username);
                password.setText(Password);
            }
        }
        login_dialog.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(TextUtils.isEmpty(email.getText().toString())){
                    Toast.makeText(MainActivity.this,"Please Enter Email",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(MainActivity.this,"Please Enter Password",Toast.LENGTH_LONG).show();
                }else if (password.getText().toString().length() < 6){
                    Toast.makeText(MainActivity.this,"Password too short",Toast.LENGTH_LONG).show();
                }else if(!isEmailValid(email.getText().toString())) {
                    Toast.makeText(MainActivity.this,"Email is not Valid",Toast.LENGTH_LONG).show();
                }else{
                    final android.app.AlertDialog waiting_dialog=new SpotsDialog(MainActivity.this);
                    waiting_dialog.show();
                    waiting_dialog.setMessage("Please Wait...");
                    waiting_dialog.setCancelable(false);
                    auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser userinfo = auth.getCurrentUser();
                                if (userinfo.isEmailVerified()) {
                                    users.child(userinfo.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            user = dataSnapshot.getValue(User.class);
                                            Paper.book().write("Email",email.getText().toString());
                                            Paper.book().write("Registration_number",user.getRegistration_number());
                                            Paper.book().write("Name",user.getName());
                                            Paper.book().write("Phone",user.getPhone());
                                            Paper.book().write("Password",password.getText().toString());

                                            Toast.makeText(MainActivity.this, "Welcome " + user.getName(), Toast.LENGTH_LONG).show();
                                            waiting_dialog.dismiss();
                                            startActivity(new Intent(MainActivity.this, Home.class));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            waiting_dialog.dismiss();
                                          Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else{
                                    waiting_dialog.dismiss();
                                    Toast.makeText(MainActivity.this,"Your Email is not Verified",Toast.LENGTH_LONG).show();
                                    auth.signOut();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waiting_dialog.dismiss();
                            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setNeutralButton("Use as Guest", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MainActivity.this,Home.class));
            }
        }).setView(v).show();
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
