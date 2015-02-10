package com.leo.game;

import com.leo.game.view.GameLayout;
import com.leo.game.view.GameScoreChangeListeren;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity implements GameScoreChangeListeren {
	private TextView mScoreTextView;
	private GameLayout mGameLayout;
	private long mScore = 0l;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mGameLayout = (GameLayout) findViewById(R.id.game_layout);
		mGameLayout.setGameScoreLister(this);
		mScoreTextView = (TextView) findViewById(R.id.game_score);
	}

	@Override
	public void gameScoreChange(long score) {
		mScore += score;
		mScoreTextView.setText(String.valueOf(mScore));
	}

}
