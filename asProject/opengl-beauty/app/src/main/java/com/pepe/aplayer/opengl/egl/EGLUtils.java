package com.pepe.aplayer.opengl.egl;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.view.Surface;


/**
 * https://blog.csdn.net/u010302327/article/details/77450355
  */
public class EGLUtils {
    private EGLSurface eglSurface = EGL14.EGL_NO_SURFACE;
    private EGLContext eglCtx = EGL14.EGL_NO_CONTEXT;
    private EGLDisplay eglDis = EGL14.EGL_NO_DISPLAY;

    private EGLContext eglContext = EGL14.EGL_NO_CONTEXT;

    public void initEGL(EGLContext eglContext, Surface surface) {
        this.eglContext = eglContext;
        eglDis = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        int[] version = new int[2];
        EGL14.eglInitialize(eglDis, version, 0, version, 1);
        int confAttr[] = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_SURFACE_TYPE,
                EGL14.EGL_WINDOW_BIT,
                EGL14.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        EGL14.eglChooseConfig(eglDis, confAttr, 0, configs, 0, 1, numConfigs, 0);
        int ctxAttr[] = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,// 0x3098
                EGL14.EGL_NONE
        };
        eglCtx = EGL14.eglCreateContext(eglDis, configs[0], eglContext, ctxAttr, 0);
        int[] surfaceAttr = {
                EGL14.EGL_NONE
        };
        eglSurface = EGL14.eglCreateWindowSurface(eglDis, configs[0], surface, surfaceAttr, 0);

        //将EGLDisplay、EGLSurface、EGLContext绑定起来
        EGL14.eglMakeCurrent(eglDis, eglSurface, eglSurface, eglCtx);

    }

    public EGLContext getContext() {
        return eglCtx;
    }

    public void swap() {
        EGL14.eglSwapBuffers(eglDis, eglSurface);
    }

    public void release() {
        if (eglSurface != EGL14.EGL_NO_SURFACE) {
            EGL14.eglMakeCurrent(eglDis, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(eglDis, eglSurface);
            eglSurface = EGL14.EGL_NO_SURFACE;
        }
        if (eglCtx != EGL14.EGL_NO_CONTEXT ) {
            if(eglContext == EGL14.EGL_NO_CONTEXT){
                EGL14.eglDestroyContext(eglDis, eglCtx);
            }
            eglCtx = EGL14.EGL_NO_CONTEXT;
        }
        if (eglDis != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglTerminate(eglDis);
            eglDis = EGL14.EGL_NO_DISPLAY;
        }
    }
}
