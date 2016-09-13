package com.farfly;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MiTextView extends TextView {
    Context context;

    public MiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MiTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"fonts/Animated.ttf");
        setTypeface(tf);
    }


}
