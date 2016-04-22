package com.makbeard.musicguide;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Класс преобразует URL в Drawable и заполняет ImageView
 *
 */
public class ShowDrawableFromUrlAsyncTask extends AsyncTask<String, Void, Drawable> {

    private final String TAG = "DrawableAsyncTask";

    private ImageView mImageView;

    public ShowDrawableFromUrlAsyncTask(ImageView imageView) {
        mImageView = imageView;
    }

    @Override
    protected Drawable doInBackground(String... params) {
        try {
            InputStream is = (InputStream) new URL(params[0]).getContent();
            return Drawable.createFromStream(is, null);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "loadImageFromUrl: MalformedURLException");
            // TODO: 22.04.2016 Обрабоать MalformedURLException
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "loadImageFromUrl: IOException");
            // TODO: 22.04.2016 Обрабоать IOException
            return null;
        }
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
        super.onPostExecute(drawable);
        mImageView.setImageDrawable(drawable);
    }
}
