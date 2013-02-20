package com.example.thamatrix;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.Surface;
import android.view.Choreographer.FrameCallback;

public class DreamatrixRenderer implements FrameCallback {
	
    static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    static final int EGL_OPENGL_ES2_BIT = 4;

	private EGL10 mEgl;
	private EGLDisplay mEglDisplay;
	private EGLConfig mEglConfig;
	private EGLContext mEglContext;
	private EGLSurface mEglSurface;
	private GL mGL;
	
	private boolean mRun = true;
	private SurfaceTexture mSurface = null;
	private Resources mResources = null;
	private int drawCount = 0;
    
    private String TAG = "SurfaceListenerRunnable";
    
    public DreamatrixRenderer(SurfaceTexture surface, int width, int height){
    	synchronized (this) {
        	this.mRun = true;
		}
    }
    
    private static final String sSimpleVS =
        "attribute vec4 position;\n" +
        "attribute vec2 texCoords;\n" +
        "varying vec2 outTexCoords;\n" +
        "\nvoid main(void) {\n" +
        "    outTexCoords = texCoords;\n" +
        "    gl_Position = position;\n" +
        "}\n\n";
	private static final String sSimpleFS =
        "precision mediump float;\n\n" +
        "varying vec2 outTexCoords;\n" +
        "uniform sampler2D texture;\n" +
        "\nvoid main(void) {\n" +
        "    gl_FragColor = texture2D(texture, outTexCoords);\n" +
        "}\n\n";

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private final float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
             1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -1.0f,  1.0f, 0.0f, 0.0f, 1.0f,
             1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
    };
	
	public void run() {
		synchronized (this) {
		
			while(this.mResources == null || this.mSurface == null || !this.mRun) {
				Log.d(TAG, "waiting for mSurfaceReady and mRun");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Log.d(TAG, "Was waiting for surface to be ready! Interrupted we exit!" );
					break;
				}
			}
			
			Log.d(TAG, "surface ready, go GL!");
			
			runGL();
		}
    }
	
	
	public void setSurfaceResources(Resources resources, SurfaceTexture surface){
			this.mResources = resources;
			this.mSurface = surface;
	}
	
	public void stopDrawing(){
		this.mRun = false;
	}
	
	private void runGL(){
		initGL();
        
        FloatBuffer triangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        triangleVertices.put(mTriangleVerticesData).position(0);

        int texture = loadTexture(R.drawable.matrix);
        int program = buildProgram(sSimpleVS, sSimpleFS);

        int attribPosition = glGetAttribLocation(program, "position");
        checkGlError();

        int attribTexCoords = glGetAttribLocation(program, "texCoords");
        checkGlError();

        int uniformTexture = glGetUniformLocation(program, "texture");
        checkGlError();

        glBindTexture(GL_TEXTURE_2D, texture);
        checkGlError();

        glUseProgram(program);
        checkGlError();

        glEnableVertexAttribArray(attribPosition);
        checkGlError();

        glEnableVertexAttribArray(attribTexCoords);
        checkGlError();

        glUniform1i(uniformTexture, texture);
        checkGlError();
        
        while (this.mRun) {
            checkCurrent();

            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            checkGlError();

            glClear(GL_COLOR_BUFFER_BIT);
            checkGlError();

            // drawQuad
            triangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
            glVertexAttribPointer(attribPosition, 3, GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);

            triangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
            glVertexAttribPointer(attribTexCoords, 3, GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);

            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

            if (!mEgl.eglSwapBuffers(mEglDisplay, mEglSurface)) {
                throw new RuntimeException("Cannot swap buffers");
            }
            checkEglError();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // Ignore
            }
        }

        finishGL();
	}
	
	private int loadTexture(int resource) {
        int[] textures = new int[1];

        glActiveTexture(GL_TEXTURE0);
        glGenTextures(1, textures, 0);
        checkGlError();

        int texture = textures[0];
        glBindTexture(GL_TEXTURE_2D, texture);
        checkGlError();
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        Bitmap bitmap = BitmapFactory.decodeResource(mResources, resource);

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap, GL_UNSIGNED_BYTE, 0);
        checkGlError();

        bitmap.recycle();

        return texture;
    }
    
    private int buildProgram(String vertex, String fragment) {
        int vertexShader = buildShader(vertex, GL_VERTEX_SHADER);
        if (vertexShader == 0) return 0;

        int fragmentShader = buildShader(fragment, GL_FRAGMENT_SHADER);
        if (fragmentShader == 0) return 0;

        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        checkGlError();

        glAttachShader(program, fragmentShader);
        checkGlError();

        glLinkProgram(program);
        checkGlError();

        int[] status = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, status, 0);
        if (status[0] != GL_TRUE) {
            String error = glGetProgramInfoLog(program);
            Log.d(TAG, "Error while linking program:\n" + error);
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            glDeleteProgram(program);
            return 0;
        }

        return program;
    }
    
    private int buildShader(String source, int type) {
        int shader = glCreateShader(type);

        glShaderSource(shader, source);
        checkGlError();

        glCompileShader(shader);
        checkGlError();

        int[] status = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, status, 0);
        if (status[0] != GL_TRUE) {
            String error = glGetShaderInfoLog(shader);
            Log.d(TAG, "Error while compiling shader:\n" + error);
            glDeleteShader(shader);
            return 0;
        }
        
        return shader;
    }

    private void checkEglError() {
        int error = mEgl.eglGetError();
        if (error != EGL10.EGL_SUCCESS) {
            Log.w(TAG, "EGL error = 0x" + Integer.toHexString(error));
        }
    }

    private void checkGlError() {
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            Log.w(TAG, "GL error = 0x" + Integer.toHexString(error));
        }
    }

    private void finishGL() {
        mEgl.eglDestroyContext(mEglDisplay, mEglContext);
        mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
    }

    private void checkCurrent() {
        if (!mEglContext.equals(mEgl.eglGetCurrentContext()) ||
                !mEglSurface.equals(mEgl.eglGetCurrentSurface(EGL10.EGL_DRAW))) {
            if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
                throw new RuntimeException("eglMakeCurrent failed "
                        + GLUtils.getEGLErrorString(mEgl.eglGetError()));
            }
        }
    }
	
	private void initGL(){
		mEgl = (EGL10) EGLContext.getEGL();

        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed "
                    + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
        
        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed " +
                    GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        mEglConfig = chooseEglConfig();
        if (mEglConfig == null) {
            throw new RuntimeException("eglConfig not initialized");
        }
        
        mEglContext = createContext(mEgl, mEglDisplay, mEglConfig);

        mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, mEglConfig, mSurface, null);

        if (mEglSurface == null || mEglSurface == EGL10.EGL_NO_SURFACE) {
            int error = mEgl.eglGetError();
            if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                Log.e(TAG, "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                return;
            }
            throw new RuntimeException("createWindowSurface failed "
                    + GLUtils.getEGLErrorString(error));
        }

        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw new RuntimeException("eglMakeCurrent failed "
                    + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        mGL = mEglContext.getGL();	
	}
	
	EGLContext createContext(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig) {
        int[] attrib_list = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };
        return egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);            
    }

    private EGLConfig chooseEglConfig() {
        int[] configsCount = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        int[] configSpec = getConfig();
        if (!mEgl.eglChooseConfig(mEglDisplay, configSpec, configs, 1, configsCount)) {
            throw new IllegalArgumentException("eglChooseConfig failed " +
                    GLUtils.getEGLErrorString(mEgl.eglGetError()));
        } else if (configsCount[0] > 0) {
            return configs[0];
        }
        return null;
    }
    
    private int[] getConfig() {
        return new int[] {
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_NONE
        };
    }

	@Override
	public void doFrame(long arg0) {
		// TODO Auto-generated method stub

	}


	public void stop() {
		// TODO Auto-generated method stub
		
	}


	public void start() {
		// TODO Auto-generated method stub
		
	}

	public void setSize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

}
