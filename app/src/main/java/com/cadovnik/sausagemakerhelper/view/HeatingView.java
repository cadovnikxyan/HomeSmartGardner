package com.cadovnik.sausagemakerhelper.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.cadovnik.sausagemakerhelper.R;

public class HeatingView extends LinearLayout {
    private ImageView image;
    private Bitmap contentBitmap;
    private int leftTop = 0;
    private int rightBottom = 0;
    public HeatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.heating_view, this);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.HeatingView);
        image = findViewById(R.id.image);
        TextView text = findViewById(R.id.caption);
        Drawable drawable = attributes.getDrawable(R.styleable.HeatingView_image);
        image.setImageDrawable(drawable);
        contentBitmap = ((BitmapDrawable)drawable).getBitmap();
        text.setText(attributes.getString(R.styleable.HeatingView_text));
        leftTop = attributes.getInteger(R.styleable.HeatingView_leftTop,0);
        rightBottom = attributes.getInteger(R.styleable.HeatingView_rightBottom,0);
        attributes.recycle();
    }

    public void addTextOnImage(String text){
        try {
            Resources resources = getContext().getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = contentBitmap;
            android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();
            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(110,110, 110));
            // text size in pixels
            paint.setTextSize((int) (24 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            int x = leftTop;
            int y = rightBottom;

            canvas.drawText(text, x * scale, y * scale, paint);
            image.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "IMAGE TEXT DRAW: " , e);
        }
    }
}
