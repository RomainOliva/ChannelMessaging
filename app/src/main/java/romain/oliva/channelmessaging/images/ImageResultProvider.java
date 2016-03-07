package romain.oliva.channelmessaging.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class ImageResultProvider extends AsyncTask<Void, Void, Void> {

    private ArrayList<OnDownloadImage> listeners = new ArrayList<OnDownloadImage>();

    private int requestCode;

    private String imageURL = "";
    private String fileName = "";

    public ImageResultProvider(int requestCode, String imageURL, String fileName) {
        this.requestCode = requestCode;
        this.imageURL = imageURL;
        this.fileName = fileName;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        try {
            URL url = new URL(imageURL);
            File file = new File(fileName);
            file.createNewFile();

            /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();

            /* Define InputStreams to read from the URLConnection.*/
            InputStream is = ucon.getInputStream();

            /* Read bytes to the Buffer until there is nothing more to read(-1) and
write on the fly in the file.*/
            FileOutputStream fos = new FileOutputStream(file);
            final int BUFFER_SIZE = 23 * 1024;

            BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
            byte[] baf = new byte[BUFFER_SIZE];
            int actual = 0;

            while (actual != -1) {
                fos.write(baf, 0, actual);
                actual = bis.read(baf, 0, BUFFER_SIZE);
            }
            fos.close();

        } catch (IOException e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void s) {
        super.onPostExecute(s);

        Bitmap pathName = BitmapFactory.decodeFile(fileName);

        newImageRequestCompleted(pathName);
    }

    public void setOnNewImageRequestListener(OnDownloadImage listener)
    {
        this.listeners.add(listener);
    }

    private void newImageRequestCompleted(Bitmap response){
        for(OnDownloadImage oneListener : listeners){
            oneListener.onCompleted(requestCode, response);
        }
    }

    private void newImageRequestError(String error){
        for(OnDownloadImage oneListener : listeners){
            oneListener.onError(error);
        }
    }
}
