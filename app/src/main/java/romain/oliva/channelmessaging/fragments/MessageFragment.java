package romain.oliva.channelmessaging.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import romain.oliva.channelmessaging.ChannelActivity;
import romain.oliva.channelmessaging.ChannelListActivity;
import romain.oliva.channelmessaging.MessageAdapter;
import romain.oliva.channelmessaging.R;
import romain.oliva.channelmessaging.gps.GPSActivity;
import romain.oliva.channelmessaging.gps.MapActivity;
import romain.oliva.channelmessaging.gson.Channel;
import romain.oliva.channelmessaging.gson.GetMessagesResponse;
import romain.oliva.channelmessaging.gson.Message;
import romain.oliva.channelmessaging.gson.SendMessage;
import romain.oliva.channelmessaging.network.NetworkResultProvider;
import romain.oliva.channelmessaging.network.UploadFileToServer;
import romain.oliva.channelmessaging.network.onWsRequestListener;

public class MessageFragment  extends Fragment implements View.OnClickListener, onWsRequestListener, AdapterView.OnItemClickListener, UploadFileToServer.OnUploadFileListener {

    private static final int MESSAGE_REQUEST = 2;
    private static final int PICTURE_REQUEST_CODE = 2;
    private ListView messageList;
    private Button btn_send;
    private Button btn_send_photo;
    private EditText sendMessage;
    private boolean isActive;
    private Location mCurrentLocation;

    public int channel_ID =0;

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        isActive = true;

        View v = inflater.inflate(R.layout.activity_fragment_channel,container);
        messageList = (ListView)v.findViewById(R.id.messageList);

        sendMessage = (EditText)v.findViewById(R.id.sendMessage);
        btn_send = (Button)v.findViewById(R.id.btn_send);
        btn_send_photo = (Button)v.findViewById(R.id.btn_send_photo);

        btn_send.setOnClickListener(this);
        btn_send_photo.setOnClickListener(this);

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                refreshMessages();
                if(isActive)
                    handler.postDelayed(this, 5000);


            }
        };
        handler.post(r);
        return v;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        isActive = false;
    }

    public void updateChannel(int channel_ID) {
        this.channel_ID = channel_ID;

        refreshMessages();
    }

    private void refreshMessages() {

        if(getContext() != null)
        {
            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
            String theAccessToken = settings.getString("accesstoken", null);

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("accesstoken", theAccessToken);
            params.put("channelid", String.valueOf(channel_ID));

            NetworkResultProvider np = new NetworkResultProvider(MESSAGE_REQUEST, "getmessages", params);
            np.setOnNewWsRequestListener(this);
            np.execute();
        }
    }

    public void sendMessages() {

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        String theAccessToken = settings.getString("accesstoken", null);

        mCurrentLocation = ((GPSActivity) getActivity()).getCurrentLocation();

        HashMap<String, String> params = new HashMap<String,String>();
        params.put("accesstoken", theAccessToken);
        params.put("channelid", String.valueOf(channel_ID));
        params.put("message", sendMessage.getText().toString());

        //Coordonnées GPS
        if(mCurrentLocation.getLatitude() > 0 && mCurrentLocation.getLatitude() > 0)
        {
            params.put("latitude", String.valueOf(mCurrentLocation.getLatitude()));
            params.put("longitude", String.valueOf(mCurrentLocation.getLongitude()));
        }

        NetworkResultProvider np = new NetworkResultProvider(1, "sendmessage", params);

        np.setOnNewWsRequestListener(this);
        np.execute();

    }

    @Override
    public void onError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleted(int requestCode, String response) {

        if (requestCode == MESSAGE_REQUEST) {
            try {
                Gson gson = new Gson();

                GetMessagesResponse myGetMessagesResponse = gson.fromJson(response, GetMessagesResponse.class);

                ArrayList<Message> allMessages = new ArrayList<>();

                for (Message oneMessage : myGetMessagesResponse.getMessages()) {
                    allMessages.add(0, oneMessage);
                }

                MessageAdapter adapter = new MessageAdapter(getActivity(), allMessages);
                messageList.setAdapter(adapter);
                messageList.setOnItemClickListener(this);

            } catch (Exception e) {
                Log.w("JsonException", e.toString());
            }
        }
        if (requestCode != MESSAGE_REQUEST) {
            Gson gson = new Gson();

            SendMessage mySendMessagesResponse = gson.fromJson(response, SendMessage.class);

            if (mySendMessagesResponse.code == 200) {
                Toast.makeText(getActivity(), mySendMessagesResponse.response, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Message oneMessage = (Message) view.getTag();

        String[] arr = new String[2];
        arr[0] =  "Afficher sur la carte";
        arr[1] =  "Ajouter en amis";

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)//drawable de l'icone à gauche du titre
                .setTitle("Gestion utilisateur")//Titre de l'alert dialog
                .setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//which = la position de l'item appuyé
                        if (which == 0) {
                            Intent i = new Intent(getContext(), MapActivity.class);
                            i.putExtra("username", oneMessage.username);
                            i.putExtra("latitude", String.valueOf(oneMessage.latitude));
                            i.putExtra("longitude", String.valueOf(oneMessage.longitude));

                            startActivity(i);

                        } else {
                            //Do some over stuff (2nd item touched)
                        }
                    }
                })//items de l'alert dialog
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICTURE_REQUEST_CODE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                //String imageUri = data.getStringExtra(MediaStore.EXTRA_OUTPUT);
                String imageUri = getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+ "/photo/test.jpg";
                File f = new File(imageUri);

                try {
                    resizeFile(f, getActivity());

                    SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                    String theAccessToken = settings.getString("accesstoken", null);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("accesstoken", "value"));
                    params.add(new BasicNameValuePair("channelid", "value"));

                    UploadFileToServer uploadedFile = new UploadFileToServer(getContext(), imageUri, params, this);
                    uploadedFile.execute();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_send)
        {
            sendMessages();
        }

        if(v.getId() == R.id.btn_send_photo)
        {
            File f = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+ "/photo/");
            f.mkdirs();
            String uri = getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+ "/photo/test.jpg";

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Création de l’appel à l’application appareil photo pour récupérer une image
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri); //Emplacement de l’image stockée
            startActivityForResult(intent, PICTURE_REQUEST_CODE);
        }

    }

    //decodes image and scales it to reduce memory consumption
    private void resizeFile(File f,Context context) throws IOException {
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

        //The new size we want to scale to
        final int REQUIRED_SIZE=400;

        //Find the correct scale value. It should be the power of 2.
        int scale=1;
        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
            scale*=2;

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        int i = getCameraPhotoOrientation(context, Uri.fromFile(f),f.getAbsolutePath());
        if (o.outWidth>o.outHeight)
        {
            Matrix matrix = new Matrix();
            matrix.postRotate(i); // anti-clockwise by 90 degrees
            bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);
        }
        try {
            f.delete();
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //decodes image and scales it to reduce memory consumption
    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) throws IOException {
        int rotate = 0;
        context.getContentResolver().notifyChange(imageUri, null);
        File imageFile = new File(imagePath);
        ExifInterface exif = new ExifInterface(
                imageFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        return rotate;
    }

    @Override
    public void onResponse(String result) {
        Toast.makeText(getActivity(), "Photo bien envoyé", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(IOException error) {
        Toast.makeText(getActivity(), "Erreur lors de l'envoie", Toast.LENGTH_SHORT).show();
    }
}
