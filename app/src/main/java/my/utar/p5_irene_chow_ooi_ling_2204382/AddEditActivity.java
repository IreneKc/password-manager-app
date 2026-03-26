package my.utar.p5_irene_chow_ooi_ling_2204382;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditActivity extends AppCompatActivity {

    private EditText etSiteName, etUsername, etPasswordEntry;
    private EditText etPinNumber, etSecurityQuestion, etSecurityAnswer, etNotes;
    private Button btnSave;
    private CheckBox checkBoxShowPassword;
    private DatabaseHelper databaseHelper;

    private int passwordId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        etSiteName = findViewById(R.id.etSiteName);
        etUsername = findViewById(R.id.etUsername);
        etPasswordEntry = findViewById(R.id.etPasswordEntry);
        etPinNumber = findViewById(R.id.etPinNumber);
        etSecurityQuestion = findViewById(R.id.etSecurityQuestion);
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer);
        etNotes = findViewById(R.id.etNotes);
        btnSave = findViewById(R.id.btnSave);
        checkBoxShowPassword = findViewById(R.id.checkBoxShowPassword);

        databaseHelper = new DatabaseHelper(AddEditActivity.this);

        if (getIntent().hasExtra("id")) {
            passwordId = getIntent().getIntExtra("id", -1);
            etSiteName.setText(getIntent().getStringExtra("siteName"));
            etUsername.setText(getIntent().getStringExtra("username"));
            etPasswordEntry.setText(getIntent().getStringExtra("password"));
            etPinNumber.setText(getIntent().getStringExtra("pinNumber"));
            etSecurityQuestion.setText(getIntent().getStringExtra("securityQuestion"));
            etSecurityAnswer.setText(getIntent().getStringExtra("securityAnswer"));
            etNotes.setText(getIntent().getStringExtra("notes"));
            btnSave.setText("Update Password");
        } else {
            btnSave.setText("Save Password");
        }

        checkBoxShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etPasswordEntry.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etPasswordEntry.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            etPasswordEntry.setSelection(etPasswordEntry.getText().length());
        });

        btnSave.setOnClickListener(v -> saveOrUpdatePassword());
    }

    private void saveOrUpdatePassword() {
        String siteName = etSiteName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPasswordEntry.getText().toString().trim();
        String pinNumber = etPinNumber.getText().toString().trim();
        String securityQuestion = etSecurityQuestion.getText().toString().trim();
        String securityAnswer = etSecurityAnswer.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (!validateInputs(siteName, username, password, pinNumber)) {
            return;
        }

        boolean success;

        if (passwordId == -1) {
            PasswordModel passwordModel = new PasswordModel(
                    siteName, username, password,
                    pinNumber, securityQuestion, securityAnswer, notes
            );
            success = databaseHelper.addPassword(passwordModel);

            if (success) {
                Toast.makeText(this, "Password saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save password", Toast.LENGTH_SHORT).show();
            }

        } else {
            PasswordModel passwordModel = new PasswordModel(
                    passwordId, siteName, username, password,
                    pinNumber, securityQuestion, securityAnswer, notes
            );
            success = databaseHelper.updatePassword(passwordModel);

            if (success) {
                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInputs(String siteName, String username, String password, String pinNumber) {
        clearErrors();

        if (TextUtils.isEmpty(siteName)) {
            etSiteName.setError("Site name is required");
            etSiteName.requestFocus();
            return false;
        }

        if (siteName.length() < 2) {
            etSiteName.setError("Site name must be at least 2 characters");
            etSiteName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return false;
        }

        if (username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters");
            etUsername.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPasswordEntry.setError("Password is required");
            etPasswordEntry.requestFocus();
            return false;
        }

        if (password.length() < 4) {
            etPasswordEntry.setError("Password must be at least 4 characters");
            etPasswordEntry.requestFocus();
            return false;
        }

        if (password.contains(" ")) {
            etPasswordEntry.setError("Password should not contain spaces");
            etPasswordEntry.requestFocus();
            return false;
        }

        if (!pinNumber.isEmpty() && pinNumber.length() < 4) {
            etPinNumber.setError("PIN must be at least 4 digits");
            etPinNumber.requestFocus();
            return false;
        }

        return true;
    }

    private void clearErrors() {
        etSiteName.setError(null);
        etUsername.setError(null);
        etPasswordEntry.setError(null);
        etPinNumber.setError(null);
    }
}