package br.com.santhyago.demo.circletoformanimation.activity;

/*
 * Copyright (C) 2014 Santhyago Gallao
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

import br.com.santhyago.demo.circletoformanimation.R;
import br.com.santhyago.demo.circletoformanimation.component.FloatLabelLayout;

public class MainActivity extends Activity {
    protected static final int  ANIMATION_FRAME_RATE = 25;

    View                        backgroundFilter;
    LinearLayout                searchBox;
    FloatLabelLayout            address;
    FloatLabelLayout            postalCode;
    EditText                    addressText;
    EditText                    postalCodeText;
    ImageView                   plusSign;
    ImageView                   searchButton;
    ImageView                   naoHaRegistros;
    ImageView                   casaDoJose;
    
    Handler                     animationHandler;
    RelativeLayout.LayoutParams searchBoxParams;
    RelativeLayout              mWindowManager;
    Timer                       mSearchBoxTimer;
    SearchBoxAnimTimerTask      mSearchBoxAnimTimer;
    DisplayMetrics              mDisplayMetrics;
    
    int                         searchBoxWidth;
    int                         searchBoxHeight;
    static boolean              enterSearchBox = true;

    String                      strAddressText;
    String                      strPostalCodeText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        initComponent();
    }

    private void initComponent() {
        backgroundFilter = (View)             findViewById(R.id.background_filter);
        searchBox        = (LinearLayout)     findViewById(R.id.search_box);
        address          = (FloatLabelLayout) findViewById(R.id.address);
        addressText      = (EditText)         findViewById(R.id.address_text);
        postalCode       = (FloatLabelLayout) findViewById(R.id.postal_code);
        postalCodeText   = (EditText)         findViewById(R.id.postal_code_text);
        plusSign         = (ImageView)        findViewById(R.id.plus_sign);
        searchButton     = (ImageView)        findViewById(R.id.search_button);
        searchBoxParams  = (android.widget.RelativeLayout.LayoutParams) searchBox.getLayoutParams();
        mWindowManager   = (RelativeLayout)   findViewById(R.id.main_relative);

        naoHaRegistros   = (ImageView)        findViewById(R.id.nao_ha_registros);
        casaDoJose       = (ImageView)        findViewById(R.id.casa_do_jose);

        animationHandler = new Handler();
        mDisplayMetrics  = getResources().getDisplayMetrics();
        searchBoxWidth   = mDisplayMetrics.widthPixels  - 270;
        searchBoxHeight  = mDisplayMetrics.heightPixels - 1300;

        addListeners();
    }

    private void addListeners() {
        plusSign.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                searchModeAnimation();
            }
        });
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { exitFields(); }
        });
    }

    protected void searchModeAnimation() {
        ObjectAnimator alpha;
        if (enterSearchBox) {
            alpha = ObjectAnimator.ofFloat(backgroundFilter, "alpha", 0f, 0.5f);
        } else {
            alpha = ObjectAnimator.ofFloat(backgroundFilter, "alpha", 0.5f, 0f);
        }
        alpha.setDuration(300);
        alpha.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { if (enterSearchBox) backgroundFilter.setVisibility(View.VISIBLE); }
            
            @Override
            public void onAnimationRepeat(Animator animation) {}
            
            @Override
            public void onAnimationEnd(Animator animation) {
                if (enterSearchBox) {
                    exitPlusSignAnimation();
                } else {
                    startSearchResults();
                }
            }
            
            @Override
            public void onAnimationCancel(Animator animation) {}
        });
        alpha.start();
    }

    @TargetApi(Build.VERSION_CODES.L)
    private void startSearchResults() {
        backgroundFilter.setVisibility(View.GONE);
        MainActivity.enterSearchBox = true;
        Intent intent = new Intent(this, SearchResults.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, searchButton, "search_button");
        Bundle bundle = options.toBundle();
        bundle.putString(SearchResults.KEY_SEARCH_TEXT, strAddressText);
        intent.putExtra(SearchResults.KEY_SEARCH_BUNDLE, bundle);
        startActivityForResult(intent, SearchResults.REQUEST_CODE, bundle);
    }

    private void exitPlusSignAnimation() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1, 0);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1, 0);
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(plusSign, scaleX, scaleY);
        scale.setDuration(180);
        scale.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            
            @Override
            public void onAnimationRepeat(Animator animation) {}
            
            @Override
            public void onAnimationEnd(Animator animation) {
                plusSign.setVisibility(View.GONE);
                animSearchBox();
            }
            
            @Override
            public void onAnimationCancel(Animator animation) {}
        });
        scale.start();
    }

    protected void animSearchBox() {
        mSearchBoxAnimTimer = new SearchBoxAnimTimerTask();
        mSearchBoxTimer = new Timer();
        mSearchBoxTimer.schedule(mSearchBoxAnimTimer, 0, ANIMATION_FRAME_RATE);
    }
    
    protected void enterSearchButton() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(searchButton, scaleX, scaleY);
        scale.setDuration(180);
        scale.setInterpolator(new BounceInterpolator());
        scale.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                searchButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}
            
            @Override
            public void onAnimationEnd(Animator animation) {
                enterFields();
            }
            
            @Override
            public void onAnimationCancel(Animator animation) {}
        });
        scale.start();
    }

    protected void enterFields() {
        ObjectAnimator alphaPostalCode = ObjectAnimator.ofFloat(postalCode, "alpha", 0f, 1f);
        ObjectAnimator alphaAddress = ObjectAnimator.ofFloat(address, "alpha", 0f, 1f);

        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(150);
        animation.playSequentially(alphaAddress, alphaPostalCode);
        animation.start();
    }

    protected void exitFields() {
        strAddressText = addressText.getText().toString();
        strPostalCodeText = postalCodeText.getText().toString();

        ObjectAnimator alphaPostalCode = ObjectAnimator.ofFloat(postalCode, "alpha", 1f, 0f);
        ObjectAnimator alphaAddress = ObjectAnimator.ofFloat(address, "alpha", 1f, 0f);

        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(150);
        animation.playSequentially(alphaPostalCode, alphaAddress);
        animation.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                MainActivity.enterSearchBox = false;
                animSearchBox();
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        animation.start();
    }

    protected void openSearchResults() {
        searchModeAnimation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1, 0);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1, 0);
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(searchButton, scaleX, scaleY);
        scale.setDuration(180);
        scale.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                PropertyValuesHolder scaleXp = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
                PropertyValuesHolder scaleYp = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
                ObjectAnimator scaleP = ObjectAnimator.ofPropertyValuesHolder(plusSign, scaleXp, scaleYp);
                scaleP.setDuration(180);
                scaleP.setInterpolator(new BounceInterpolator());
                scaleP.addListener(new AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        naoHaRegistros.setVisibility(View.GONE);
                        casaDoJose.setVisibility(View.VISIBLE);
                        plusSign.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        searchButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}
                });
                scaleP.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}
        });
        scale.start();
    }

    private class SearchBoxAnimTimerTask extends TimerTask {
        float searchBoxW;
        float searchBoxH;

        public SearchBoxAnimTimerTask() {
            super();
            if (MainActivity.enterSearchBox) {
                searchBoxW = searchBoxWidth;
                searchBoxH = searchBoxHeight;
                searchBoxParams.height = 10;
                searchBoxParams.width = 10;
            } else {
                searchBoxW = 10;
                searchBoxH = 10;
                searchBoxParams.height = searchBoxHeight;
                searchBoxParams.width  = searchBoxWidth;
            }
            mWindowManager.updateViewLayout(searchBox, searchBoxParams);
            searchBox.setVisibility(View.VISIBLE);
        }

        @Override
        public void run() {
            animationHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (MainActivity.enterSearchBox) {
                        searchBoxParams.width = (int) ((2 * (searchBoxParams.width - searchBoxW)) / 3 + searchBoxW);
                        mWindowManager.updateViewLayout(searchBox, searchBoxParams);

                        if (Math.abs(searchBoxParams.width - searchBoxW) <= 2) {
                            searchBoxParams.height = (int) ((2 * (searchBoxParams.height - searchBoxH)) / 3 + searchBoxH);
                            searchBoxParams.height = (int) ((searchBoxParams.height - searchBoxH) * 2 / 3 + searchBoxH);
                            mWindowManager.updateViewLayout(searchBox, searchBoxParams);

                            if (Math.abs(searchBoxParams.height - searchBoxH) <= 2) {
                                SearchBoxAnimTimerTask.this.cancel();
                                mSearchBoxTimer.cancel();
                                enterSearchButton();
                            }
                        }
                    } else {
                        searchBoxParams.height = (int) ((2 * (searchBoxParams.height - searchBoxH)) / 3 + searchBoxH);
                        mWindowManager.updateViewLayout(searchBox, searchBoxParams);

                        if (Math.abs(searchBoxParams.height - searchBoxH) <= 2) {
                            searchBoxParams.width = (int) ((2 * (searchBoxParams.width - searchBoxW)) / 3 + searchBoxW);
                            mWindowManager.updateViewLayout(searchBox, searchBoxParams);

                            if (Math.abs(searchBoxParams.width - searchBoxW) <= 2) {
                                SearchBoxAnimTimerTask.this.cancel();
                                mSearchBoxTimer.cancel();
                                openSearchResults();
                            }
                        }
                    }
                }
            });
        }
    }
}