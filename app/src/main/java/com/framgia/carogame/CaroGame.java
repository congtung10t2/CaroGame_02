package com.framgia.carogame;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.framgia.carogame.databinding.CaroActivityBinding;

public class CaroGame extends AppCompatActivity implements OnNextTurn, OnResult {
    public ProgressBar thinkingBar;
    private GameView gameView;
    private PlayerInfo enemy;
    private PlayerInfo player;
    private CaroActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.caro_activity);
        initGame();
        binding.setEnemy(enemy);
        binding.setPlayer(player);
        gameView = (GameView) findViewById(R.id.game_view);
        gameView.invalidate();
        gameView.setGameContext(this);
        thinkingBar = (ProgressBar) findViewById(R.id.thinking_bar);
    }

    public void initGame() {
        enemy = new PlayerInfo(getResources().getString(R.string.enemy_name), 0);
        player = new PlayerInfo(getResources().getString(R.string.player_name), 0);
    }

    public void onPlayerTurn() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) thinkingBar.getLayoutParams();
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.thinking_bar_horizontal_margin);
    }

    public void onEnemyTurn() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) thinkingBar.getLayoutParams();
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.thinking_bar_horizontal_margin);
    }

    public void onWin() {
        player.increaseScoreBy(GameDef.SCORE_PER_GAME);
    }

    public void onLost() {
        enemy.increaseScoreBy(GameDef.SCORE_PER_GAME);
    }

    public void hideProgressBar() {
        thinkingBar.setVisibility(View.GONE);
    }

    public void showProgressBar() {
        thinkingBar.setVisibility(View.VISIBLE);
    }
}
