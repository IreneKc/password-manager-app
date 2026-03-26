package my.utar.p5_irene_chow_ooi_ling_2204382;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etPassword;
    private Button btnLogin;

    private static final String PREF_NAME = "PasswordManagerPrefs";
    private static final String KEY_MASTER_PASSWORD = "master_password";
    private static final String DEFAULT_MASTER_PASSWORD = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        initializeDefaultMasterPassword();

        btnLogin.setOnClickListener(v -> {
            String input = etPassword.getText().toString().trim();
            String savedPassword = getMasterPassword();

            if (input.equals(savedPassword)) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeDefaultMasterPassword() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        if (!preferences.contains(KEY_MASTER_PASSWORD)) {
            preferences.edit().putString(KEY_MASTER_PASSWORD, DEFAULT_MASTER_PASSWORD).apply();
        }
    }

    private String getMasterPassword() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return preferences.getString(KEY_MASTER_PASSWORD, DEFAULT_MASTER_PASSWORD);
    }
}