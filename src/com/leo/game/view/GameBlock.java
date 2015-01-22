package com.leo.game.view;

import com.leo.game.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.View;

public class GameBlock extends View {
	private int mValue;
	private Paint mPaint;
	private Rect mRect;
	private String mText;

	private int mBackgoundColor;
	private int mTextColor;

	public GameBlock(Context context) {
		super(context);
		mPaint = new Paint();
		mRect = new Rect();
		
		Resources res = getContext().getResources();
		mTextColor = res.getColor(R.color.text_color);
		mBackgoundColor = res.getColor(R.color.color_number_0);
	}

	public void setValue(int value) {
		mValue = value;
		Resources res = getContext().getResources();

		switch (value) {
			case 0:
				mBackgoundColor = res.getColor(R.color.color_number_0);
				break;
			case 2:
				mBackgoundColor = res.getColor(R.color.color_number_2);
				break;
			case 4:
				mBackgoundColor = res.getColor(R.color.color_number_4);
				break;
			case 8:
				mBackgoundColor = res.getColor(R.color.color_number_8);
				break;
			case 16:
				mBackgoundColor = res.getColor(R.color.color_number_16);
				break;
			case 32:
				mBackgoundColor = res.getColor(R.color.color_number_32);
				break;
			case 64:
				mBackgoundColor = res.getColor(R.color.color_number_64);
				break;
			case 128:
				mBackgoundColor = res.getColor(R.color.color_number_128);
				break;
			case 256:
				mBackgoundColor = res.getColor(R.color.color_number_256);
				break;
			case 512:
				mBackgoundColor = res.getColor(R.color.color_number_512);
				break;
			case 1024:
				mBackgoundColor = res.getColor(R.color.color_number_1024);
				break;
			case 2048:
				mBackgoundColor = res.getColor(R.color.color_number_2048);
				break;
		}
		
		mText = String.valueOf(mValue);
		mPaint.setAntiAlias(true);
		mPaint.getTextBounds(mText, 0, mText.length(), mRect);
		
		invalidate();
	}

	public int getValue() {
		return mValue;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawText(canvas);
	}

	private void drawText(Canvas canvas) {

		mPaint.setColor(mBackgoundColor);
		mPaint.setStyle(Style.FILL);
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

		if (mText != null) {
			mPaint.setColor(mTextColor);
			float x = (getWidth() - mRect.width()) / 2.0f;
			float y = (getHeight() + mRect.height()) / 2.0f;
			canvas.drawText(mText, x, y, mPaint);
		}
	}

}
