package romain.oliva.channelmessaging;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import romain.oliva.channelmessaging.fragments.MessageFragment;
import romain.oliva.channelmessaging.gps.GPSActivity;


public class ChannelActivity extends GPSActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        int channel_ID = (int) getIntent().getIntExtra("channelID", 0);
        MessageFragment messageFragment =(MessageFragment)getSupportFragmentManager().findFragmentById(R.id.MessageFragment_ID);

        messageFragment.updateChannel(channel_ID);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){

            Intent i = new Intent();
            i.putExtra("channelID",channel_ID);

            setResult(RESULT_OK, i);
            finish();
        }
    }

}
