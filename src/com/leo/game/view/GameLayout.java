package com.leo.game.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.leo.game.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class GameLayout extends RelativeLayout {
	public static final int DEFAULT_COLUMNS = 4;
	public static final int DEFAULT_INTERVAL = 16;
	public static final int FLING_MIN_DISTANCE = 50;

	private GameScoreChangeListeren mGameScoreChangeListeren;
	
	private GameBlock[] mGameBlocks;

	private int mPadding;
	private int mColumns;
	private int mInterval;
	private boolean isFirstLayout = true;
	private GestureDetector mGestureDetector;

	private boolean isMergeHappen = true;
	private boolean isMoveHappen = true;

	enum Orientation {
		LEFT, RIGHT, TOP, BOTTOM
	}

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
		mGestureDetector = new GestureDetector(context,
				new GameGestureListeren());

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int minWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());

		int blockWidth = (minWidth - mPadding - mPadding - mInterval
				* (mColumns - 1))
				/ mColumns;

		if (isFirstLayout) {
			for (int i = 0; i < mGameBlocks.length; i++) {
				GameBlock block = new GameBlock(getContext());
				block.setId(i + 1);
				mGameBlocks[i] = block;
				RelativeLayout.LayoutParams params = new LayoutParams(
						blockWidth, blockWidth);

				if ((i % mColumns) != (mColumns - 1)) {
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

			generateItem();
		}

		isFirstLayout = false;

		setMeasuredDimension(minWidth, minWidth);
	}

	private boolean isFull() {
		for (int i = 0; i < mGameBlocks.length; i++) {
			if (mGameBlocks[i].getValue() == 0) {
				return false;
			}
		}
		return true;
	}

	private boolean isGameOver() {
		if (!isFull()) {
			return false;
		}
		for (int i = 0; i < mGameBlocks.length; i++) {
			GameBlock item = mGameBlocks[i];
			int index = 0;
			if (i % mColumns != 0) {
				index = i - 1;
				if (item.getValue() == mGameBlocks[index].getValue()) {
					return false;
				}
			}

			if ((i % mColumns != mColumns - 1)) {
				index = i + 1;
				if ((item.getValue() == mGameBlocks[index].getValue())) {
					return false;
				}
			}

			index = i - mColumns;
			if (index >= 0) {
				if ((item.getValue() == mGameBlocks[index].getValue())) {
					return false;
				}
			}

			index = i + mColumns;
			if (index < mGameBlocks.length) {
				if ((item.getValue() == mGameBlocks[index].getValue())) {
					return false;
				}
			}
		}
		return true;
	}

	private int getPositionByType(Orientation orientation, int rowX, int rowY) {
		int position = 0;
		switch (orientation) {
		case LEFT:
			position = rowX * mColumns - rowY + mColumns - 1;
			break;
		case RIGHT:
			position = rowX * mColumns + rowY;
			break;
		case TOP:
			position = rowX + (mColumns - 1) * mColumns - rowY * mColumns;
			break;
		case BOTTOM:
			position = rowX + rowY * mColumns;
			break;
		default:
			break;
		}
		return position;
	}

	public void generateItem() {
		if (isGameOver()) {
			return;
		}

		if (!isFull()) {
			Log.e("liyu", "generateNum:" + isMoveHappen + ":" + isMergeHappen);
			if (isMoveHappen || isMergeHappen) {
				Random random = new Random();
				int next = random.nextInt(mColumns * mColumns);
				GameBlock item = mGameBlocks[next];

				while (item.getValue() != 0) {
					next = random.nextInt(16);
					item = mGameBlocks[next];
				}

				item.setValue(Math.random() > 0.75 ? 4 : 2);

				isMergeHappen = false;
				isMoveHappen = false;
			}
		}
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

	private void flipTo(Orientation orientation) {
		Log.e("liyu", "flipTo:" + orientation);
		for (int i = 0; i < mColumns; i++) {
			GameBlock[] itmes = new GameBlock[mColumns];
			for (int j = 0; j < mColumns; j++) {
				int index = getPositionByType(orientation, i, j);
				itmes[j] = mGameBlocks[index];
			}

			mergeItem(itmes, orientation, i);

		}

		generateItem();
	}

	private void mergeItem(GameBlock[] items, Orientation orientation, int RowX) {
		List<GameBlock> rows = new ArrayList<GameBlock>();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getValue() != 0) {
				rows.add(items[i]);
			}
		}

		int size = rows.size();

		if (size != items.length) {
			isMoveHappen = true;
		}

		for (int j = 0; j < size; j++) {
			GameBlock currItem = rows.get(j);
			int currValue = currItem.getValue();
			if (j + 1 < size) {
				GameBlock nextItem = rows.get(j + 1);
				int nextValue = nextItem.getValue();
				if (currValue == nextValue) {
					currItem.setValue(0);
					int sum = currValue + nextValue;
					nextItem.setValue(sum);
					if(mGameScoreChangeListeren != null){
						mGameScoreChangeListeren.gameScoreChange(sum);
					}
					isMergeHappen = true;
					j = j + 1;
				}
			}
		}
		
		rows.clear();
		
		for (int i = 0; i < items.length; i++) {
			if (items[i].getValue() != 0) {
				rows.add(items[i]);
			}
		}
		
		size = rows.size();
		
		int[] values = new int[items.length];
		int index = items.length - size;
		
		for (int i = 0; i < values.length; i++) {
			if(i < index){
				values[i] = 0;
			}else{
				values[i] = rows.get(0).getValue();
				rows.remove(0);
			}
		}
		
		for (int i = 0; i < values.length; i++) {
			items[i].setValue(values[i]);
		}
		

	}

	protected void printfPosition(Orientation orientation) {
		for (int i = 0; i < mColumns; i++) {
			for (int j = 0; j < mColumns; j++) {
				Log.e("liyu",
						orientation + ":"
								+ getPositionByType(orientation, i, j));
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return true;
	}

	public void setGameScoreLister(GameScoreChangeListeren gameScoreChangeListeren){
		mGameScoreChangeListeren = gameScoreChangeListeren;
	}
	
	
	class GameGestureListeren extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float x = e2.getX() - e1.getX();
			float y = e2.getY() - e1.getY();

			if (x > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > Math.abs(velocityY)) {
				flipTo(Orientation.RIGHT);
			} else if (x < -FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > Math.abs(velocityY)) {
				flipTo(Orientation.LEFT);

			} else if (y > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) < Math.abs(velocityY)) {
				flipTo(Orientation.BOTTOM);

			} else if (y < -FLING_MIN_DISTANCE
					&& Math.abs(velocityX) < Math.abs(velocityY)) {
				flipTo(Orientation.TOP);
			}
			return true;
		}

	}
}
