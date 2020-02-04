package com.araya.arto.customview;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by el araya on 26/01/2020
 * adyranggahidayat@gmail.com
 * Copyright (c) 2020
 **/
public class EditNumber extends android.support.v7.widget.AppCompatEditText {

    /**
     * custom EditText for number input only
     * default text is 0
     */

    TextWatcher textWatcher;
    String defaultString = "0";

    public EditNumber(Context context) {
        super(context);
        init();
    }

    public EditNumber(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setInputType(InputType.TYPE_CLASS_NUMBER);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("ETN", "beforeTextChanged" + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("ETN", "onTextChanged" + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("ETN", "afterTextChanged" + s.toString());
                if (s.toString().isEmpty()) {
                    setText(defaultString);
                    Log.i("ETN", "afterTextChanged cek" + s.toString());
                }
            }
        };

        addTextChangedListener(textWatcher);
    }
}
