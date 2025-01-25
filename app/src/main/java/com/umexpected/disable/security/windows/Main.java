package com.unexpected.hook.disable.security.windows;

import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class Main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 禁用安全窗口
        XposedHelpers.findAndHookMethod(Window.class, "setFlags", int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    Integer flags = (Integer) param.args[0];
                    flags &= ~ WindowManager.LayoutParams.FLAG_SECURE;
                    param.args[0] = flags;
                }
            });
        XC_MethodHook methodHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                if(param.args[1] instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) param.args[1];
                    params.flags &= ~ WindowManager.LayoutParams.FLAG_SECURE;
                }
            }
        };
        XposedHelpers.findAndHookMethod("android.view.WindowManagerImpl", lpparam.classLoader, "addView", View.class, ViewGroup.LayoutParams.class, methodHook);
        XposedHelpers.findAndHookMethod("android.view.WindowManagerImpl", lpparam.classLoader, "updateViewLayout", View.class, ViewGroup.LayoutParams.class, methodHook);
        // 禁用安全视图
        XposedHelpers.findAndHookMethod(SurfaceView.class, "setSecure", boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    param.args[0] = false;
                }
            });
        // 禁用触摸过滤，允许被遮挡时执行点击事件
        XposedHelpers.findAndHookMethod(View.class, "setFilterTouchesWhenObscured", boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    param.args[0] = false;
                }
            });
    }
}
