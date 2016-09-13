package com.farfly;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class MiButton extends Button {

    public MiButton(Context context) {
        super(context);
    }

    public MiButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public MiButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }
}
