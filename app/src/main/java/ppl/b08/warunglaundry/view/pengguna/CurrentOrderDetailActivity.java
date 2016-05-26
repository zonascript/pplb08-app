package ppl.b08.warunglaundry.view.pengguna;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ppl.b08.warunglaundry.Entity.Order;
import ppl.b08.warunglaundry.R;
import ppl.b08.warunglaundry.business.C;
import ppl.b08.warunglaundry.business.PreferencesManager;
import ppl.b08.warunglaundry.business.VolleySingleton;

public class CurrentOrderDetailActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    LatLng myLoc;
    static final float zoom = 15;
    long orderId;
    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order_detail);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        order = (Order) getIntent().getSerializableExtra(C.KEY_ORDER);
        if (order == null) finish();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getOrder();

        LatLng place = new LatLng(order.getLat(),order.getLng());
        mMap.addMarker(new MarkerOptions().position(place).title(order.getNamaProvider()).snippet("Lokasi penyedia laundry").flat(true));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place, zoom));

    }

    public void getOrder() {
       // order = new Order(orderId, 1, "Aishy Laundry", 0, 0,"08996222482", -6.3656374,106.8409036, "Rumah warna kuning, pager hijau nomer 41");
      //  double harga = 7000;
        String url = C.HOME_URL + "/getDetails";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject ob = new JSONObject(response);
                    ob = ob.getJSONObject("user");
                    String no = ob.getString("nomor_hp");
                    gantiNoHP(no);
                } catch (JSONException e) {
                    Toast.makeText(CurrentOrderDetailActivity.this, C.KONEKSI_GAGAL, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CurrentOrderDetailActivity.this, C.KONEKSI_GAGAL, Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> a = new HashMap<>();
                a.put("token", PreferencesManager.getInstance(CurrentOrderDetailActivity.this).getToken());
                a.put("id", order.getIdProvider()+"");
                return a;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);

        TextView namaTxt = (TextView) findViewById(R.id.nama_txt);
        TextView status = (TextView) findViewById(R.id.status_txt);


        EditText detil = (EditText) findViewById(R.id.detil_txt);
        EditText berat = (EditText) findViewById(R.id.berat_txt);
        EditText hargaSatuan = (EditText) findViewById(R.id.harga_satuan_txt);
        EditText hargaTxt = (EditText) findViewById(R.id.harga_txt);

        namaTxt.setText(order.getNamaProvider());

        detil.setText(order.getDetilLokasi());
        status.setText("Status : "+order.getStatusStr());
        status.setTextColor(Color.parseColor(order.getColor()));
        String message = order.getBerat()==0?"-":String.format("%.02f", order.getBerat())+" kg";
        berat.setText(message);
        double tmp = order.getBerat()==0? 0 : order.getHargaTotal()/order.getBerat();

        hargaSatuan.setText(String.format("%.02f", tmp));
        hargaTxt.setText(String.format("%.02f", order.getHargaTotal()));
    }

    private void gantiNoHP(String ho) {
        EditText phoneTxt = (EditText) findViewById(R.id.phone_txt);
        phoneTxt.setText(ho);
    }
}