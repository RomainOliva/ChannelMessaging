package romain.oliva.channelmessaging.gson;

/**
 * Created by RomainMac on 08/02/2016.
 */
public class Channel {
    public int channelID;
    public String name;
    public int connectedusers;

    public void Channel() {
    }

    public Channel(int channelID, String name, int connectedusers)
    {
        this.channelID = channelID;
        this.name = name;
        this.connectedusers = connectedusers;
    }
}
