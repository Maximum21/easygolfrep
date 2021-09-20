package com.minhhop.easygolf.widgets;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.minhhop.easygolf.utils.svg.SVG;
import com.minhhop.easygolf.utils.svg.SVGParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PicassoImageSVGGetter implements Html.ImageGetter {

    private Resources resources;


    private TextView textView;

    public PicassoImageSVGGetter(final TextView textView, final Resources resources) {
        this.textView = textView;
        this.resources = resources;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Drawable getDrawable(final String source) {
        final BitmapDrawablePlaceHolder result = new BitmapDrawablePlaceHolder();

        new AsyncTask<Void, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(final Void... meh) {
                try {
                    final URL url = new URL(source);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    SVG svg = SVGParser. getSVGFromInputStream(inputStream);
                    return svg.createPictureDrawable();

                } catch (Exception e) {
                    Log.e("WOW","PicassoImageSVGGetter: " + e.getLocalizedMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final Drawable drawable) {
                try {

                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

                    result.setDrawable(drawable);
                    result.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

                    textView.setText(textView.getText());
                } catch (Exception e) {
                    /* nom nom nom*/
                }
            }

        }.execute((Void) null);

        return result;
    }

    static class BitmapDrawablePlaceHolder extends BitmapDrawable {

        protected Drawable drawable;



        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }
    }
}
