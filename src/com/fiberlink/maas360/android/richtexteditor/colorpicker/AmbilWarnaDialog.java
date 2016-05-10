package com.fiberlink.maas360.android.richtexteditor.colorpicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fiberlink.maas360.android.richtexteditor.R;

public class AmbilWarnaDialog
{
    public enum ColorType
    {
        TEXT,
        BACKGROUND
    }

    public interface OnAmbilWarnaListener
    {
        void onCancel(AmbilWarnaDialog dialog);

        void onOk(AmbilWarnaDialog dialog, int color);
    }

    final AlertDialog dialog;
    final OnAmbilWarnaListener listener;
    final ColorType type;
    final View viewHue;
    final AmbilWarnaSquare viewSatVal;
    final ImageView viewCursor;
    final TextView viewNewColorText;
    final ImageView viewTarget;
    final ImageView viewColorBlack;
    final ImageView viewColorGray;
    final ImageView viewColorLtGray;
    final ImageView viewColorWhite;
    final ImageView viewColorRed;
    final ImageView viewColorYellow;
    final ImageView viewColorBlue;
    final ImageView viewColorGreen;

    final ViewGroup viewContainer;
    final float[] currentColorHsv = new float[3];
    int currentDefaultColor = -1;
    int alpha;

    /**
     * Create an AmbilWarnaDialog.
     * 
     * @param context
     *            activity context
     * @param color
     *            current color
     * @param listener
     *            Listener for color change
     * @param type
     *            Type: Text color or text background color
     * @param textPreviewString
     *            String used to show preview of formatting
     */
    public AmbilWarnaDialog(final Context context, int color, OnAmbilWarnaListener listener, ColorType type,
            String textPreviewString)
    {
        this.listener = listener;
        this.type = type;
        color = color | 0xff000000;

        Color.colorToHSV(color, currentColorHsv);
        alpha = Color.alpha(color);

        final View view = LayoutInflater.from(context).inflate(R.layout.ambilwarna_dialog, null);
        viewHue = view.findViewById(R.id.ambilwarna_viewHue);
        viewSatVal = (AmbilWarnaSquare) view.findViewById(R.id.ambilwarna_viewSatBri);
        viewCursor = (ImageView) view.findViewById(R.id.ambilwarna_cursor);
        viewNewColorText = (TextView) view.findViewById(R.id.ambilwarna_newColor_text);
        viewTarget = (ImageView) view.findViewById(R.id.ambilwarna_target);
        viewContainer = (ViewGroup) view.findViewById(R.id.ambilwarna_viewContainer);
        viewColorBlack = (ImageView) view.findViewById(R.id.color_black);
        viewColorGray = (ImageView) view.findViewById(R.id.color_gray);
        viewColorLtGray = (ImageView) view.findViewById(R.id.color_lt_gray);
        viewColorWhite = (ImageView) view.findViewById(R.id.color_white);
        viewColorRed = (ImageView) view.findViewById(R.id.color_red);
        viewColorYellow = (ImageView) view.findViewById(R.id.color_yellow);
        viewColorBlue = (ImageView) view.findViewById(R.id.color_blue);
        viewColorGreen = (ImageView) view.findViewById(R.id.color_green);

        viewSatVal.setHue(getHue());
        setSelectedColor(color);

        if (!TextUtils.isEmpty(textPreviewString)) {
            viewNewColorText.setText(textPreviewString);
        }

        viewColorBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                currentDefaultColor = Color.BLACK;
                setSelectedColor(Color.BLACK);
            }
        });
        viewColorGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                currentDefaultColor = Color.GRAY;
                setSelectedColor(Color.GRAY);
            }
        });
        viewColorLtGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                currentDefaultColor = Color.LTGRAY;
                setSelectedColor(Color.LTGRAY);
            }
        });
        viewColorWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                currentDefaultColor = Color.WHITE;
                setSelectedColor(Color.WHITE);
            }
        });
        viewColorRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                currentDefaultColor = Color.RED;
                setSelectedColor(Color.RED);
            }
        });
        viewColorYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                currentDefaultColor = Color.YELLOW;
                setSelectedColor(Color.YELLOW);
            }
        });
        viewColorBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                currentDefaultColor = Color.BLUE;
                setSelectedColor(Color.BLUE);
            }
        });
        viewColorGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                currentDefaultColor = Color.GREEN;
                setSelectedColor(Color.GREEN);
            }
        });

        viewHue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    currentDefaultColor = -1;

                    float y = event.getY();
                    if (y < 0.f) {
                        y = 0.f;
                    }
                    if (y > viewHue.getMeasuredHeight()) {
                        y = viewHue.getMeasuredHeight() - 0.001f; // to avoid jumping the cursor from bottom to top.
                    }
                    float hue = 360.f - 360.f / viewHue.getMeasuredHeight() * y;
                    if (hue == 360.f) {
                        hue = 0.f;
                    }
                    setHue(hue);

                    // update view
                    viewSatVal.setHue(getHue());
                    moveCursor();
                    setSelectedColor(getColor());
                    return true;
                }
                return false;
            }
        });

        viewSatVal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    currentDefaultColor = -1;

                    float x = event.getX(); // touch event are in dp units.
                    float y = event.getY();

                    if (x < 0.f) {
                        x = 0.f;
                    }
                    if (x > viewSatVal.getMeasuredWidth()) {
                        x = viewSatVal.getMeasuredWidth();
                    }
                    if (y < 0.f) {
                        y = 0.f;
                    }
                    if (y > viewSatVal.getMeasuredHeight()) {
                        y = viewSatVal.getMeasuredHeight();
                    }

                    setSat(1.f / viewSatVal.getMeasuredWidth() * x);
                    setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));

                    // update view
                    moveTarget();
                    setSelectedColor(getColor());

                    return true;
                }
                return false;
            }
        });

        dialog = new AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (AmbilWarnaDialog.this.listener != null) {
                            AmbilWarnaDialog.this.listener.onOk(AmbilWarnaDialog.this, getSelectedColor());
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (AmbilWarnaDialog.this.listener != null) {
                            AmbilWarnaDialog.this.listener.onCancel(AmbilWarnaDialog.this);
                        }
                    }
                }).setOnCancelListener(new OnCancelListener() {
                    // if back button is used, call back our listener.
                    @Override
                    public void onCancel(DialogInterface paramDialogInterface)
                    {
                        if (AmbilWarnaDialog.this.listener != null) {
                            AmbilWarnaDialog.this.listener.onCancel(AmbilWarnaDialog.this);
                        }

                    }
                }).create();
        // kill all padding from the dialog window
        dialog.setView(view, 0, 0, 0, 0);

        // move cursor & target on first draw
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout()
            {
                moveCursor();
                moveTarget();
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void setSelectedColor(int color)
    {
        if (type == ColorType.TEXT) {
            viewNewColorText.setTextColor(color);
        }
        else {
            viewNewColorText.setBackgroundColor(color);
        }
    }

    protected void moveCursor()
    {
        float y = viewHue.getMeasuredHeight() - (getHue() * viewHue.getMeasuredHeight() / 360.f);
        if (y == viewHue.getMeasuredHeight()) {
            y = 0.f;
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (viewHue.getLeft() - Math.floor(viewCursor.getMeasuredWidth() / 2) - viewContainer
                .getPaddingLeft());
        layoutParams.topMargin = (int) (viewHue.getTop() + y - Math.floor(viewCursor.getMeasuredHeight() / 2) - viewContainer
                .getPaddingTop());
        viewCursor.setLayoutParams(layoutParams);
    }

    protected void moveTarget()
    {
        float x = getSat() * viewSatVal.getMeasuredWidth();
        float y = (1.f - getVal()) * viewSatVal.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
        layoutParams.leftMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - viewContainer
                .getPaddingLeft());
        layoutParams.topMargin = (int) (viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - viewContainer
                .getPaddingTop());
        viewTarget.setLayoutParams(layoutParams);
    }

    private int getSelectedColor()
    {
        if (currentDefaultColor != -1) {
            return currentDefaultColor;
        }
        return getColor();
    }

    private int getColor()
    {
        final int argb = Color.HSVToColor(currentColorHsv);
        return alpha << 24 | (argb & 0x00ffffff);
    }

    private float getHue()
    {
        return currentColorHsv[0];
    }

    private float getSat()
    {
        return currentColorHsv[1];
    }

    private float getVal()
    {
        return currentColorHsv[2];
    }

    private void setHue(float hue)
    {
        currentColorHsv[0] = hue;
    }

    private void setSat(float sat)
    {
        currentColorHsv[1] = sat;
    }

    private void setVal(float val)
    {
        currentColorHsv[2] = val;
    }

    public void show()
    {
        dialog.show();
    }

    public AlertDialog getDialog()
    {
        return dialog;
    }
}
