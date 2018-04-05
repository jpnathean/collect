package org.odk.collect.android.utilities;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class HttpUtility {

    private String charset = "UTF-8";
    private HttpURLConnection conn;
    private StringBuilder result;
    private URL urlObj;
    private JSONObject jObj = null;
    private StringBuilder sbParams;
    private final int CONNECTION_TIMEOUT = 15000;

    public JSONObject httpPost(String url, String method,
                               HashMap<String, String> params, String token){
        final int READ_TIMEOUT = 10000;

        //adds post parameters
        sbParams = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0){
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), charset));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }
        try {
            //creates a connection
            urlObj = new URL(url);

            conn = (HttpURLConnection) urlObj.openConnection();

            conn.setDoOutput(true);

            conn.setRequestMethod(method);

            conn.setRequestProperty("Accept-Charset", charset);
            conn.setRequestProperty("Authorization", "OAuth " + token);

            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);

            conn.connect();

            String paramsString = sbParams.toString();

            //writes to connection stream
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(paramsString);
            wr.flush();
            wr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.d("HttpUtility", "result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.disconnect();

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(result.toString());
        } catch (JSONException e) {
            Log.e("HttpUtility", "Error parsing data " + e.toString());
        }

        // return JSON Object
        return jObj;
    }

    public JSONObject httpGet(String url, String method){
        sbParams = new StringBuilder();

        if (sbParams.length() != 0) {
            url += "?" + sbParams.toString();
        }

        try {
            //creates connection
            urlObj = new URL(url);

            conn = (HttpURLConnection) urlObj.openConnection();

            conn.setDoOutput(false);

            conn.setRequestMethod(method);

            conn.setRequestProperty("Accept-Charset", charset);

            conn.setConnectTimeout(CONNECTION_TIMEOUT);

            conn.connect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.d("HttpUtility", "result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.disconnect();

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(result.toString());
        } catch (JSONException e) {
            Log.e("HttpUtility", "Error parsing data " + e.toString());
        }

        // return JSON Object
        return jObj;
    }
}
