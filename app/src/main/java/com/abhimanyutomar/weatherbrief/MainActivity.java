package com.abhimanyutomar.weatherbrief;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    DownloadText downloadText;
    EditText et;
    EditText et2;
    String s2;
    String display;
    String display2;
    public void getData(View view){
        //New instance of class
        downloadText=new DownloadText();
        //Breakdown of url in 3 strings to accommodate user entered location
        String s1="http://api.openweathermap.org/data/2.5/weather?q=";
        String s3="&units=metric&appid=a61e782e8b4101b3699565a0ef878606";
        AutoCompleteTextView atv=(AutoCompleteTextView) findViewById(R.id.actv);
        //hides the keyboard when its not needed
        InputMethodManager mgr2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr2.hideSoftInputFromWindow(atv.getWindowToken(), 0);
        s2=String.valueOf(atv.getText());
        String s=s1+s2+s3;
        //Send the url to the data downloader function
        //try and catch will prevent the app from crashing in case of permission error or no internet connection or anything else
        try {
            String res=downloadText.execute(s).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Data Downloader function, gets data from openWeatherapi
    public class  DownloadText extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            String result="";
            try {
                //establish connection
                url = new URL(strings[0]);
                HttpURLConnection urlConnection;
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream inputStream=urlConnection.getInputStream();
                // read the data from the stream
                InputStreamReader inputStreamReader= new InputStreamReader(inputStream);
                int data=  inputStreamReader.read();
                // Keep reading until end is reached
                while(data!=-1){
                    char current=(char) data;
                    result+=current;
                    data=inputStreamReader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "OOPS something went wrong! ";
            }
        }
        //Works on string returned by doInBackground() function
        @Override
        protected void onPostExecute(String r){
            super.onPostExecute(r);
            try {
                //Using json to separate useful data from the whole data
                JSONObject jsonObject= new JSONObject(r);
                String wet=jsonObject.getString("weather");
                JSONArray arr= new JSONArray(wet);
                JSONObject useful=arr.getJSONObject(0);
                String a=useful.getString("main");
                String b=useful.getString("description");
                display="Main: "+a+"\n"+"Description: "+b;
                et.setText(display);
                et.setAlpha(0.8f);
                et2.setAlpha(0.8f);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject2 = new JSONObject(r);
                String x=jsonObject2.getJSONObject("main").getString("temp");
                String y= jsonObject2.getJSONObject("main").getString("humidity");
                display2="Temperature: "+x+" 'C"+"\n"+"Humidity: "+y+" %";
                et2.setText(display2);
            } catch (JSONException e) {
                Log.i("TEMPERATURE: ",String.valueOf(e));
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et=(EditText)findViewById(R.id.editText3);
        et2=(EditText)findViewById(R.id.editText4);
        et.setAlpha(0f);
        et2.setAlpha(0f);
    }

}