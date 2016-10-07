package com.framgia.carogame.view;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.framgia.carogame.R;
import com.framgia.carogame.databinding.CaroActivityBinding;
import com.framgia.carogame.libs.UserStorage;
import com.framgia.carogame.model.constants.GameDef;
import com.framgia.carogame.model.enums.Commands;
import com.framgia.carogame.viewmodel.PlayerInfoViewModel;
import com.framgia.carogame.viewmodel.games.OnNextTurn;
import com.framgia.carogame.viewmodel.games.OnResult;
import com.framgia.carogame.viewmodel.services.BluetoothConnection;

public class CaroGame extends AppCompatActivity implements OnNextTurn, OnResult {
    public ProgressBar thinkingBar;
    private GameView gameView;
    private PlayerInfoViewModel players;
    private CaroActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    public void saveCurrentProcess() {
        UserStorage.getInstance().save(players.getPlayerInfo().getScore(),
            players.getEnemyInfo().getScore());
    }

    public void initView() {
        BluetoothConnection.getInstance().setGameContext(this);
        binding = DataBindingUtil.setContentView(this, R.layout.caro_activity);
        initGame();
        binding.setPlayers(players);
        gameView = (GameView) findViewById(R.id.game_view);
        gameView.invalidate();
        gameView.setGameContext(this);
        BluetoothConnection.getInstance().setGameCallback(gameView);
        thinkingBar = (ProgressBar) findViewById(R.id.thinking_bar);
        if (BluetoothConnection.getInstance().isServer()) gameView.playerTurn();
        else gameView.enemyTurn();
    }

    public void initGame() {
        players = new PlayerInfoViewModel(this);
    }

    public void onPlayerTurn() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) thinkingBar.getLayoutParams();
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.leftMargin =
            getResources().getDimensionPixelSize(R.dimen.thinking_bar_horizontal_margin);
        binding.getRoot().requestLayout();
    }

    public void onEnemyTurn() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) thinkingBar.getLayoutParams();
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.rightMargin =
            getResources().getDimensionPixelSize(R.dimen.thinking_bar_horizontal_margin);
        binding.getRoot().requestLayout();
    }

    public void onWin() {
        players.getPlayerInfo().increaseScoreBy(GameDef.SCORE_PER_GAME);
        binding.invalidateAll();
    }

    public void surrender(View view) {
        new AlertDialog.Builder(this)
            .setTitle(R.string.surrender_title)
            .setMessage(R.string.ays_msg)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    BluetoothConnection.getInstance().writes(Commands.SURRENDER.name());
                    gameView.enemyWin();
                    gameView.playerTurn();
                }
            })
            .setNegativeButton(android.R.string.no, null).show();
    }

    public void leave(View view) {
        new AlertDialog.Builder(this)
            .setTitle(R.string.leave_title)
            .setMessage(R.string.ays_msg)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    BluetoothConnection.getInstance().writes(Commands.LEAVE.name());
                    saveCurrentProcess();
                    CaroGame.this.finish();
                }
            }).setNegativeButton(android.R.string.no, null).show();
    }

    public void onLost() {
        players.getEnemyInfo().increaseScoreBy(GameDef.SCORE_PER_GAME);
        binding.invalidateAll();
    }

    public void hideProgressBar() {
        thinkingBar.setVisibility(View.GONE);
    }

    public void showProgressBar() {
        thinkingBar.setVisibility(View.VISIBLE);
    }
}
