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

import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import br.com.santhyago.demo.circletoformanimation.R;
import br.com.santhyago.demo.circletoformanimation.component.FloatLabelLayout;

public class MainActivity extends Activity {
    protected static final int  ANIMATION_FRAME_RATE = 25;

    View                        backgroundFilter;
    LinearLayout                searchBox;
    FloatLabelLayout            address;
    FloatLabelLayout            postalCode;
    ImageView                   plusSign;
    ImageView                   searchButton;
    
    Handler                     animationHandler;
    RelativeLayout.LayoutParams searchBoxParams;
    RelativeLayout              mWindowManager;
    Timer                       mSearchBoxTimer;
    SearchBoxAnimTimerTask      mSearchBoxAnimTimer;
    DisplayMetrics              mDisplayMetrics;
    
    int                         searchBoxWidth;
    int                         searchBoxHeight;
    
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
        postalCode       = (FloatLabelLayout) findViewById(R.id.postal_code);
        plusSign         = (ImageView)        findViewById(R.id.plus_sign);
        searchButton     = (ImageView)        findViewById(R.id.search_button);
        searchBoxParams  = (android.widget.RelativeLayout.LayoutParams) searchBox.getLayoutParams();
        mWindowManager   = (RelativeLayout)   findViewById(R.id.main_relative);
        
        animationHandler = new Handler();
        mDisplayMetrics  = getResources().getDisplayMetrics();
        searchBoxWidth   = mDisplayMetrics.widthPixels -  ((35 + 55)  * 3);
        searchBoxHeight  = mDisplayMetrics.heightPixels - ((35 + 484) * 3);
        
        addListeners();
    }

    private void addListeners() {
        plusSign.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchModeAnimationStart();
            }
        });
    }

    protected void searchModeAnimationStart() {
        backgroundFilter.setVisibility(View.VISIBLE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(backgroundFilter, "alpha", 0f, 0.3f);
        alpha.setDuration(300);
        alpha.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            
            @Override
            public void onAnimationRepeat(Animator animation) {}
            
            @Override
            public void onAnimationEnd(Animator animation) {
                exitPlusSignAnimation();
            }
            
            @Override
            public void onAnimationCancel(Animator animation) {}
        });
        alpha.start();
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
                enterSearchBox();
            }
            
            @Override
            public void onAnimationCancel(Animator animation) {}
        });
        scale.start();
    }

    protected void enterSearchBox() {
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

    private class SearchBoxAnimTimerTask extends TimerTask {
        float searchBoxW;
        float searchBoxH;
        public SearchBoxAnimTimerTask() {
            super();
            searchBoxW = searchBoxWidth;
            searchBoxH = searchBoxHeight;
            searchBoxParams.height = 10;
            searchBoxParams.width  = 10;
            mWindowManager.updateViewLayout(searchBox, searchBoxParams);
            searchBox.setVisibility(View.VISIBLE);
        }

        @Override
        public void run() {
            animationHandler.post(new Runnable() {
                @Override
                public void run() {
                    searchBoxParams.width = (int) ((2 * (searchBoxParams.width - searchBoxW)) / 3 + searchBoxW);
                    mWindowManager.updateViewLayout(searchBox, searchBoxParams);
                    
                    if (Math.abs(searchBoxParams.width - searchBoxW) <= 2) {
                        searchBoxParams.height = (int) ((2 * (searchBoxParams.height - searchBoxH)) / 3 + searchBoxH);
                        mWindowManager.updateViewLayout(searchBox, searchBoxParams);
                        
                        if (Math.abs(searchBoxParams.height - searchBoxH) <= 2) {
                            SearchBoxAnimTimerTask.this.cancel();
                            mSearchBoxTimer.cancel();
                            enterSearchButton();
                        }
                    }
                }
            });
        }
    }
}