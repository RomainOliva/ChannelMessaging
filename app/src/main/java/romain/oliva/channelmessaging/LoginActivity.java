package romain.oliva.channelmessaging;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
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
import romain.oliva.channelmessaging.notification.NotificationActivity;


public class LoginActivity extends NotificationActivity implements View.OnClickListener, onWsRequestListener {

    //TODO
    //2. "Resolved" Pas de coordonnées GPS quand envoie photos
    //3. "Resolved" Si on clique sur photo et on passe en landscape bug
    //4. "Not time :p" Faire envoie de son
    //5. "In Progress" Tp ergo
    //6. "Resolved"   vibreur marche pas

    private EditText txt_id;
    private EditText txt_mdp;
    private Button btn_validate;

    private static final int CONNECT_REQUEST = 0;
    public static final String PREFS_NAME = "MyPrefsFile";

    private static final int REQUEST_MESSAGES = 0;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_id = (EditText) findViewById(R.id.txt_id);
        txt_mdp = (EditText) findViewById(R.id.txt_mdp);
        btn_validate = (Button) findViewById(R.id.btn_validate);

        btn_validate.setOnClickListener(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String theAccessToken = settings.getString("accesstoken", null);


        if(theAccessToken != null)
        {

            mDialog = new ProgressDialog(this);
            mDialog.setMessage("Récupération du token...");
            mDialog.setCancelable(false);
            mDialog.setIndeterminate(true);

            mDialog.show();

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
        params.put("registrationid", getRegistrationId());

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


        if(mDialog != null && mDialog.isShowing())
        {
            mDialog.hide();
        }

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
                    String fromNotify = (String) getIntent().getAction();

                    if(fromNotify.equals("fromNotify"))
                    {
                        int GoToChannel = Integer.valueOf((String) getIntent().getStringExtra("GoToChannel"));

                        Intent I_News = new Intent(getApplicationContext(), ChannelListActivity.class);
                        I_News.setAction("fromNotify");
                        I_News.putExtra("GoToChannel", GoToChannel);
                        startActivityForResult(I_News, REQUEST_MESSAGES);
                    }
                    else{
                        Intent I_News = new Intent(this,ChannelListActivity.class);
                        startActivity(I_News);
                    }
                }
            }
            catch (Exception e) {
                Log.w("JsonException", e.toString());
            }
        }
    }
}
