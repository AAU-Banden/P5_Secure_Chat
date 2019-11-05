package aau.itcom.group_2.p5_secure_chatting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccountActivity extends AppCompatActivity {
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    EditText firstNameEdit;
    EditText lastNameEdit;
    EditText emailEdit;
    EditText phoneNumberEdit;

    Context context;
    Toast missingInfoToast;
    Toast emailNotValidToast;
    Toast emailAlreadyExistToast;
    CharSequence missingInfoError = "Please fill out all the fields above...";
    CharSequence emailNotValid = "Please provide a valid email...";
    CharSequence emailAlreadyExist = "The email provided already exists...";
    int toastDuration = Toast.LENGTH_SHORT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        context = getApplicationContext();

        firstNameEdit = findViewById(R.id.editText_firstName);
        lastNameEdit = findViewById(R.id.editText_lastName);
        emailEdit = findViewById(R.id.editText_email);
        phoneNumberEdit = findViewById(R.id.editText_phoneNumber);



    }

    public void nextButtonClicked(View view){
        Intent myIntent = new Intent(CreateAccountActivity.this, CreateAccountPage2Activity.class);
        firstName = firstNameEdit.getText().toString();
        lastName = lastNameEdit.getText().toString();
        email = emailEdit.getText().toString();
        phoneNumber = phoneNumberEdit.getText().toString();
        missingInfoToast = Toast.makeText(context, missingInfoError, toastDuration);
        emailAlreadyExistToast = Toast.makeText(context, emailAlreadyExist, toastDuration);
        emailNotValidToast = Toast.makeText(context, emailNotValid, toastDuration);


        if(firstName.equals("") || lastName.equals("") || email.equals("") || phoneNumber.equals("")) {
            missingInfoToast.show();
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Does not check if the email exist. Only the format example@123.aaa
            emailEdit.setError("Email format is invalid (use example@email.com)");
            emailNotValidToast.show();
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("FIRST_NAME", firstName);
            bundle.putString("LAST_NAME", lastName);
            bundle.putString("EMAIL", email);
            bundle.putString("PHONE_NUMBER", phoneNumber);
            myIntent.putExtras(bundle);
            startActivity(myIntent);
        }









    }
    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


}
