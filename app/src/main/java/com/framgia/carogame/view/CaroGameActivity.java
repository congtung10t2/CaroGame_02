package com.framgia.carogame.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.framgia.carogame.R;
import com.framgia.carogame.databinding.CaroActivityBinding;
import com.framgia.carogame.libs.GameHelper;
import com.framgia.carogame.libs.ProgressDialogUtils;
import com.framgia.carogame.libs.ToastUtils;
import com.framgia.carogame.libs.UserStorage;
import com.framgia.carogame.model.constants.GameDef;
import com.framgia.carogame.model.enums.Commands;
import com.framgia.carogame.viewmodel.PlayerInfoViewModel;
import com.framgia.carogame.viewmodel.games.OnNextTurn;
import com.framgia.carogame.viewmodel.games.OnResult;
import com.framgia.carogame.viewmodel.services.BluetoothConnection;

public class CaroGameActivity extends AppCompatActivity implements OnNextTurn, OnResult {
    public ProgressBar thinkingBar;
    private GameView gameView;
    private PlayerInfoViewModel players;
    private CaroActivityBinding binding;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BluetoothConnection.getInstance().stopAllConnect();
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
        String shortPlayerName = GameHelper.getShortName(BluetoothConnection.getInstance()
            .getBluetoothAdapter().getName());
        String shortEnemyName= GameHelper.getShortName(BluetoothConnection.getInstance()
            .getEnemyDeviceName());
        players.getPlayerInfo().setDisplayName(shortPlayerName);
        players.getEnemyInfo().setDisplayName(shortEnemyName);
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.leave_title)
            .setMessage(R.string.ays_msg)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    BluetoothConnection.getInstance().writes(Commands.LEAVE.name());
                    saveCurrentProcess();
                    BluetoothConnection.getInstance().stopAllConnect();
                    finish();
                }
            }).setNegativeButton(android.R.string.no, null).show();
    }

    public void onLost() {
        players.getEnemyInfo().increaseScoreBy(GameDef.SCORE_PER_GAME);
        binding.invalidateAll();
    }

    public void onShareFacebook(View view){
        SharePhotoContent content = GameHelper.shareImage(getWindow().getDecorView());
        ShareDialog shareDialog = new ShareDialog(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                ToastUtils.showToast(R.string.share_success, CaroGameActivity.this);
            }

            @Override
            public void onCancel() {
                ToastUtils.showToast(R.string.share_cancelled, CaroGameActivity.this);
            }

            @Override
            public void onError(FacebookException exception) {
                ToastUtils.showToast(R.string.share_error, CaroGameActivity.this);
            }
        });
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            shareDialog.show(content);
        } else {
            ToastUtils.showToast(R.string.cannot_share, CaroGameActivity.this);
        }
    }

    public void hideProgressBar() {
        thinkingBar.setVisibility(View.GONE);
    }

    public void showProgressBar() {
        thinkingBar.setVisibility(View.VISIBLE);
    }

    public void showProgressDialog(){
        ProgressDialog pd =  ProgressDialogUtils
            .show(this, R.string.reconnecting_title, R.string.please_wait);
        BluetoothConnection.getInstance().setProgressDialog(pd);
    }
}
