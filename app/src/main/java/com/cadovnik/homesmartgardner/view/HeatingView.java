package com.cadovnik.homesmartgardner.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cadovnik.homesmartgardner.R;

import androidx.annotation.Nullable;

public class HeatingView extends LinearLayout {
    public HeatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.heating_view, this);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.HeatingView);
        ImageView image = findViewById(R.id.image);
        TextView text = findViewById(R.id.caption);
        image.setImageDrawable(attributes.getDrawable(R.styleable.HeatingView_image));
        text.setText(attributes.getString(R.styleable.HeatingView_text));
        attributes.recycle();
    }
}
