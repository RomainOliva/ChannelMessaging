package romain.oliva.channelmessaging.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import romain.oliva.channelmessaging.MessageAdapter;
import romain.oliva.channelmessaging.R;
import romain.oliva.channelmessaging.gson.GetMessagesResponse;
import romain.oliva.channelmessaging.gson.Message;
import romain.oliva.channelmessaging.gson.SendMessage;
import romain.oliva.channelmessaging.network.NetworkResultProvider;
import romain.oliva.channelmessaging.network.onWsRequestListener;

public class MessageFragment  extends Fragment implements View.OnClickListener, onWsRequestListener {

    private static final int MESSAGE_REQUEST = 2;
    private ListView messageList;
    private Button btn_send;
    private EditText sendMessage;
    private boolean isActive;

    public int channel_ID =0;

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        isActive = true;

        View v = inflater.inflate(R.layout.activity_fragment_channel,container);
        messageList = (ListView)v.findViewById(R.id.messageList);


        messageList = (ListView)v.findViewById(R.id.messageList);
        sendMessage = (EditText)v.findViewById(R.id.sendMessage);
        btn_send = (Button)v.findViewById(R.id.btn_send);

        btn_send.setOnClickListener(this);


        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                refreshMessages();
                if(isActive)
                    handler.postDelayed(this, 5000);


            }
        };
        handler.post(r);
        return v;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        isActive = false;
    }

    public void updateChannel(int channel_ID) {
        this.channel_ID = channel_ID;

        refreshMessages();
    }

    private void refreshMessages() {

        if(getContext() != null)
        {
            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
            String theAccessToken = settings.getString("accesstoken", null);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("accesstoken", theAccessToken);
            params.put("channelid", String.valueOf(channel_ID));

            NetworkResultProvider np = new NetworkResultProvider(MESSAGE_REQUEST, "getmessages", params);
            np.setOnNewWsRequestListener(this);
            np.execute();
        }
    }

    private void sendMessages() {

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        String theAccessToken = settings.getString("accesstoken",null);

        HashMap<String, String> params = new HashMap<String,String>();
        params.put("accesstoken", theAccessToken);
        params.put("channelid", String.valueOf(channel_ID));
        params.put("message", sendMessage.getText().toString());
        NetworkResultProvider np = new NetworkResultProvider(1, "sendmessage", params);

        np.setOnNewWsRequestListener(this);
        np.execute();

    }

    @Override
    public void onError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleted(int requestCode, String response) {

        if (requestCode == MESSAGE_REQUEST) {
            try {
                Gson gson = new Gson();

                GetMessagesResponse myGetMessagesResponse = gson.fromJson(response, GetMessagesResponse.class);

                ArrayList<Message> allMessages = new ArrayList<>();

                for (Message oneMessage : myGetMessagesResponse.getMessages()) {
                    allMessages.add(0, oneMessage);
                }

                MessageAdapter adapter = new MessageAdapter(getActivity(), allMessages);
                messageList.setAdapter(adapter);
            } catch (Exception e) {
                Log.w("JsonException", e.toString());
            }
        }
        if (requestCode != MESSAGE_REQUEST) {
            Gson gson = new Gson();

            SendMessage mySendMessagesResponse = gson.fromJson(response, SendMessage.class);

            if (mySendMessagesResponse.code == 200) {
                Toast.makeText(getActivity(), mySendMessagesResponse.response, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onClick(View v) {
        sendMessages();
    }

    }
