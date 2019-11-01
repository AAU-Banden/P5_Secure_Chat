package aau.itcom.group_2.p5_secure_chatting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccountActivity extends AppCompatActivity {
    String firstName;
    String lastName;
    String email;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        final EditText firstNameEdit = findViewById(R.id.editText_firstName);
        final EditText lastNameEdit = findViewById(R.id.editText_lastName);
        final EditText emailEdit = findViewById(R.id.editText_email);
        final EditText phoneNumberEdit = findViewById(R.id.editText_emailLogin);





        final Button loginButton = findViewById(R.id.button_next);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(CreateAccountActivity.this, CreateAccountPage2Activity.class);
                firstName = firstNameEdit.getText().toString();
                lastName = lastNameEdit.getText().toString();
                email = emailEdit.getText().toString();
                /*
                TO DO:
                Email should be checked for correct formalia
                 */
                phoneNumber = phoneNumberEdit.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("FIRST_NAME", firstName);
                bundle.putString("LAST_NAME", lastName);
                bundle.putString("EMAIL", email);
                bundle.putString("PHONE_NUMBER", phoneNumber);
                myIntent.putExtras(bundle);
                startActivity(myIntent);


            }
        });






    }

}
