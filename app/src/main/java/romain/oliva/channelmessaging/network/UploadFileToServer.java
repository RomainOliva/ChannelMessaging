package romain.oliva.channelmessaging.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * <p>Extends of Asynctask that let you upload a file on a server</p>
 */
public class UploadFileToServer extends AsyncTask<String, Integer, String> {

    private ProgressDialog mDialog;
    private static final String URL = "http://www.raphaelbischof.fr/messaging/upload.php";
    private String mFilePath = "";
    private List<NameValuePair> mParameters;
    private File mFile;
    private OnUploadFileListener mOnUploadFileListener;
    private IOException currentException;

    /**
     * Constructor
     * @param context Context required to create a dialog that shows progress
     * @param filePath Path to the file that you want to send to the server
     * @param values Post params that you want to give to the server
     * @param onUploadFileListener Listener that provides you the differents states of the request
     */
    public UploadFileToServer(Context context, String filePath, List<NameValuePair> values,OnUploadFileListener onUploadFileListener) {
        mDialog = new ProgressDialog(context);
        mFilePath = filePath;
        mParameters = values;
        this.mOnUploadFileListener = onUploadFileListener;
    }

    @Override
    protected void onPreExecute() {
        mFile = new File(mFilePath);
        mDialog.setMessage("Please Wait..");
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection;
        DataOutputStream outputStream;
        InputStream inputStream;
        String twoHyphens = "--";
        String boundary = "*****"
                + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 512;

        String[] q = mFilePath.split("/");
        int idx = q.length - 1;
        try {

            FileInputStream fileInputStream = new FileInputStream(mFile);

            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent",
                    "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(
                    connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream
                    .writeBytes("Content-Disposition: form-data; name=dosya; filename=\""
                            + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: image/jpg" + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary"
                    + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            int boyut = 0;
            while (bytesRead > 0) {
                boyut += bytesRead;
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                publishProgress(boyut);
            }

            outputStream.writeBytes(lineEnd);
            for (NameValuePair post : mParameters) {
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream
                        .writeBytes("Content-Disposition: form-data; name=\""
                                + post.getName() + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain"
                        + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(post.getValue());
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                    + lineEnd);
            inputStream = connection.getInputStream();
            String result = convertStreamToString(inputStream);
            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            return result;
        } catch (IOException e) {
            currentException = e;
            return null;
        }

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mDialog.setProgress((progress[0]*100)/(int)mFile.length());
    }

    @Override
    protected void onPostExecute(String result) {
        mDialog.dismiss();
        if (result == null){
            if (currentException!= null){
                mOnUploadFileListener.onFailed(currentException);
            }
        }else{
            mOnUploadFileListener.onResponse(result);
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public interface OnUploadFileListener{
        public void onResponse(String result);
        public void onFailed(IOException error);
    }
}