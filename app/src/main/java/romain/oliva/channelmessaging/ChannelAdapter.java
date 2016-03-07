package romain.oliva.channelmessaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import romain.oliva.channelmessaging.gson.Channel;


public class ChannelAdapter extends BaseAdapter {
    private final Context context;
    ArrayList<Channel> allChannels;
    ArrayList<Channel> channelFiltered;

    public ChannelAdapter(Context context, ArrayList<Channel> channels) {
        this.allChannels = channels;
        this.channelFiltered = (ArrayList<Channel>) channels.clone();
        this.context = context;
    }


    public void filter(String filtre){
        channelFiltered.clear();

        for (Channel oneChannel : allChannels) {
            if(oneChannel.name.toLowerCase().contains(filtre.toLowerCase()))
            {
                channelFiltered.add(oneChannel);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return channelFiltered.size();
    }

    @Override
    public Channel getItem(int position) {
        return channelFiltered.get(position);
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

        rowView.setTag(channelFiltered.get(position));

        nameChannel.setText(channelFiltered.get(position).name);
        nbUser.setText("Nombre d'utilisateurs connectés : " + channelFiltered.get(position).connectedusers);

        return rowView;
    }
}
