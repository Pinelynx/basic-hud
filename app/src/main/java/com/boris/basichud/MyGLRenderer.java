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

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private Triangle mTriangle;
    private Triangle incLarge;
    private Triangle incSmall;
    private Triangle xLarge;
    private Triangle xSmall;
    private Triangle yLarge;
    private Triangle ySmall;
    private Square mSquare;
    private Square leftLine;
    private Square rightLine;
    private Square botLine;
    private Square incLine1;
    private Square incLine2;
    private Square incLine3;
    private Square incLine4;
    private Square incLine5;
    private Square xLine1;
    private Square xLine2;
    private Square xLine3;
    private Square xLine4;
    private Square xLine5;
    private Square yLine1;
    private Square yLine2;
    private Square yLine3;
    private Square yLine4;
    private Square yLine5;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private float mAngle;
    private float hAngle;
    private float incShift;
    private float xShift;
    private float yShift;
    private boolean portrait = true;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        mTriangle = new Triangle();
        mSquare   = new Square();

        incLarge = new Triangle(0.5f, 0.05f, 0.5f, -0.05f, 0.55f, 0f, 0f, 1f, 0f, 1f);
        incSmall = new Triangle(0.505f, 0.04f, 0.505f, -0.04f, 0.54f, 0f, 0f, 0f, 0f, 0f);

        xLarge = new Triangle( -0.05f, -0.5f, 0.05f, -0.5f, 0.0f, -0.55f, 1f, 0f, 0f, 1f);
        xSmall = new Triangle( -0.04f, -0.505f, 0.04f, -0.505f, 0.0f, -0.54f, 0f, 0f, 0f, 0f);

        yLarge = new Triangle(-0.55f, 0f, -0.5f, -0.05f, -0.5f, 0.05f, 1f, 0f, 0f, 1f);
        ySmall = new Triangle(-0.54f, 0f, -0.505f, -0.04f, -0.505f, 0.04f, 0f, 0f, 0f, 0f);

        leftLine = new Square (-0.5f, 0.005f, -0.5f, -0.005f, -0.1f, -0.005f, -0.1f, 0.005f, 0f, 1f, 0f);
        rightLine = new Square (0.5f, 0.005f, 0.5f, -0.005f, 0.1f, -0.005f, 0.1f, 0.005f, 0f, 1f, 0f);
        botLine = new Square (-0.005f, -0.1f, -0.005f, -0.3f, 0.005f, -0.3f, 0.005f, -0.1f, 0f, 1f, 0f);

        incLine1 = new Square (0.5f, 0.205f, 0.5f, 0.195f, 1.0f, 0.195f, 1.0f, 0.205f, 0f, 1f, 0f);
        incLine2 = new Square (0.55f, 0.105f, 0.55f, 0.095f, 1.0f, 0.095f, 1.0f, 0.105f, 0f, 1f, 0f);
        incLine3 = new Square (0.5f, 0.005f, 0.5f, -0.005f, 1.0f, -0.005f, 1.0f, 0.005f, 0f, 1f, 0f);
        incLine4 = new Square (0.55f, -0.095f, 0.55f, -0.105f, 1.0f, -0.105f, 1.0f, -0.095f, 0f, 1f, 0f);
        incLine5 = new Square (0.5f, -0.195f, 0.5f, -0.205f, 1.0f, -0.205f, 1.0f, -0.195f, 0f, 1f, 0f);

        xLine1 = new Square (-0.205f, -0.5f, -0.205f, -1.0f, -0.195f, -1.0f, -0.195f, -0.5f, 1f, 0f, 0f);
        xLine2 = new Square (-0.105f, -0.55f, -0.105f, -1.0f, -0.095f, -1.0f, -0.095f, -0.55f, 1f, 0f, 0f);
        xLine3 = new Square (-0.005f, -0.5f, -0.005f, -1.0f, 0.005f, -1.0f, 0.005f, -0.5f, 1f, 0f, 0f);
        xLine4 = new Square (0.095f, -0.55f, 0.095f, -1.0f, 0.105f, -1.0f, 0.105f, -0.55f, 1f, 0f, 0f);
        xLine5 = new Square (0.195f, -0.5f, 0.195f, -1.0f, 0.205f, -1.0f, 0.205f, -0.5f, 1f, 0f, 0f);

        yLine1 = new Square (-1.0f, 0.205f, -1.0f, 0.195f, -0.5f, 0.195f, -0.5f, 0.205f,  1f, 0f, 0f);
        yLine2 = new Square (-1.0f, 0.105f, -1.0f, 0.095f, -0.55f, 0.095f, -0.55f, 0.105f, 1f, 0f, 0f);
        yLine3 = new Square (-1.0f, 0.005f, -1.0f, -0.005f, -0.5f, -0.005f, -0.5f, 0.005f, 1f, 0f, 0f);
        yLine4 = new Square (-1.0f, -0.095f, -1.0f, -0.105f, -0.55f, -0.105f, -0.55f, -0.095f, 1f, 0f, 0f);
        yLine5 = new Square (-1.0f, -0.195f, -1.0f, -0.205f, -0.5f, -0.205f, -0.5f, -0.195f, 1f, 0f, 0f);

    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw square
        Matrix.setRotateM(mRotationMatrix, 0, hAngle, 0, 0, 1.0f);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);


        if (portrait){
            incLine1.setCoords(0.5f, 0.205f, 0.5f, 0.195f, 1.0f, 0.195f, 1.0f, 0.205f, 0f, 1f, 0f);
            incLine2.setCoords(0.55f, 0.105f, 0.55f, 0.095f, 1.0f, 0.095f, 1.0f, 0.105f, 0f, 1f, 0f);
            incLine3.setCoords(0.5f, 0.005f, 0.5f, -0.005f, 1.0f, -0.005f, 1.0f, 0.005f, 0f, 1f, 0f);
            incLine4.setCoords(0.55f, -0.095f, 0.55f, -0.105f, 1.0f, -0.105f, 1.0f, -0.095f, 0f, 1f, 0f);
            incLine5.setCoords(0.5f, -0.195f, 0.5f, -0.205f, 1.0f, -0.205f, 1.0f, -0.195f, 0f, 1f, 0f);

            xLine1.setCoords(-0.205f, -0.9f, -0.205f, -1.0f, -0.195f, -1.0f, -0.195f, -0.9f, 1f, 0f, 0f);
            xLine2.setCoords(-0.105f, -0.95f, -0.105f, -1.0f, -0.095f, -1.0f, -0.095f, -0.95f, 1f, 0f, 0f);
            xLine3.setCoords(-0.005f, -0.9f, -0.005f, -1.0f, 0.005f, -1.0f, 0.005f, -0.9f, 1f, 0f, 0f);
            xLine4.setCoords(0.095f, -0.95f, 0.095f, -1.0f, 0.105f, -1.0f, 0.105f, -0.95f, 1f, 0f, 0f);
            xLine5.setCoords(0.195f, -0.9f, 0.195f, -1.0f, 0.205f, -1.0f, 0.205f, -0.9f, 1f, 0f, 0f);

            yLine1.setCoords(-1.0f, 0.205f, -1.0f, 0.195f, -0.5f, 0.195f, -0.5f, 0.205f,  1f, 0f, 0f);
            yLine2.setCoords(-1.0f, 0.105f, -1.0f, 0.095f, -0.55f, 0.095f, -0.55f, 0.105f, 1f, 0f, 0f);
            yLine3.setCoords(-1.0f, 0.005f, -1.0f, -0.005f, -0.5f, -0.005f, -0.5f, 0.005f, 1f, 0f, 0f);
            yLine4.setCoords(-1.0f, -0.095f, -1.0f, -0.105f, -0.55f, -0.105f, -0.55f, -0.095f, 1f, 0f, 0f);
            yLine5.setCoords(-1.0f, -0.195f, -1.0f, -0.205f, -0.5f, -0.205f, -0.5f, -0.195f, 1f, 0f, 0f);

            incLarge.setOffset(0f, incShift);
            incSmall.setOffset(0f, incShift);

            xLarge.setOffset(xShift, -0.4f);
            xSmall.setOffset(xShift, -0.4f);

            yLarge.setOffset(0f, yShift);
            ySmall.setOffset(0f, yShift);


        }else{
            incLine1.setCoords(0.9f, 0.205f, 0.9f, 0.195f, 1.0f, 0.195f, 1.0f, 0.205f, 0f, 1f, 0f);
            incLine2.setCoords(0.95f, 0.105f, 0.95f, 0.095f, 1.0f, 0.095f, 1.0f, 0.105f, 0f, 1f, 0f);
            incLine3.setCoords(0.9f, 0.005f, 0.9f, -0.005f, 1.0f, -0.005f, 1.0f, 0.005f, 0f, 1f, 0f);
            incLine4.setCoords(0.95f, -0.095f, 0.95f, -0.105f, 1.0f, -0.105f, 1.0f, -0.095f, 0f, 1f, 0f);
            incLine5.setCoords(0.9f, -0.195f, 0.9f, -0.205f, 1.0f, -0.205f, 1.0f, -0.195f, 0f, 1f, 0f);

            xLine1.setCoords(-0.205f, -0.5f, -0.205f, -1.0f, -0.195f, -1.0f, -0.195f, -0.5f, 1f, 0f, 0f);
            xLine2.setCoords(-0.105f, -0.55f, -0.105f, -1.0f, -0.095f, -1.0f, -0.095f, -0.55f, 1f, 0f, 0f);
            xLine3.setCoords(-0.005f, -0.5f, -0.005f, -1.0f, 0.005f, -1.0f, 0.005f, -0.5f, 1f, 0f, 0f);
            xLine4.setCoords(0.095f, -0.55f, 0.095f, -1.0f, 0.105f, -1.0f, 0.105f, -0.55f, 1f, 0f, 0f);
            xLine5.setCoords(0.195f, -0.5f, 0.195f, -1.0f, 0.205f, -1.0f, 0.205f, -0.5f, 1f, 0f, 0f);

            yLine1.setCoords(-1.0f, 0.205f, -1.0f, 0.195f, -0.9f, 0.195f, -0.9f, 0.205f,  1f, 0f, 0f);
            yLine2.setCoords(-1.0f, 0.105f, -1.0f, 0.095f, -0.95f, 0.095f, -0.95f, 0.105f, 1f, 0f, 0f);
            yLine3.setCoords(-1.0f, 0.005f, -1.0f, -0.005f, -0.9f, -0.005f, -0.9f, 0.005f, 1f, 0f, 0f);
            yLine4.setCoords(-1.0f, -0.095f, -1.0f, -0.105f, -0.95f, -0.105f, -0.95f, -0.095f, 1f, 0f, 0f);
            yLine5.setCoords(-1.0f, -0.195f, -1.0f, -0.205f, -0.9f, -0.205f, -0.9f, -0.195f, 1f, 0f, 0f);

            incLarge.setOffset(0.4f, incShift);
            incSmall.setOffset(0.4f, incShift);

            xLarge.setOffset(xShift, 0f);
            xSmall.setOffset(xShift, 0f);

            yLarge.setOffset(-0.4f, yShift);
            ySmall.setOffset(-0.4f, yShift);
        }
        incLarge.draw(scratch);
        incSmall.draw(scratch);
        xLarge.draw(scratch);
        xSmall.draw(scratch);
        yLarge.draw(scratch);
        ySmall.draw(scratch);
        incLine1.draw(scratch);
        incLine2.draw(scratch);
        incLine3.draw(scratch);
        incLine4.draw(scratch);
        incLine5.draw(scratch);
        xLine1.draw(scratch);
        xLine2.draw(scratch);
        xLine3.draw(scratch);
        xLine4.draw(scratch);
        xLine5.draw(scratch);
        yLine1.draw(scratch);
        yLine2.draw(scratch);
        yLine3.draw(scratch);
        yLine4.draw(scratch);
        yLine5.draw(scratch);
        //mSquare.draw(mMVPMatrix);

        // Create a rotation for the triangle

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);

        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Draw triangle
        //mTriangle.draw(scratch);
        leftLine.draw(scratch);
        rightLine.draw(scratch);
        botLine.draw(scratch);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
    * Utility method for debugging OpenGL calls. Provide the name of the call
    * just after making it:
    *
    * <pre>
    * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
    *
    * If the operation is not successful, the check throws an error.
    *
    * @param glOperation - Name of the OpenGL call to check.
    */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }

    public void sethAngle(float angle) {hAngle = angle; }

    public void setOrientation (boolean orientation) {portrait = orientation;}

    public void setIncShift (float inclination) {incShift = inclination;}

    public void setXShift (float xAcc) {xShift = xAcc;}

    public void setYShift (float yAcc) {yShift = yAcc;}

}