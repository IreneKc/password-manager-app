package my.utar.p5_irene_chow_ooi_ling_2204382;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnAddPassword, btnAllPasswords, btnRecentlyDeleted;
    private ImageButton btnMoreOptions;
    private ListView listViewPasswords;

    private EditText etSearch;

    private DatabaseHelper databaseHelper;
    private ArrayList<PasswordModel> passwordList;
    private TextView tvEmptyMessage;
    private boolean showingDeleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddPassword = findViewById(R.id.btnAddPassword);
        //btnLogout = findViewById(R.id.btnLogout);
        btnAllPasswords = findViewById(R.id.btnAllPasswords);
        btnRecentlyDeleted = findViewById(R.id.btnRecentlyDeleted);
        //btnChangeMasterPassword = findViewById(R.id.btnChangeMasterPassword);
        btnMoreOptions = findViewById(R.id.btnMoreOptions);
        listViewPasswords = findViewById(R.id.listViewPasswords);
        etSearch = findViewById(R.id.etSearch);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        databaseHelper = new DatabaseHelper(MainActivity.this);
        databaseHelper.getWritableDatabase();

        btnAddPassword.setOnClickListener(v -> {
            if (showingDeleted) {
                Toast.makeText(this, "Please switch to All Passwords to add new entries", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                startActivity(intent);
            }
        });

        btnAllPasswords.setOnClickListener(v -> {
            showingDeleted = false;
            etSearch.setText(""); // clear search
            loadPasswords();
        });

        btnRecentlyDeleted.setOnClickListener(v -> {
            showingDeleted = true;
            etSearch.setText(""); // clear search
            loadPasswords();
        });

//        btnLogout.setOnClickListener(v -> {
//            new AlertDialog.Builder(MainActivity.this)
//                    .setTitle("Logout")
//                    .setMessage("Are you sure you want to logout?")
//                    .setPositiveButton("Yes", (dialog, which) -> {
//                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                    })
//                    .setNegativeButton("No", null)
//                    .show();
//        });
//
//        btnChangeMasterPassword.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, ChangeMasterPasswordActivity.class);
//            startActivity(intent);
//        });

        btnMoreOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, btnMoreOptions);

            popup.getMenu().add("Change Master Password");
            popup.getMenu().add("Logout");

            popup.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();

                if (title.equals("Change Master Password")) {
                    Intent intent = new Intent(MainActivity.this, ChangeMasterPasswordActivity.class);
                    startActivity(intent);
                } else if (title.equals("Logout")) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Logout")
                            .setMessage("Are you sure you want to logout?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .setNegativeButton("No", null)
                            .show();
                }

                return true;
            });

            popup.show();
        });

        //Search feature
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPasswords(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        listViewPasswords.setOnItemClickListener((parent, view, position, id) -> {
            PasswordModel selectedPassword = passwordList.get(position);

            if (!showingDeleted) {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra("id", selectedPassword.getId());
                intent.putExtra("siteName", selectedPassword.getSiteName());
                intent.putExtra("username", selectedPassword.getUsername());
                intent.putExtra("password", selectedPassword.getPassword());
                intent.putExtra("pinNumber", selectedPassword.getPinNumber());
                intent.putExtra("securityQuestion", selectedPassword.getSecurityQuestion());
                intent.putExtra("securityAnswer", selectedPassword.getSecurityAnswer());
                intent.putExtra("notes", selectedPassword.getNotes());
                startActivity(intent);
            } else {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Restore Password")
                        .setMessage("Restore this password entry to All Passwords?")
                        .setPositiveButton("Restore", (dialog, which) -> {
                            boolean success = databaseHelper.restorePassword(selectedPassword.getId());
                            if (success) {
                                Toast.makeText(MainActivity.this, "Password restored", Toast.LENGTH_SHORT).show();
                                loadPasswords();
                            } else {
                                Toast.makeText(MainActivity.this, "Restore failed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        listViewPasswords.setOnItemLongClickListener((parent, view, position, id) -> {
            PasswordModel selectedPassword = passwordList.get(position);

            if (!showingDeleted) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Move to Recently Deleted")
                        .setMessage("Move this password entry to Recently Deleted?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            boolean success = databaseHelper.moveToRecentlyDeleted(selectedPassword.getId());
                            if (success) {
                                Toast.makeText(MainActivity.this, "Moved to Recently Deleted", Toast.LENGTH_SHORT).show();
                                loadPasswords();
                            } else {
                                Toast.makeText(MainActivity.this, "Action failed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Permanently")
                        .setMessage("This entry will be permanently deleted. Continue?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            boolean success = databaseHelper.permanentlyDeletePassword(selectedPassword.getId());
                            if (success) {
                                Toast.makeText(MainActivity.this, "Entry permanently deleted", Toast.LENGTH_SHORT).show();
                                loadPasswords();
                            } else {
                                Toast.makeText(MainActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            return true;
        });

        loadPasswords();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPasswords();
    }

    //Load password
    private void loadPasswords() {
        if (showingDeleted) {
            passwordList = databaseHelper.getDeletedPasswords();
            btnAddPassword.setEnabled(false);
            btnAddPassword.setAlpha(0.5f);
        } else {
            passwordList = databaseHelper.getActivePasswords();
            btnAddPassword.setEnabled(true);
            btnAddPassword.setAlpha(1.0f);
        }

        updateTabUI();

        if (passwordList.isEmpty()) {

            listViewPasswords.setVisibility(View.GONE);
            tvEmptyMessage.setVisibility(View.VISIBLE);

            //Different message at two tabs
            if (showingDeleted) {
                tvEmptyMessage.setText("No deleted passwords.");
            } else {
                tvEmptyMessage.setText("No passwords yet.\nTap \"Add New Password\" to get started.");
            }

        } else {
            listViewPasswords.setVisibility(View.VISIBLE);
            tvEmptyMessage.setVisibility(View.GONE);
        }

        PasswordAdapter adapter = new PasswordAdapter(this, passwordList);
        listViewPasswords.setAdapter(adapter);
    }

    //Filter password
    private void filterPasswords(String query) {
        ArrayList<PasswordModel> filteredList = new ArrayList<>();

        for (PasswordModel pm : passwordList) {
            if (pm.getSiteName().toLowerCase().contains(query.toLowerCase()) ||
                    pm.getUsername().toLowerCase().contains(query.toLowerCase())) {

                filteredList.add(pm);
            }
        }

        PasswordAdapter adapter = new PasswordAdapter(this, filteredList);
        listViewPasswords.setAdapter(adapter);
    }

    //Switch tab
    private void updateTabUI() {
        if (showingDeleted) {
            btnRecentlyDeleted.setBackgroundResource(R.drawable.tab_selected);
            btnAllPasswords.setBackgroundResource(R.drawable.tab_unselected);
        } else {
            btnAllPasswords.setBackgroundResource(R.drawable.tab_selected);
            btnRecentlyDeleted.setBackgroundResource(R.drawable.tab_unselected);
        }
    }
}