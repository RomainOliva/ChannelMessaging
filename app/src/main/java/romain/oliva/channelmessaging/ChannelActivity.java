package romain.oliva.channelmessaging;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import romain.oliva.channelmessaging.fragments.MessageFragment;
import romain.oliva.channelmessaging.gson.GetMessagesResponse;
import romain.oliva.channelmessaging.gson.Message;
import romain.oliva.channelmessaging.gson.SendMessage;
import romain.oliva.channelmessaging.network.NetworkResultProvider;
import romain.oliva.channelmessaging.network.onWsRequestListener;


public class ChannelActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        int channel_ID = (int) getIntent().getIntExtra("channelID", 0);
        MessageFragment messageFragment =(MessageFragment)getSupportFragmentManager().findFragmentById(R.id.MessageFragment_ID);

        messageFragment.updateChannel(channel_ID);
    }

}
