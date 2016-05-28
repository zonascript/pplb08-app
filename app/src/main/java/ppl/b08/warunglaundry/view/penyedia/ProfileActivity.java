package ppl.b08.warunglaundry.view.penyedia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ppl.b08.warunglaundry.R;
import ppl.b08.warunglaundry.business.C;
import ppl.b08.warunglaundry.business.PreferencesManager;
import ppl.b08.warunglaundry.business.VolleySingleton;

public class ProfileActivity extends AppCompatActivity {
    String id;
    TextView namaEdt;
    TextView emailEdt;
    TextView noHPEdt;
    TextView alamat1;
    TextView harga1;
    TextView rate1;
    TextView jangkauan1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);
        getSupportActionBar().setTitle("Profil Penyedia");
        namaEdt = (TextView) findViewById(R.id.name1);
        emailEdt = (TextView) findViewById(R.id.email1);
        noHPEdt = (TextView) findViewById(R.id.telpon1);
        alamat1 = (TextView) findViewById(R.id.alamat1);
        harga1 = (TextView) findViewById(R.id.harga1);
        rate1 = (TextView) findViewById(R.id.rate1);
        jangkauan1 = (TextView) findViewById(R.id.jangkauan1);
        Button updateBtn = (Button) findViewById(R.id.ubah_btn);
        Button kembali = (Button) findViewById(R.id.back_btn);
        getData();
        System.out.println(PreferencesManager.getInstance(ProfileActivity.this).getToken());
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, PasswordEditActivity.class);
                Bundle data = new Bundle();
                data.putString("nama", (String) namaEdt.getText());
                data.putString("email", (String) emailEdt.getText());
                data.putString("noHp", (String) noHPEdt.getText());
                intent.putExtras(data);
                startActivity(intent);
            }
        });
        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();
            }
        });
    }
    public void getData() {

        String url = C.HOME_URL+"/getData/"+ PreferencesManager.getInstance(ProfileActivity.this).getToken();
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject hasil = new JSONObject(response);
                    id = hasil.getString("id");
                    getDetails();
                } catch (JSONException e) {
                    Toast.makeText(ProfileActivity.this, "Kesalahan jaringan, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, "Kesalahan jaringan, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }

    public void getDetails() {

        String url = C.HOME_URL+"/getDetails";
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject hasil = new JSONObject(response);
                    int status = hasil.getInt("status");
                    if (status == 1 ){
                        JSONObject user = hasil.getJSONObject("user");
                        namaEdt.setText(user.getString("name"));
                        noHPEdt.setText(user.getString("nomor_hp"));
                        emailEdt.setText(user.getString("email"));
                        System.out.println(response);
                    }
                    else{
                        Toast.makeText(ProfileActivity.this, "Terjadi kesalahan, silahkan coba kembali", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(ProfileActivity.this, "Kesalahan jaringan, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, "Kesalahan jaringan, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> a = new HashMap<>();
                a.put("token", PreferencesManager.getInstance(ProfileActivity.this).getToken());
                a.put("id",id);
                return a;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }
}

