package romain.oliva.channelmessaging;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;


import romain.oliva.channelmessaging.fragments.MessageFragment;
import romain.oliva.channelmessaging.gson.Channel;
import romain.oliva.channelmessaging.network.onWsRequestListener;

public class ChannelListActivity extends AppCompatActivity implements onWsRequestListener, AdapterView.OnItemClickListener, View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channellist);
    }


    @Override
    public void onError(String error) {

    }

    @Override
    public void onCompleted(int requestCode, String response) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Channel oneChannel = (Channel)view.getTag();

        MessageFragment messageFragment = (MessageFragment)getSupportFragmentManager().findFragmentById(R.id.MessageFragment_ID);
        if(messageFragment == null|| !messageFragment.isInLayout()){
            Intent i = new Intent(getApplicationContext(),ChannelActivity.class);
            i.putExtra("channelID",oneChannel.channelID);
            startActivity(i);
        } else {
            messageFragment.updateChannel(oneChannel.channelID);
        }

    }

    @Override
    public void onClick(View v) {

    }
}
