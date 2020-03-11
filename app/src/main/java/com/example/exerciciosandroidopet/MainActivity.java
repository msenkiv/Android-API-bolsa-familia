package com.example.exerciciosandroidopet;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private List<String> names;
    private String anoMes = "202001";

    private String cidade = "4106902";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textName);

        names = new ArrayList<>();
    }

    public void btnCarregarEvent(View v){
        carregarDados();
    }

    private void generateRequest(String url){
        String endpoint = url;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, endpoint, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println(">>>>>>>>>>>>");
                System.out.println(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        System.out.println("CALL");
        APISingleton.getInstance(this).addToRequestQueue(request);
    }

    private void showData() {
        String names_str = "";
        for(String name : names){
            names_str += name + "\n";
        }
        textView.setText(names_str);
    }

    private void carregarDados(){
        String endpoint = "http://www.transparencia.gov.br/api-de-dados/bolsa-familia-por-municipio?mesAno="+this.anoMes+"&codigoIbge="+this.cidade+"&pagina=1";
        System.out.println(endpoint);
        generateRequest(endpoint);
    }
}
