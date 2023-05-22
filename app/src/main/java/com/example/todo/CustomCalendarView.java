package com.example.todo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CalendarView;
import androidx.appcompat.widget.AppCompatImageButton;
import java.util.Calendar;

public class CustomCalendarView extends CalendarView {

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDateCellDrawable(int drawableResId, long timeInMillis) {
        int date = getDateFromTime(timeInMillis);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view instanceof AppCompatImageButton) {
                AppCompatImageButton imageButton = (AppCompatImageButton) view;
                if (imageButton.getContentDescription() != null && imageButton.getContentDescription().toString().equals(String.valueOf(date))) {
                    imageButton.setImageResource(drawableResId);
                    return;
                }
            }

        }
    }


    private int getDateFromTime(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        return calendar.get(Calendar.DAY_OF_MONTH);
    }
}
