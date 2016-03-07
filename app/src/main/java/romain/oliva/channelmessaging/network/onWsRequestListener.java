package romain.oliva.channelmessaging.network;

/**
 * Created by RomainMac on 02/02/2016.
 */
public interface onWsRequestListener {
    public void onError(String error);
    public void onCompleted(int requestCode, String response);
}
