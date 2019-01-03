/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.boris.basichud;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    private ActionBar bar;
    private View decorView;

    public MyGLSurfaceView(Context context) {
        super(context);
        Activity current = (Activity) context;
        bar = current.getActionBar();
        decorView = current.getWindow().getDecorView();
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                mRenderer.setAngle(
                        mRenderer.getAngle() +
                        ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
                requestRender();
                break;
            case MotionEvent.ACTION_DOWN:
                if (bar.isShowing()){
                    bar.hide();
                    int uiOptions = (View.SYSTEM_UI_FLAG_FULLSCREEN + View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    decorView.setSystemUiVisibility(uiOptions);
                }else{
                    int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                    decorView.setSystemUiVisibility(uiOptions);
                    bar.show();
                }

        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

    public void setHUDInfo(float angle, float HUDangle, boolean portrait, float incshift, float xshift, float yshift){
        mRenderer.setAngle(angle);
        mRenderer.sethAngle(HUDangle);
        mRenderer.setOrientation(portrait);
        mRenderer.setIncShift(incshift);
        mRenderer.setXShift(xshift);
        mRenderer.setYShift(yshift);
        requestRender();
    }

}
