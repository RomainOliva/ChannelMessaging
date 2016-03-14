package romain.oliva.channelmessaging;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import romain.oliva.channelmessaging.gson.Message;
import romain.oliva.channelmessaging.images.ImageResultProvider;
import romain.oliva.channelmessaging.images.OnDownloadImage;


public class MessageAdapter extends BaseAdapter {
    private final Context context;
    private int requestCode = 4;

    ArrayList<Message> allMessages;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.allMessages = messages;
        this.context = context;
        Collections.reverse(allMessages);

    }

    @Override
    public int getCount() {
        return allMessages.size();
    }

    @Override
    public Message getItem(int position) {
        return allMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 50;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String filename = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + allMessages.get(position).username + ".png";

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        if (allMessages.get(position).messageImageUrl.equals("")) {
            rowView = inflater.inflate(R.layout.custom_list_message, parent, false);
        } else {
            rowView = inflater.inflate(R.layout.custom_list_message_image, parent, false);
        }

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        TextView userName = (TextView) rowView.findViewById(R.id.userName);
        TextView msgText = (TextView) rowView.findViewById(R.id.msgText);
        TextView hourText = (TextView) rowView.findViewById(R.id.hourText);
        final ImageView userPhoto = (ImageView) rowView.findViewById(R.id.userPhoto);


        File file = new File(filename);
        if (!file.exists()) {
            ImageResultProvider np = new ImageResultProvider(requestCode, allMessages.get(position).imageUrl, filename);
            np.setOnNewImageRequestListener(new OnDownloadImage() {
                @Override
                public void onError(String error) {

                }

                @Override
                public void onCompleted(int requestCode, Bitmap response) {
                    if (response != null) {
                        Bitmap finalImage = getRoundedCornerBitmap(response);
                        userPhoto.setImageBitmap(finalImage);
                    }
                }
            });

            np.execute();
        } else {
            Bitmap pathName = BitmapFactory.decodeFile(filename);

            if (pathName != null) {
                Bitmap finalImage = getRoundedCornerBitmap(pathName);

                userPhoto.setImageBitmap(finalImage);
            }
        }

        rowView.setTag(allMessages.get(position));

        if (!allMessages.get(position).messageImageUrl.equals("")) {
            Picasso.with(context).load(allMessages.get(position).messageImageUrl).into(imageView);
        }

        userName.setText(allMessages.get(position).username + " : ");

        hourText.setText(allMessages.get(position).date);

        if (allMessages.get(position).messageImageUrl.equals("")) {
            msgText.setText(allMessages.get(position).message);
        }

        return rowView;
    }
}
