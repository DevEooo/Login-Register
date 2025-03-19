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

public class Login extends AppCompatActivity {

    private static final String LOGIN_URL = "http://10.0.2.2/activity_main/login.php";

    EditText Username, Password;
    Button btnLogin, btnRegister;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        Username = findViewById(R.id.UsernameLogin);
        Password = findViewById(R.id.PasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        // Tombol Back ke Register
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        });

        // Tombol Login
        btnLogin.setOnClickListener(v -> {
            String sUsername = Username.getText().toString().trim();
            String sPassword = Password.getText().toString().trim();

            if (sUsername.isEmpty() || sPassword.isEmpty()) {
                Toast.makeText(Login.this, "Username dan Password harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(sUsername, sPassword);
        });
    }

    private void loginUser(final String username, final String password) {
        progressDialog.setMessage("Memproses login...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    Log.d("API_RESPONSE", "Server Response: " + response);
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("BERHASIL")) {
                            Toast.makeText(Login.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, Gallery.class)); // Ganti dengan activity tujuan setelah login
                            finish();
                        } else {
                            String message = jsonObject.optString("message", "Login gagal.");
                            Toast.makeText(Login.this, "Gagal: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(Login.this, "Kesalahan parsing JSON!", Toast.LENGTH_SHORT).show();
                        Log.e("LoginError", "JSON Exception: " + e.getMessage() + " | Response: " + response);
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    String errorMsg = (error.getMessage() != null) ? error.getMessage() : "Terjadi kesalahan jaringan.";
                    Toast.makeText(Login.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("LoginError", "Volley Error: " + errorMsg);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, // Waktu tunggu 5 detik
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
