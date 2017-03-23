package xavi.hellofirebase;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MiniAct 6" ;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference myRef = database.getReference("message");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button sendButton = (Button)findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(this);
        Button writeButton = (Button)findViewById(R.id.buttonWrite);
        writeButton.setOnClickListener(this);
        findViewById(R.id.Register).setOnClickListener(this);
        findViewById(R.id.Login).setOnClickListener(this);
        findViewById(R.id.Logout).setOnClickListener(this);




        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Read from the database
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            String value = dataSnapshot.getValue(String.class);
                            Log.d(TAG, "Value is: " + value);
                            TextView tv = (TextView)findViewById(R.id.textView);
                            tv.setText(value);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };




    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onClick(View v) {
        EditText et1,et2;
        String email, pass;
        switch (v.getId()){
            case R.id.buttonSend:
                EditText et = (EditText)findViewById(R.id.editText);
                String value = et.getText().toString();
                if (value.isEmpty()){
                    //text null, nothing to send
                    Toast.makeText(MainActivity.this,"Empty text",Toast.LENGTH_SHORT).show();
                }else{
                    //send value
                    myRef.setValue(value);
                }
                break;
            case R.id.buttonWrite:
                String defValue = getString(R.string.defaultValue);
                myRef.setValue(defValue);
                break;
            case R.id.Login:
                 et1 = (EditText)findViewById(R.id.email);
                 et2 = (EditText)findViewById(R.id.password);
                 email = et1.getText().toString();
                 pass = et2.getText().toString();
                if (email.isEmpty() ||pass.isEmpty()){
                    Toast.makeText(MainActivity.this,"Email or Password is empty",Toast.LENGTH_LONG).show();
                }else {
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){

                                Toast.makeText(MainActivity.this,"Log in failed",Toast.LENGTH_LONG).show();

                            }else{
                                Toast.makeText(MainActivity.this,"Log in succesful",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }


                break;

            case R.id.Logout:
                mAuth.signOut();
                Toast.makeText(MainActivity.this,"Log out",Toast.LENGTH_LONG).show();

                break;
            case R.id.Register:
                //check email and pass is  not empty
                 et1 = (EditText)findViewById(R.id.email);
                 et2 = (EditText)findViewById(R.id.password);
                 email = et1.getText().toString();
                 pass = et2.getText().toString();
                if (email.isEmpty() ||pass.isEmpty()){
                    Toast.makeText(MainActivity.this,"Email or Password is empty",Toast.LENGTH_LONG).show();
                }else{
                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){

                            if(!task.isSuccessful()){

                                Toast.makeText(MainActivity.this,"Registration failed",Toast.LENGTH_LONG).show();

                            }else{
                                Toast.makeText(MainActivity.this,"Registration succesful",Toast.LENGTH_LONG).show();
                            }
                        }

                    });

                }

                break;
        }
    }
}
