package romain.oliva.channelmessaging.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import romain.oliva.channelmessaging.ChannelAdapter;
import romain.oliva.channelmessaging.ChannelListActivity;
import romain.oliva.channelmessaging.R;
import romain.oliva.channelmessaging.gson.Channel;
import romain.oliva.channelmessaging.gson.GetChannelsResponse;
import romain.oliva.channelmessaging.network.NetworkResultProvider;
import romain.oliva.channelmessaging.network.onWsRequestListener;

public class ChannelListFragment extends Fragment implements onWsRequestListener{

    private ListView channelList;

    private static final int CHANNEL_REQUEST = 1;
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_fragment_channellist,container);
        channelList = (ListView)v.findViewById(R.id.channelList);

        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
        String theAccessToken = settings.getString("accesstoken", null);


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("accesstoken", theAccessToken);

        NetworkResultProvider np = new NetworkResultProvider(CHANNEL_REQUEST,"getchannels", params);
        np.setOnNewWsRequestListener(this);
        np.execute();

        return v;
    }

    @Override
    public void onError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleted(int requestCode, String response) {
        try{
            Gson gson = new Gson();

            GetChannelsResponse myGetChannelsResponse = gson.fromJson(response, GetChannelsResponse.class);

            ArrayList<Channel> allChannel = new ArrayList<>();

            for (Channel oneChannel: myGetChannelsResponse.getChannels()) {
                allChannel.add(oneChannel);
            }

            ChannelAdapter adapter = new ChannelAdapter(getContext(), allChannel);
            channelList.setAdapter(adapter);

            channelList.setOnItemClickListener((ChannelListActivity) getActivity());
        }

        catch (Exception e) {
            Log.w("JsonException", e.toString());
        }

    }

    public void search(String filtre){

        ((ChannelAdapter) channelList.getAdapter()).filter(filtre);

    }

}
