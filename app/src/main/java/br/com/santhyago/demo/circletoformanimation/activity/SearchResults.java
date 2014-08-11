package br.com.santhyago.demo.circletoformanimation.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import br.com.santhyago.demo.circletoformanimation.R;

public class SearchResults extends Activity {
    protected static final int  ANIMATION_FRAME_RATE = 60;
    public static final int REQUEST_CODE             = 4111980;
    public static final String KEY_SEARCH_TEXT       = "TEXT_TO_SEARCH";
    public static final String KEY_SEARCH_BUNDLE     = "BUNDLE_TO_SEARCH";

    Timer                     mTypingSimulationTimer;
    TypingSimulationTimerTask mTypingSimulationAnimTimer;

    Handler                   animationHandler;
    private String            textToSearch;

    TextView   search;
    ImageView  bkgCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results_activity);

        initComponent();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bounceSearchIcon();
            }
        }, 1200);
    }

    private void initComponent() {
        animationHandler = new Handler();
        bkgCircle = (ImageView) findViewById(R.id.circle_background);
        search = (TextView) findViewById(R.id.search_text);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(KEY_SEARCH_BUNDLE);
        textToSearch = bundle.getString(KEY_SEARCH_TEXT);
    }

    private void animCircle() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 2, 25);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 2, 25);
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(bkgCircle, scaleX, scaleY);
        scale.setDuration(700);
        scale.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) { animTyping(); }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        scale.start();
    }

    private void bounceSearchIcon() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1, 2);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1, 2);
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(bkgCircle, scaleX, scaleY);
        scale.setInterpolator(new BounceInterpolator());
        scale.setDuration(300);
        scale.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                bkgCircle.setVisibility(View.VISIBLE);
                animCircle();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        scale.start();
    }

    protected void animTyping() {
        mTypingSimulationAnimTimer = new TypingSimulationTimerTask();
        mTypingSimulationTimer = new Timer();
        mTypingSimulationTimer.schedule(mTypingSimulationAnimTimer, 0, ANIMATION_FRAME_RATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class TypingSimulationTimerTask extends TimerTask {
        String searchText;

        public TypingSimulationTimerTask() {
            super();
            searchText = textToSearch;
        }

        @Override
        public void run() {
            animationHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (searchText.length() > 0) {
                        search.setText(search.getText().toString() + searchText.substring(0, 1));
                        searchText = searchText.substring(1);
                    } else {
                        TypingSimulationTimerTask.this.cancel();
                        mTypingSimulationTimer.cancel();
                    }
                }
            });
        }
    }
}
