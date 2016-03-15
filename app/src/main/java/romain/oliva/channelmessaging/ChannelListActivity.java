package romain.oliva.channelmessaging;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;


import romain.oliva.channelmessaging.fragments.ChannelListFragment;
import romain.oliva.channelmessaging.fragments.MessageFragment;
import romain.oliva.channelmessaging.gps.GPSActivity;
import romain.oliva.channelmessaging.gson.Channel;
import romain.oliva.channelmessaging.network.onWsRequestListener;

public class ChannelListActivity extends GPSActivity implements onWsRequestListener, AdapterView.OnItemClickListener, View.OnClickListener, SearchView.OnQueryTextListener {


    private static final int REQUEST_MESSAGES = 0;
    private MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channellist);

        if(getIntent().getAction() != null)
        {
            String fromNotify = getIntent().getAction();

            if(fromNotify.equals("fromNotify"))
            {
                int GoToChannel = getIntent().getIntExtra("GoToChannel", 0);

                Intent I_News = new Intent(getApplicationContext(), ChannelActivity.class);
                I_News.putExtra("channelID", GoToChannel);
                startActivityForResult(I_News, REQUEST_MESSAGES);
            }
        }

    }


    @Override
    public void onError(String error) {

    }

    @Override
    public void onCompleted(int requestCode, String response) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Channel oneChannel = (Channel) view.getTag();

        MessageFragment messageFragment = (MessageFragment) getSupportFragmentManager().findFragmentById(R.id.MessageFragment_land_ID);
        if (messageFragment == null || !messageFragment.isInLayout()) {
            Intent i = new Intent(getApplicationContext(), ChannelActivity.class);
            i.putExtra("channelID", oneChannel.channelID);
            startActivityForResult(i, REQUEST_MESSAGES);
        } else {
            messageFragment.updateChannel(oneChannel.channelID);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        MessageFragment messageFragment = (MessageFragment) getSupportFragmentManager().findFragmentById(R.id.MessageFragment_land_ID);

        if(resultCode == RESULT_OK && requestCode == REQUEST_MESSAGES)
        {
            int channel_ID = data.getIntExtra("channelID", 0);

            messageFragment.updateChannel(channel_ID);
        }
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchViewAction = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchViewAction.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onQueryTextSubmit(String s) {
        MenuItemCompat.collapseActionView(searchMenuItem);

        ChannelListFragment channelListFragment = (ChannelListFragment) getSupportFragmentManager().findFragmentById(R.id.ChannelListFragment_ID);
        channelListFragment.search(s);

        return false;
    }
    @Override
    public boolean onQueryTextChange(String s) {

        ChannelListFragment channelListFragment = (ChannelListFragment) getSupportFragmentManager().findFragmentById(R.id.ChannelListFragment_ID);
        channelListFragment.search(s);

        return false;
    }

}
