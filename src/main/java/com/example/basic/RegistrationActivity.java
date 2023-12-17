package com.example.basic;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.basic.LoginActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private final DatabaseHelper1 dbHelper = new DatabaseHelper1(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.register_page);

        // Set up the UI components

        // Example: Set up a button click listener
        findViewById(R.id.regBtn).setOnClickListener(v -> {
            // Call the method to handle registration when the button is clicked
            handleRegistration();
        });
    }

    private void handleRegistration() {
        // Retrieve values from EditText fields
        EditText nameEditText = findViewById(R.id.edText1);
        EditText emailEditText = findViewById(R.id.editText2);
        EditText passwordEditText = findViewById(R.id.editText3);
        EditText contactEditText = findViewById(R.id.editText4);

        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String contact = contactEditText.getText().toString();

        // Check if any field is empty before proceeding with registration
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Driver object
        Driver driver = new Driver(0, name, email, password, contact);

        // Open the SQLite database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues to insert data into the SQLite database
        ContentValues values = new ContentValues();
        values.put("name", driver.getName());
        values.put("email", driver.getEmail());
        values.put("password", driver.getPassword());
        values.put("contact", driver.getContact());

        // Insert the data into the SQLite database
        long rowId = db.insert("Drivers", null, values);

        // Close the SQLite database
        db.close();

        // Open the Firebase Realtime Database reference
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference("drivers");

        // Push the data to the Firebase Realtime Database
        String firebaseKey = firebaseRef.push().getKey();
        DatabaseReference driverRef = firebaseRef.child(firebaseKey);
        driverRef.setValue(driver);

        if (rowId > 0) {
            // Show a toast indicating successful registration
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();

            // Use a Handler to delay the redirection to LoginActivity
            new Handler().postDelayed(() -> {
                // Redirect to LoginActivity
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);

                // Finish the current activity to remove it from the stack
                finish();
            }, 2000); // Delay in milliseconds (e.g., 2000 milliseconds = 2 seconds)
        } else {
            // Show a toast indicating that data was not saved
            Toast.makeText(this, "Data not saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void redirectToLoginPage(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Optional: finish the current activity to prevent going back to it
    }
}
