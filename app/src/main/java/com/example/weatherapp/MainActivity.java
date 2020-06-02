package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private final String WEATHER_URL =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=9ffa4fe7cacb720943f39d857c5dc098&lang=ru&units=metric";

    private EditText editTextCity;
    @SuppressLint("StaticFieldLeak")
    private static TextView textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextCity = findViewById(R.id.editTextCity);
        textViewWeather = findViewById(R.id.textViewWeather);
    }

    public void onClickShowWeather(View view) {
        String city = editTextCity.getText().toString().trim();
        if (!city.isEmpty()){
            DownloadWeatherTask task = new DownloadWeatherTask();
            String url = String.format(WEATHER_URL, city);
            try {
                task.execute(url).get();//String content = task.execute(url).get();
                //Log.i("Json", content);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onClickShowStavropol(View view) {
        DownloadWeatherTask task = new DownloadWeatherTask();
        String url = String.format(WEATHER_URL, "Stavropol");
        try {
            task.execute(url).get();//String content = task.execute(url).get();
            //Log.i("Json", content);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class DownloadWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.i("Allresult", s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                JSONObject main = jsonObject.getJSONObject("main");
                String temperature = main.getString("temp");
                JSONArray JSONarray = jsonObject.getJSONArray("weather");
                JSONObject res = JSONarray.getJSONObject(0);
                String description = res.getString("description");
                String weather = String.format("%s\nТемпература: %s\nНа улице: %s",
                        city, temperature, description);
                textViewWeather.setText(weather);
                //Log.i("Fullresult", city + ", " + temperature + ", " + description);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
