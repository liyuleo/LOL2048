package com.leo.game.view;


import com.leo.game.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

public class GameLayout extends RelativeLayout {
	public static final int DEFAULT_COLUMNS = 4;
	public static final int DEFAULT_INTERVAL = 16;

	private GameBlock[] mGameBlocks;

	private int mPadding;
	private int mColumns;
	private int mInterval;
	private boolean isFirstLayout = true;

	public GameLayout(Context context) {
		this(context, null, 0);
	}

	public GameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.GameLayout, defStyle, 0);
		int size = a.getIndexCount();
		for (int i = 0; i < size; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.GameLayout_columns:
				mColumns = a.getInt(attr, DEFAULT_COLUMNS);
				break;
			case R.styleable.GameLayout_interval:
				mInterval = a.getDimensionPixelSize(attr, (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_SP,
								DEFAULT_INTERVAL, getResources()
										.getDisplayMetrics()));
				break;
			}
		}

		mGameBlocks = new GameBlock[mColumns * mColumns];
		mPadding = min(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
				getPaddingBottom());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int minWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());

		int blockWidth = (minWidth - mPadding - mPadding - mInterval * (mColumns - 1)) / mColumns;

		if (isFirstLayout) {
			for (int i = 0; i < mGameBlocks.length; i++) {
				GameBlock block = new GameBlock(getContext());
				block.setId(i+1);
				mGameBlocks[i] = block;
				RelativeLayout.LayoutParams params = new LayoutParams(
						blockWidth, blockWidth);

				if ( (i % mColumns) != (mColumns - 1)) {
					params.rightMargin = mInterval;
				}

				if (i % mColumns != 0) {
					params.addRule(RelativeLayout.RIGHT_OF,
							mGameBlocks[i - 1].getId());
				}

				if (i >= mColumns) {
					params.topMargin = mInterval;
					params.addRule(RelativeLayout.BELOW, mGameBlocks[i
							- mColumns].getId());
				}
				addView(block, params);
			}
		}

		isFirstLayout = false;

		setMeasuredDimension(minWidth, minWidth);

	}

	private int min(int... params) {
		int min = params[0];
		for (int param : params) {
			if (min > param) {
				min = param;
			}
		}
		return min;
	}
	
}
