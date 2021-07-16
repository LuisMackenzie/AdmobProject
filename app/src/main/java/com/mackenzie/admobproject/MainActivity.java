package com.mackenzie.admobproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AdView banner;
    private InterstitialAd mInterstitialAd;
    private RewardedAd rewardedAd, rewardedAd2;
    private Button btn1, btn2, btn3, btn4, btn5;
    private int coinCountBan, coinCountInter, coinCountBoni;
    private TextView coinBan, coinInter, coinBoni;
    private boolean gameOver;
    private boolean gamePaused;
    private static final long GAME_LENGTH_MILLISECONDS = 3000;
    private static final String AD_UNIT_INTERS_ID = "";
    private static final String AD_UNIT_BANNER_ID = "";
    private static final String AD_UNIT_REWARDED_ID = "";
    private static final String AD_UNIT_INTERS_TEST = "ca-app-pub-3940256099942544/1033173712";
    private static final String AD_UNIT_BANNER_TEST = "ca-app-pub-3940256099942544/6300978111";
    private static final String AD_UNIT_REWARDED_TEST = "ca-app-pub-3940256099942544/5224354917";
    private static final long COUNTER_TIME = 10;
    private static final int GAME_OVER_REWARD = 1;
    private static final String TAG = "MyActivity";

    private CountDownTimer countDownTimer, countDownTimer2;
    private Button retryButton;
    private boolean gameIsInProgress, isLoading;
    private long timerMilliseconds, timeRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objetos();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // loadAd();
        loadRewardedAd();


        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
                        .build());

        // Solicitud de anuncio para mostrar el banner
        AdRequest adRequest = new AdRequest.Builder().build();
        // asociar la solicitud al banner
        banner.loadAd(adRequest);

        banner.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Este eveneto se lanza al cargar un anuncio correctamente
                Toast.makeText(MainActivity.this, "El anuncio se cargo sin errores", Toast.LENGTH_SHORT).show();
                addCoins(GAME_OVER_REWARD, 0);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Este evento se lanza cuando falla la solicitud de carga del anuncio
                Toast.makeText(MainActivity.this, "El anuncio no se cargo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Este evento se lanza cuando el anuncio acupa toda la panatalla
                Toast.makeText(MainActivity.this, "El anuncio ocupa toda la pantalla", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                // Este evento se lanza cuando se clicka en el anuncio
                Toast.makeText(MainActivity.this, "El anuncio fue cklickado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                // Este evento se lanza cuando se cierra el anuncio
                Toast.makeText(MainActivity.this, "El anuncio fue cerrado", Toast.LENGTH_SHORT).show();
            }
        });


        //Interstitial ads unit add


        // mInterstitialAd = new InterstitialAd(this);
        // mInterstitialAd.getAdUnitId();
        // mInterstitialAd.setImmersiveMode(true);
        // mInterstitialAd.load(new AdRequest.Builder().build());
        /*mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });*/

        SharedPreferences pref = getSharedPreferences("datos", Context.MODE_PRIVATE);

        if (pref == null) {
            coinCountBan = 0;
            coinCountInter = 0;
            coinCountBoni = 0;
        } else {
            coinCountBan = pref.getInt("banner", 0);
            coinCountInter = pref.getInt("inter", 0);
            coinCountBoni = pref.getInt("boni", 0);
        }

        coinBan.setText("Coins Banner: " + coinCountBan);
        coinInter.setText("Coins Intersticial: " + coinCountInter);
        coinBoni.setText("Coins Bonificados: " + coinCountBoni);

        // loadAd();
        startGame();

        // loadRewardedAd();
        startGame2();


    }

    private void objetos() {
        banner = findViewById(R.id.adView);
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.retry2_button);
        btn5 = findViewById(R.id.button5);
        coinBan = findViewById(R.id.coin_banner);
        coinInter = findViewById(R.id.coin_inter);
        coinBoni = findViewById(R.id.coin_boni);
        retryButton = findViewById(R.id.retry_button);
        retryButton.setVisibility(View.INVISIBLE);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        retryButton.setOnClickListener(this);
    }

    private void loadAd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, AD_UNIT_INTERS_TEST,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        MainActivity.this.mInterstitialAd = interstitialAd;
                        Log.i(TAG, "Intersticial Cargado Correctamente");
                        Toast.makeText(MainActivity.this, "Intersticial CARGADO Correctamente", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        MainActivity.this.mInterstitialAd = null;
                                        Log.d("TAG", "Intersticial Correctamente cerrado");
                                        Toast.makeText(MainActivity.this, "Intersticial CERRADO Correctamente", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        MainActivity.this.mInterstitialAd = null;
                                        Log.d("TAG", "Intersticial Fallo al mostrar");
                                        Toast.makeText(MainActivity.this, "Intersticial Fallo al mostrar", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "Intersticial Correctamente Cargado Modo Fullscreen");
                                        addCoins(GAME_OVER_REWARD, 1);
                                        Toast.makeText(MainActivity.this, "Intersticial CARGADO Correctamente Modo Fullscreen", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;

                        String error = String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Toast.makeText(
                                MainActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                                .show();
                    }
                });

    }

    private void loadRewardedAd() {
        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    this,
                    AD_UNIT_REWARDED_TEST,
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.getMessage());
                            rewardedAd = null;
                            MainActivity.this.isLoading = false;
                            Toast.makeText(MainActivity.this, "Fallo en la CARGA del ADS  Bonificado", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            MainActivity.this.rewardedAd = rewardedAd;
                            Log.d(TAG, "onAdLoaded");
                            MainActivity.this.isLoading = false;
                            Toast.makeText(MainActivity.this, "CARGA Correcta del Bonificado", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void createTimer(final long milliseconds) {
        // Create the game timer, which counts down to the end of the level
        // and shows the "retry" button.
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        final TextView textView = findViewById(R.id.timer);

        countDownTimer = new CountDownTimer(milliseconds, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                timerMilliseconds = millisUnitFinished;
                textView.setText("seconds remaining: " + ((millisUnitFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                gameIsInProgress = false;
                textView.setText("done!");
                retryButton.setVisibility(View.VISIBLE);
            }
        };
    }

    // Create the game timer, which counts down to the end of the level
    // and shows the "retry" button.
    private void createTimer2(long time) {
        final TextView textView = findViewById(R.id.timer2);
        if (countDownTimer2 != null) {
            countDownTimer2.cancel();
        }
        countDownTimer2 =
                new CountDownTimer(time * 1000, 50) {
                    @Override
                    public void onTick(long millisUnitFinished) {
                        timeRemaining = ((millisUnitFinished / 1000) + 1);
                        textView.setText("seconds remaining: " + timeRemaining);
                    }

                    @Override
                    public void onFinish() {
                        if (rewardedAd != null) {
                            btn4.setVisibility(View.VISIBLE);
                        }
                        textView.setText("You Lose!");
                        // addCoins(GAME_OVER_REWARD);
                        retryButton.setVisibility(View.VISIBLE);
                        gameOver = true;
                    }
                };
        countDownTimer2.start();
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            startGame();
        }
    }

    private void showRewardedVideo() {

        if (rewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }
        btn4.setVisibility(View.INVISIBLE);

        rewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "onAdShowedFullScreenContent");
                        Toast.makeText(MainActivity.this, "MUESTRA Correcta del Bonificado", Toast.LENGTH_SHORT)
                                .show();
                        addCoins(GAME_OVER_REWARD, 2);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d(TAG, "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;
                        Toast.makeText(
                                MainActivity.this, "Fallo en la MUESTRA del ADS  Bonificado", Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;
                        Log.d(TAG, "onAdDismissedFullScreenContent");
                        Toast.makeText(MainActivity.this, "CIERRE Correcto del Bonificado", Toast.LENGTH_SHORT)
                                .show();
                        // Preload the next rewarded ad.
                        MainActivity.this.loadRewardedAd();
                    }
                });
        Activity activityContext = MainActivity.this;
        rewardedAd.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("TAG", "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                    }
                });

    }

    private void startGame() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (mInterstitialAd == null) {
            loadAd();
        }
        hideButtons();

        resumeGame(GAME_LENGTH_MILLISECONDS);
    }

    private void startGame2() {
        // Hide the retry button, load the ad, and start the timer.
        hideButtons();


        if (rewardedAd != null && !isLoading) {
            loadRewardedAd();
        }
        createTimer2(COUNTER_TIME);
        gamePaused = false;
        gameOver = false;
    }

    private void resumeGame(long milliseconds) {
        // Create a new timer for the correct length and start it.
        gameIsInProgress = true;
        timerMilliseconds = milliseconds;
        createTimer(milliseconds);
        countDownTimer.start();
    }

    private void resumeGame2() {
        createTimer2(timeRemaining);
        gamePaused = false;
    }

    private void pauseGame() {
        countDownTimer.cancel();
        gamePaused = true;
    }

    private void addCoins(int coins, int type) {

        switch(type) {
            case 0:
                coinCountBan += coins;
                coinBan.setText("Coins Banner: " + coinCountBan);
                break;
            case 1:
                coinCountInter += coins;
                coinInter.setText("Coins Intersticial: " + coinCountInter);
                break;
            case 2:
                coinCountBoni += coins;
                coinBoni.setText("Coins Bonificados: " + coinCountBoni);
                break;
            default:
                Toast.makeText(this, "No se ha registrado recompensa", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void hideButtons() {
        int isVisible = retryButton.getVisibility();
        if (isVisible == View.VISIBLE) {
            retryButton.setVisibility(View.INVISIBLE);
            btn4.setVisibility(View.INVISIBLE);
        }
    }

    public void Guardar(){
        SharedPreferences pref2 = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor Obj_editor = pref2.edit();
        Obj_editor.putInt("banner", coinCountBan);
        Obj_editor.putInt("inter", coinCountInter);
        Obj_editor.putInt("boni", coinCountBoni);
        Obj_editor.commit();
        // finish();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button1:
                showInterstitial();
                // loadAd();
                /*if (mInterstitialAd != null) {
                    mInterstitialAd.show(this);
                    Toast.makeText(this, "Se cargo correctamente ?", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No se ha cargado la publi", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }*/
                break;
            case R.id.button2:
                showRewardedVideo();
                break;
            case R.id.button3:

                break;
            case R.id.button5:
                Intent in = new Intent(MainActivity.this, NativeAds.class);
                startActivity(in);
                break;
            case R.id.retry_button:
                startGame2();
                // showInterstitial();
                break;
            case R.id.retry2_button:
                showRewardedVideo();
                break;
        }
    }

    @Override
    public void onResume() {
        // Start or resume the game.
        super.onResume();
        if (banner != null) {
            banner.resume();
        }
        if (mInterstitialAd != null) {
            loadAd();
        }
        if (gameIsInProgress) {
            resumeGame(timerMilliseconds);
        }
        if (!gameOver && gamePaused) {
            resumeGame2();
        }
    }

    @Override
    public void onPause() {
        if (banner != null) {
            banner.pause();
        }
        Guardar();
        // Cancel the timer if the game is paused.
        countDownTimer.cancel();
        pauseGame();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Guardar();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (banner != null) {
            banner.destroy();
        }
        Guardar();
        super.onDestroy();
    }

}