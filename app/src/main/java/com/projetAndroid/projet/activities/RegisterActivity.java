package com.projetAndroid.projet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.projetAndroid.projet.R;
import com.projetAndroid.projet.database.TripDatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private TripDatabaseHelper dbHelper;
    private EditText etUsername, etPassword, etConfirmPassword;
    private RadioGroup rgRegUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new TripDatabaseHelper(this);

        etUsername = findViewById(R.id.etRegUsername);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        rgRegUserType = findViewById(R.id.rgRegUserType);

        Button btnRegister = findViewById(R.id.btnDoRegister);
        TextView tvGoLogin = findViewById(R.id.tvGoLogin);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 4) {
                Toast.makeText(this, "Mot de passe trop court (min 4 caractères)", Toast.LENGTH_SHORT).show();
                return;
            }

            String userType = rgRegUserType.getCheckedRadioButtonId() == R.id.rbRegConducteur
                    ? "Conducteur" : "Passager";

            if (dbHelper.registerUser(username, password, userType)) {
                Toast.makeText(this, "Compte créé ! Connectez-vous.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else {
                Toast.makeText(this, "Ce nom d'utilisateur existe déjà", Toast.LENGTH_SHORT).show();
            }
        });

        tvGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
    }
}
