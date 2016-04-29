package ppl.b08.warunglaundry.view.penyedia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ppl.b08.warunglaundry.R;
import ppl.b08.warunglaundry.business.C;
import ppl.b08.warunglaundry.business.PreferencesManager;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt;
    EditText paswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);


        PreferencesManager manager = PreferencesManager.getInstance(this);
        if (manager.getIdValue() != -1) {
            Intent intent = manager.getRoleValue()==0 ?
                    new Intent(this,ppl.b08.warunglaundry.view.pengguna.HomeActivity.class) :
                    new Intent(this, ppl.b08.warunglaundry.view.penyedia.HomeActivity.class);
            startActivity(intent);
            finish();
        }

        emailEt = (EditText) findViewById(R.id.email_txt);
        paswordEt = (EditText) findViewById(R.id.password_txt);
        Button registerBtn = (Button) findViewById(R.id.register);
        Button loginBtn = (Button) findViewById(R.id.login);
        TextView changeBtn = (TextView) findViewById(R.id.change_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ppl.b08.warunglaundry.view.pengguna.LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void login() {
        final String email = emailEt.getText().toString();
        final String pass = paswordEt.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(this, "Email wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.isEmpty()) {
            Toast.makeText(this, "Passeord wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = C.HOME_URL + "/login";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int r = response.getInt("status");
                    if (r == 1) {
                        //TODO Sukses
                        PreferencesManager.getInstance(LoginActivity.this).setIdValue(111);
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, "Email/password tidak sesuai", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Kesalahan jaringan, coba kembali nanti", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Kesalahan jaringan, coba kembali nanti", Toast.LENGTH_SHORT).show();
                Log.e("volley", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> re = new HashMap<>();
                re.put("email", email);
                re.put("pasword", pass);
                return re;
            }
        };

        //TODO delete this blok
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        //VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}