package com.example.ulangan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String REGIST_URL = "http://10.0.2.2/activity_main/regist.php";

    EditText Username, Email, Password, ConfirmPassword;
    Button btnLogin, btnRegister;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        Username = findViewById(R.id.Username);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        ConfirmPassword = findViewById(R.id.ConfirmPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        // Tombol Back ke Login
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        });

        // Tombol Register
        btnRegister.setOnClickListener(v -> {
            String sUsername = Username.getText().toString().trim();
            String sEmail = Email.getText().toString().trim();
            String sPassword = Password.getText().toString().trim();
            String sConfirmPassword = ConfirmPassword.getText().toString().trim();

            // Validasi Input (PERBAIKAN)
            if (sUsername.isEmpty() || sEmail.isEmpty() || sPassword.isEmpty()) {
                Toast.makeText(MainActivity.this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!sPassword.equals(sConfirmPassword)) {
                Toast.makeText(MainActivity.this, "Password tidak cocok!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kirim data ke server
            registerUser(sUsername, sEmail, sPassword);
        });
    }

    private void registerUser(final String username, final String email, final String password) {
        progressDialog.setMessage("Mendaftarkan...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGIST_URL,
                response -> {
                    Log.d("API_RESPONSE", "Server Response: " + response);
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("BERHASIL")) {
                            Toast.makeText(MainActivity.this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, Login.class));
                            finish();
                        } else {
                            String message = jsonObject.optString("message", "Registrasi gagal.");
                            Toast.makeText(MainActivity.this, "Gagal: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Kesalahan parsing JSON!", Toast.LENGTH_SHORT).show();
                        Log.e("RegisterError", "JSON Exception: " + e.getMessage() + " | Response: " + response);
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    String errorMsg = (error.getMessage() != null) ? error.getMessage() : "Terjadi kesalahan jaringan.";
                    Toast.makeText(MainActivity.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("RegisterError", "Volley Error: " + errorMsg);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        // **Tambahkan Retry Policy untuk menghindari Timeout**
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, // Waktu tunggu 5 detik
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
