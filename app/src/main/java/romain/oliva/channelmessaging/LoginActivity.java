package romain.oliva.channelmessaging;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;

import romain.oliva.channelmessaging.gson.ConnexionResponse;
import romain.oliva.channelmessaging.gson.GetAccessTokenResponse;
import romain.oliva.channelmessaging.network.NetworkResultProvider;
import romain.oliva.channelmessaging.network.onWsRequestListener;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, onWsRequestListener {

    private EditText txt_id;
    private EditText txt_mdp;
    private TextView lbl_id;
    private TextView lbl_mdp;
    private Button btn_validate;

    private static final int CONNECT_REQUEST = 0;
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_id = (EditText) findViewById(R.id.txt_id);
        txt_mdp = (EditText) findViewById(R.id.txt_mdp);
        lbl_id = (TextView) findViewById(R.id.lbl_id);
        lbl_mdp = (TextView) findViewById(R.id.lbl_mdp);
        btn_validate = (Button) findViewById(R.id.btn_validate);

        btn_validate.setOnClickListener(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String theAccessToken = settings.getString("accesstoken", null);

        if(theAccessToken != null)
        {
            //List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            //params.add(new BasicNameValuePair("username", txt_id.getText().toString()));
            //params.add(new BasicNameValuePair("password", txt_mdp.getText().toString()));
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("accesstoken", theAccessToken);

            NetworkResultProvider np = new NetworkResultProvider(1, "isaccesstokenvalid", params);
            np.setOnNewWsRequestListener(this);
            np.execute();
        }
    }


    @Override
    public void onClick(View v) {
        //List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        //params.add(new BasicNameValuePair("username", txt_id.getText().toString()));
        //params.add(new BasicNameValuePair("password", txt_mdp.getText().toString()));
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", txt_id.getText().toString());
        params.put("password", txt_mdp.getText().toString());

        NetworkResultProvider np = new NetworkResultProvider(CONNECT_REQUEST, "connect", params);
        np.setOnNewWsRequestListener(this);
        np.execute();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleted(int requestCode, String response) {

        if(requestCode == 0)
        {
            try{
                Gson gson = new Gson();
                ConnexionResponse myConnexionResponse = gson.fromJson(response, ConnexionResponse.class);

                if(myConnexionResponse.code == 200)
                {
                    Toast.makeText(getApplicationContext(), myConnexionResponse.response, Toast.LENGTH_SHORT).show();

                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("accesstoken", myConnexionResponse.accesstoken);

                    editor.commit();

                    Intent I_News = new Intent(this,ChannelListActivity.class);
                    startActivity(I_News);
                }

                if(myConnexionResponse.code == 500)
                {
                    Toast.makeText(getApplicationContext(), "Identifiants incorrect", Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e) {
                Log.w("JsonException", e.toString());
            }
        }
        else {
            try {
                Gson gson = new Gson();
                GetAccessTokenResponse accessTokenResponse = gson.fromJson(response, GetAccessTokenResponse.class);

                if (accessTokenResponse.code == 200) {
                    Intent I_News = new Intent(this,ChannelListActivity.class);
                    startActivity(I_News);
                }
            }
            catch (Exception e) {
                Log.w("JsonException", e.toString());
            }
        }
    }
}
