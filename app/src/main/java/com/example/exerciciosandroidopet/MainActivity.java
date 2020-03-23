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

import com.example.exerciciosandroidopet.enums.Consts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private TextView city, UF, total, media, maxVal , minVal;
    private EditText editMunicipio, editYear;

    private Double totalValue, medValue, maxValue, minValue;
    private String nomecity, UF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.city);
        UF = findViewById(R.id.UF);
        total = findViewById(R.id.total);
        media = findViewById(R.id.media);
        maxVal  = findViewById(R.id.maxVal );
        minVal = findViewById(R.id.minVal);

        editMunicipio = findViewById(R.id.editMunicipio);
        editYear = findViewById(R.id.editYear);
    }



    private boolean verifyData(View view, String ibge) {
        boolean emptCity = editMunicipio.getText().toString().trim().equals("");
        boolean emptYear = editYear.getText().toString().trim().equals("");

        if (!TextUtils.isDigitsOnly(ibge) || emptCity) {
            Snackbar snackBar = Snackbar.make(view, 'ERRO CONSULTA', Snackbar.LENGTH_SHORT);
            snackBar.show();
            return false;

        } else if (emptYear) {
            Snackbar snackBar = Snackbar.make(view, 'ANO VAZIO', Snackbar.LENGTH_SHORT);
            snackBar.show();
            return false;

        } else {
            return true;
        }
    }

  

    private void runRequestYear(String ibge) {
        for (int i = 1; i <= 12; i++) {
            String month = validateMonth(i);

            String dateConst = editYear.getText().toString() + month;
            String endpoint = String.format(API_DADOS_SITE + "?monthAno=%s&ibge=%s&pagina=1",
                    dateConst, ibge
            );

            runReq(endpoint, 0);
        }
    }

    private String validateMonth(int i) {
        String month;

        if (i < 10) {
            month = "0" + i;
        } else {
            month = Integer.toString(i);
        }

        return month;
    }

    private void runReq(String url, int operation) {
        if (operation == 0) {
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject dataObject = response.getJSONObject(0);
                                extactValueObj(dataObject);
                                setTextosView();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) { }
            });

            APISingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        } else if (operation == 1) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                editMunicipio.setText(response.get("id").toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) { }
            });

            APISingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    }

    private void extactValueObj(JSONObject dataObject) throws JSONException {
        totalValue += Double.parseDouble(dataObject.getString("valor"));
        medValue += Double.parseDouble(dataObject.getString("quantidadeBeneficiados"));
        nomecity = dataObject.getJSONObject("municipio").getString("nomeIBGE");
        UF = dataObject.getJSONObject("municipio").getJSONObject("uf").getString("nome");

        double valorMensal = Double.parseDouble(dataObject.getString("valor"));
        if(maxValue <= valorMensal) {
            maxValue = valorMensal;
        }
        if (minValue >= valorMensal) {
            minValue = valorMensal;
        }
    }

    @SuppressLint("DefaultLocale")
    private void setTextosView() {
        city.setText("Nome da city: " + nomecity);
        UF.setText("Sigla do UF: " + UF);
        total.setText("Montante anual: R$" + String.format("%.2f", totalValue));
        media.setText("Media de beneficiados: " + String.format("%.2f", medValue / 12) + " pessoas");
        maxVal .setText("Menor valor cedido: R$" + String.format("%.2f", maxValue));
        minVal.setText("Maior valor cedido: R$" + String.format("%.2f", minValue));
    }


    // CONVERTER FUNCTIONS
    public void btnCarregarIBGEEvent(View v) {
        loadIbge();
    }

    private void loadIbge() {
        String city = editMunicipio.getText().toString().replace(' ', '-');

        String endpoint = IBGE_SITE + city;
        runReq(endpoint, 1);
    }

    public void btnCarregarEvent(View v){
        loadData(v);
    }

    private void loadData(View view) {
        String iCode = editMunicipio.getText().toString();

        if (verifyData(view, iCode)) {
            resetVaView();
            runRequestYear(iCode);
        }
    }
    private void resetVaView() {
        totalValue = 0.0;
        medValue = 0.0;
        maxValue = -9999999999.0;
        minValue = 99999999999.0;
    }
}