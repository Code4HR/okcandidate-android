package com.example.jsonparser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
 
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;
 
public class JSONParser {
 
    static InputStream is = null;
    static JSONObject jObj = null;
    static JSONTokener jsonTokener = null;
    static String json = "";
 
    // constructor
    public JSONParser() {
 
    }
 
    public JSONTokener getJSONFromUrl(String url) throws Exception {

        URL urlObj;
        urlObj = createURLObj(url);

        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) urlObj.openConnection();
            urlConnection.connect();
        }
        catch (java.io.IOException e) {
            Log.e("JSONPARSER", "Error parsing data " + e.toString());
            throw e;
        }

        jsonTokener = GetJSON(urlConnection);

        urlConnection.disconnect();

        Log.d("JSONPARSER", "Complete?");

        return jsonTokener;
    }

    private URL createURLObj(String url) throws Exception {
        URL urlObj;

        // Making HTTP request
        try {
            urlObj = new URL(url);
        }
        catch (java.net.MalformedURLException e) {
            Log.e("JSONPARSER", "Error parsing data " + e.toString());
            throw e;
        }

        return urlObj;
    }

    public JSONTokener postJSONFromUrl(String url, String json) throws Exception {

        URL urlObj;
        urlObj = createURLObj(url);

        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) urlObj.openConnection();
            urlConnection.setRequestMethod( "POST" );
            urlConnection.setRequestProperty( "Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            //set the content length of the body
            urlConnection.setRequestProperty("Content-length", json.getBytes().length + "");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);

            //send the json as body of the request
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(json.getBytes("UTF-8"));
            outputStream.close();
            urlConnection.connect();
        }
        catch (java.io.IOException e) {
            Log.e("JSONPARSER", "Error parsing data " + e.toString());
            throw e;
        }



        jsonTokener = GetJSON(urlConnection);

        urlConnection.disconnect();

        Log.d("JSONPARSER", "Complete?");

        return jsonTokener;
    }

    private JSONTokener GetJSON(HttpURLConnection urlConnection) throws Exception {

        int bufferSize = 1024;

        try {
            InputStream in;
            try {
                in = new BufferedInputStream(urlConnection.getInputStream());

            }
            catch (java.io.IOException e) {
                Log.e("JSONPARSER", "Error parsing data " + e.toString());
                throw e;
            }
            catch (Exception e) {
                Log.e("JSONPARSER", "Error parsing data " + e.toString());
                throw e;
            }

            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            String line = null;
            while((bytesRead = in.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, bytesRead));
            }
            in.close();
            json = sb.toString();

            try {
                jsonTokener = new JSONTokener(json);
            } catch (Exception e) {
                Log.e("JSONPARSER", "Error parsing data " + e.toString());
                throw e;
            }


        }
        catch (Exception e) {
            Log.e("JSONPARSER", "Error parsing data " + e.toString());
            throw e;
        }

        return jsonTokener;
    }

}