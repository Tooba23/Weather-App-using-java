package com.frsarker.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String IMG_URL = "https://openweathermap.org/img/w/";

    String CITY = "karachi,pk";
    String API = "52c60e8764f3814ef2623151bd8ecd07";
    String CITY_NAME;

    TextView updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt, cityName, visibilityTxt;
    EditText addressTxt;
    ImageView searchBtn, weatherIconView;
    RelativeLayout rl ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updated_atTxt = findViewById(R.id.updated_at);
        statusTxt = findViewById(R.id.status);
        tempTxt = findViewById(R.id.temp);
        temp_minTxt = findViewById(R.id.temp_min);
        temp_maxTxt = findViewById(R.id.temp_max);
        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        windTxt = findViewById(R.id.wind);
        pressureTxt = findViewById(R.id.pressure);
        humidityTxt = findViewById(R.id.humidity);
        addressTxt = findViewById(R.id.address);
        cityName = findViewById(R.id.cityName);
        searchBtn = findViewById(R.id.searchBtn);
        weatherIconView = findViewById(R.id.weatherIcon);
        visibilityTxt = findViewById(R.id.visibility);
        rl=findViewById(R.id.mainBox);
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                CITY = addressTxt.getText().toString();
                new weatherTask().execute();
            }

        });
        new weatherTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE */
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            //addressTxt = (EditText) findViewById(R.id.address);
            String str = addressTxt.getEditableText().toString();
            //edt.findViewById(R.id.address);

            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                // JSONObject visibility=jsonObj.getJSONObject("visibility");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "°C";
                String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
                String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                String pressure = main.getString("pressure") + " mb";
                String humidity = main.getString("humidity") + " %";
                String weatherVisibility = jsonObj.getString("visibility") + " ft";

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed") + " km/hr";
                String weatherDescription = weather.getString("description");
                String iconName = weather.getString("icon");
                if (iconName.contains("n")) {
                    rl.setBackgroundResource(R.drawable.night_gradient);

                } else if(iconName.contains("d")) {
                    rl.setBackgroundResource(R.drawable.morning_gradient);
                }

                String address = jsonObj.getString("name") + ", " + sys.getString("country");
                Picasso.get().load(IMG_URL + iconName + ".png").into(weatherIconView);

                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                windTxt.setText(windSpeed);
                pressureTxt.setText(pressure);
                humidityTxt.setText(humidity);
                cityName.setText(address);
                visibilityTxt.setText(weatherVisibility);
                /* Views populated, Hiding the loader, Showing the main design */
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }

}
