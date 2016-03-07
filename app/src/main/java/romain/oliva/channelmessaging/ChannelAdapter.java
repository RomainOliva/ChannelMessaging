package romain.oliva.channelmessaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import romain.oliva.channelmessaging.gson.Channel;

/**
 * Created by RomainMac on 08/02/2016.
 */
public class ChannelAdapter extends BaseAdapter {
    private final Context context;
    ArrayList<Channel> allChannels;

    public ChannelAdapter(Context context, ArrayList<Channel> channels) {
        this.allChannels = channels;
        this.context = context;
    }

    @Override
    public int getCount() {
        return allChannels.size();
    }

    @Override
    public Channel getItem(int position) {
        return allChannels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_channel, parent, false);

        TextView nameChannel = (TextView) rowView.findViewById(R.id.nameChannel);
        TextView nbUser = (TextView) rowView.findViewById(R.id.nbUser);

        rowView.setTag(allChannels.get(position));

        nameChannel.setText(allChannels.get(position).name);
        nbUser.setText("Nombre d'utilisateurs connectés : " + allChannels.get(position).connectedusers);

        return rowView;
    }
}
