package com.asm.bigmart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
//import android.support.v4.content.ContextCompat;
//https://mobikul.com/adding-badge-count-on-menu-items-like-cart-notification-etc/

import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;

//import com.webkul.mobikul.R;

class BadgeDrawable extends Drawable {

    private Paint mBadgePaint;
    private Paint mBadgePaint1;
    private Paint mTextPaint,mTextAmount;
    private Rect mTxtRect = new Rect();

    private String mCount = "";
    private String mAmount = "";
    private  String rupee = "";
    private boolean mWillDraw;

    public BadgeDrawable(Context context) {
        float mTextSize = context.getResources().getDimension(R.dimen.cartItems);
        rupee =  context.getResources().getString(R.string.Rupee);

        mBadgePaint = new Paint();
        mBadgePaint.setColor(Color.RED);
        mBadgePaint.setAntiAlias(true);
        mBadgePaint.setStyle(Paint.Style.FILL);
        mBadgePaint1 = new Paint();
        mBadgePaint1.setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.darkgreenColorButton));
        mBadgePaint1.setAntiAlias(true);
        mBadgePaint1.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mTextAmount = new Paint();
        mTextAmount.setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.darkgreenColorButton));
        mTextAmount.setTypeface(Typeface.DEFAULT);
        mTextAmount.setTextSize(mTextSize);
        mTextAmount.setAntiAlias(true);
        mTextAmount.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {



        if (!mWillDraw) {
            return;
        }
        Rect bounds = getBounds();
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;

        // Position the badge in the top-right quadrant of the icon.

        /*Using Math.max rather than Math.min */

        float radius = ((Math.max(width, height) / 2)) / 2;
        float centerX = (width - radius - 1) +5+15;
        float centerY = radius -5-15;
        radius = (float) (radius * (1.8));
        if(mCount.length() <= 2){
            // Draw badge circle.
            //canvas.drawCircle(centerX, centerY, (int)(radius+7.5), mBadgePaint1);
            canvas.drawCircle(centerX, centerY, (int)(radius+5.5), mBadgePaint);
        }
        else{
            //canvas.drawCircle(centerX, centerY, (int)(radius+8.5), mBadgePaint1);
            canvas.drawCircle(centerX, centerY, (int)(radius+6.5), mBadgePaint);
//	        	canvas.drawRoundRect(radius, radius, radius, radius, 10, 10, mBadgePaint);
        }
        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length(), mTxtRect);
        float textHeight = mTxtRect.bottom - mTxtRect.top;
        float textY = centerY + (textHeight / 2f);
        if(mCount.length() > 2)
            canvas.drawText("99+", centerX, textY, mTextPaint);
        else
            canvas.drawText(mCount, centerX, textY, mTextPaint);

        //canvas.drawText(rupee + "12345.00", centerX-0, textY+75, mTextAmount);
        DecimalFormat formater = new DecimalFormat("0.0");
        canvas.drawText(rupee + formater.format( Double.parseDouble( mAmount)), centerX-30, textY+67, mTextAmount);
    }

    /*
    Sets the count (i.e notifications) to display.
     */
    public void setCount(String count, String amount) {
        mCount = count;
        mAmount = amount;

        // Only draw a badge if there are notifications.
        mWillDraw = !count.equalsIgnoreCase("0");
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        // do nothing
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // do nothing
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}