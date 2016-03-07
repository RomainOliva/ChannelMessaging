package romain.oliva.channelmessaging.network;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by RomainMac on 02/02/2016.
 */
public class NetworkResultProvider extends AsyncTask<String,Integer, String> {
    private ArrayList<onWsRequestListener> listeners = new ArrayList<onWsRequestListener>();
    private HashMap<String, String> nameValuePairs;
    private int requestCode;

    String requestURL = "";

    public NetworkResultProvider(int requestCode, String url, HashMap<String, String> nameValuePairs) {
        this.nameValuePairs = nameValuePairs;
        this.requestURL = "http://www.raphaelbischof.fr/messaging/?function=" + url;
        this.requestCode = requestCode;
    }

    @Override
    protected String doInBackground(String... arg0)  {


        String response = "";

        try {
            URL url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(nameValuePairs));

            writer.flush();
            writer.close();
            os.close();

            int responseCode=conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line=br.readLine()) != null) {
                    response+=line;
                }

            } else {

                response="";
            }

        } catch (Exception e) {

            e.printStackTrace();

        }


        return response;


       /* String content = null;


        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            //TODO Handler
        }

        // Execute HTTP Post Request
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
            content = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            //TODO Handler
        }

        return content;

        */
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        boolean first = true;

        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) first = false;
            else result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8")); result.append("="); result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        newWsRequestCompleted(s);
    }

    public void setOnNewWsRequestListener(onWsRequestListener listener)
    {
        this.listeners.add(listener);
    }

    private void newWsRequestCompleted(String response){
        for(onWsRequestListener oneListener : listeners){
            oneListener.onCompleted(requestCode, response);
        }
    }

    private void newWsRequestError(String error){
        for(onWsRequestListener oneListener : listeners){
            oneListener.onError(error);
        }
    }
}
