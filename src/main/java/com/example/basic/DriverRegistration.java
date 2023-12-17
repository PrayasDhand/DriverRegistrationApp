package com.example.basic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.BreakIterator;

public class DriverRegistration extends AppCompatActivity {
    private static final int PICK_DRIVER_IMAGE_REQUEST = 1;
    private static final int PICK_LICENSE_IMAGE_REQUEST = 2;
    private ImageView imageView;
    private ImageView licenseImageView;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private DatePicker dobDatePicker;
    private EditText vehicleEditText;
    private DatabaseHelper databaseHelper;
    private TesseractHelper ocrHelper;
    private EditText contactEditText;
    private EditText licenseNoEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_driver);

        imageView = findViewById(R.id.imageView);
        licenseImageView = findViewById(R.id.licenseImageView);
        fullNameEditText = findViewById(R.id.fname2);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        dobDatePicker = findViewById(R.id.age); // Assuming this is the ID for Date of Birth DatePicker
        vehicleEditText = findViewById(R.id.vehicleType);
        licenseNoEditText = findViewById(R.id.licenseNo);
        contactEditText = findViewById(R.id.contact);
        Button chooseDriverImageBtn = findViewById(R.id.chooseDriverImageBtn);
        chooseDriverImageBtn.setOnClickListener(this::onChooseDriverImageClick);

        Button chooseLicenseImageBtn = findViewById(R.id.chooseLicenseImageBtn);
        chooseLicenseImageBtn.setOnClickListener(this::onChooseLicenseImageClick);
        databaseHelper = new DatabaseHelper(this);
        ocrHelper = new TesseractHelper(this, "eng");
    }
    private void onChooseDriverImageClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_DRIVER_IMAGE_REQUEST);
    }

    private void onChooseLicenseImageClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_LICENSE_IMAGE_REQUEST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ocrHelper.onDestroy();
    }



    public void onRegisterClick(View view) {

        String fullName = fullNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Extract year, month, and day from DatePicker
        int year = dobDatePicker.getYear();
        int month = dobDatePicker.getMonth();
        int day = dobDatePicker.getDayOfMonth();

        // Construct the Date of Birth string
        String dob = String.format("%04d-%02d-%02d", year, month + 1, day); // Month is zero-based
        String licenseNo = licenseNoEditText.getText().toString();
        String vehicleType = vehicleEditText.getText().toString();
        String contact = contactEditText.getText().toString();
        // Retrieve the chosen image's OCR result
        String ocrResultFromImage = ocrHelper.getOCRResult();

        if (ocrResultFromImage != null && !ocrResultFromImage.isEmpty()) {
            // Retrieve user information from OCR result
            String[] ocrUserInfo = extractUserInfoFromOCR(ocrResultFromImage);

            if (ocrUserInfo != null && ocrUserInfo.length == 5) {
                String ocrFirstName = ocrUserInfo[0];
                String ocrLastName = ocrUserInfo[1];
                String ocrAgeStr = ocrUserInfo[2];
                String ocrEmail = ocrUserInfo[3];
                String ocrPassword = ocrUserInfo[4];

                // Directly compare user details with values from the SQLite database
                if (databaseHelper.doesUserExist(ocrFirstName, ocrLastName, ocrEmail, ocrPassword)) {
                    // User details verified, proceed with registration
                    boolean success = databaseHelper.insertUser(fullName, email, password, dob, contact,licenseNo,vehicleType);

                    if (success) {
                        Toast.makeText(this, "Driver registered successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to register driver", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Show an error message
                    Toast.makeText(this, "Error: Details do not match. Please check and try again", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Show an error message for incorrect OCR result format
                Toast.makeText(this, "Error: Incorrect OCR result format", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show an error message for missing OCR result
            Toast.makeText(this, "Error: Unable to extract OCR result", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DRIVER_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            handleImageSelection(data.getData());
        } else if (requestCode == PICK_LICENSE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            handleLicenseImageSelection(data.getData());
        }
    }
    private void handleImageSelection(Uri imageUri) {
        imageView.setImageURI(imageUri);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            // Perform OCR or any other processing for the driver's image
            // ocrHelper.performOCRForDriverImage(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleLicenseImageSelection(Uri imageUri) {
        licenseImageView.setImageURI(imageUri);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            // Perform OCR or any other processing for the license image
            ocrHelper.performOCR(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String[] extractUserInfoFromOCR(String ocrResult) {
        // Split the OCR result into lines
        String[] lines = ocrResult.split("\n");

        // Check if there are at least 5 lines (adjust as needed based on your OCR result format)
        if (lines.length >= 5) {
            String firstName = lines[0].trim();
            String lastName = lines[1].trim();
            String ageStr = lines[2].trim(); // Assuming age is on the third line
            String email = lines[3].trim();
            String password = lines[4].trim();

            // Validate or process the extracted data as needed

            return new String[]{firstName, lastName, ageStr, email, password};
        } else {
            // Handle the case where the OCR result does not have enough lines
            return null;
        }
    }
}
