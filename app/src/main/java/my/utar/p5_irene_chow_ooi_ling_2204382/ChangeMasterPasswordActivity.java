package my.utar.p5_irene_chow_ooi_ling_2204382;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeMasterPasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnUpdateMasterPassword;

    private static final String PREF_NAME = "PasswordManagerPrefs";
    private static final String KEY_MASTER_PASSWORD = "master_password";
    private static final String DEFAULT_MASTER_PASSWORD = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_master_password);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnUpdateMasterPassword = findViewById(R.id.btnUpdateMasterPassword);

        btnUpdateMasterPassword.setOnClickListener(v -> updateMasterPassword());
    }

    private void updateMasterPassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        clearErrors();

        String savedPassword = getSavedMasterPassword();

        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Current password is required");
            etCurrentPassword.requestFocus();
            return;
        }

        if (!currentPassword.equals(savedPassword)) {
            etCurrentPassword.setError("Current password is incorrect");
            etCurrentPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 4) {
            etNewPassword.setError("New password must be at least 4 characters");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.contains(" ")) {
            etNewPassword.setError("New password should not contain spaces");
            etNewPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm the new password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        saveMasterPassword(newPassword);
        Toast.makeText(this, "Master password updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String getSavedMasterPassword() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return preferences.getString(KEY_MASTER_PASSWORD, DEFAULT_MASTER_PASSWORD);
    }

    private void saveMasterPassword(String newPassword) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        preferences.edit().putString(KEY_MASTER_PASSWORD, newPassword).apply();
    }

    private void clearErrors() {
        etCurrentPassword.setError(null);
        etNewPassword.setError(null);
        etConfirmPassword.setError(null);
    }
}