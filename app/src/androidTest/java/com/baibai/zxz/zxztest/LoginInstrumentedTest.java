package com.baibai.zxz.zxztest;

/**
 * Created by zxz on 2017/7/27 0027.
 */

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.view.KeyEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LoginInstrumentedTest {

    //登录的手机号、密码
    //[0]:手机号, [1]:密码, [2]:对应的测试用例标题
    String ACCOUNT_PASSWORD[] = {
            "", "", "手机号和密码为空，登录失败",
            "", "123456", "手机号为空，登录失败",
            "18819425652", "123456", "手机号错误，登录失败",
            "05521397022", "", "手机号正确，密码为空，登录失败",
            "05521397022", "1234567", "手机号正确，密码为空，登录失败",
            "05521397022", "123456", "手机号正确，密码正确，登录成功"
    };

    String PACKAGE_NAME = "com.baibai.baibai";//包名
    Instrumentation mInstrumentation = null;
    UiDevice mDevice = null;
    Context mContext = null;

    //组件
    UiObject mAccountEdit = null;
    UiObject mPasswordEdit = null;
    UiObject mLoginButton = null;

    @Before
    public void setUp(){
        if(null == mInstrumentation)
            mInstrumentation = InstrumentationRegistry.getInstrumentation();
        if(null == mDevice)
            mDevice = UiDevice.getInstance(mInstrumentation);
        if(null == mContext)
            mContext = InstrumentationRegistry.getContext();
    }

    @After
    public void tearDown(){

    }


    /**
     * 主方法
     */
    @Test
    public void testLoginMain() throws Exception{
        testSplashTime();//测试启动页时间为5-10秒

        testLoginCase();//执行参数列表
    }

    /*
    按照手机号、密码不同情况测试登陆
     */
    private void testLoginCase() throws Exception{
        //确保登陆按钮存在
        if(null == mLoginButton || !mLoginButton.exists()){
            mLoginButton = mDevice.findObject(new UiSelector());
        }
        //输入手机号和密码
        for(int i = 0; i < ACCOUNT_PASSWORD.length; i += 3){
            inputAccountAndPassword(ACCOUNT_PASSWORD[i], ACCOUNT_PASSWORD[i + 1]);
            mLoginButton.clickAndWaitForNewWindow();
        }
        //检查登陆成功
        UiObject enterButton = mDevice.findObject(new UiSelector()
                .resourceId("com.baibai.baibai:id/tv_login"));
        assertTrue("Login not success", enterButton.exists());
    }

    /*
    测试启动页时间为5-10秒
     */
    private void testSplashTime() throws Exception{
        mDevice.pressHome();
        mDevice.wait(Until.hasObject(By.pkg(mDevice.getLauncherPackageName())), 3 * 1000);
        //启动应用
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        if(null != intent){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(intent);
            mDevice.wait(Until.hasObject(By.pkg(PACKAGE_NAME)), 3 * 1000);
            //开始计时
            UiObject mSplashPic = mDevice.findObject(new UiSelector()
                    .className("android.widget.FrameLayout").packageName(PACKAGE_NAME));
            if(0 == mSplashPic.getChildCount()){
                long start = SystemClock.uptimeMillis();
                mSplashPic.waitUntilGone(10 * 1000);
                long time = SystemClock.uptimeMillis() - start;
                //判断停留在启动页时间5-10秒之间
                assertTrue("the Splash time is not bettween 5s and 10s"
                        , time >= 5 * 1000 && time <= 10 * 1000);
            }


        }
    }

    /*
    输入账号和密码
     */
    private void inputAccountAndPassword(String account, String password) throws Exception{
        assertNotNull("account is not", account);
        assertNotNull("password is not", password);
        //找到手机号和密码输入框
        if(null == mAccountEdit || !mAccountEdit.exists())
            mAccountEdit = mDevice.findObject(new UiSelector()
                    .resourceId("com.baibai.baibai:id/et_account"));
        if(null == mPasswordEdit || !mPasswordEdit.exists())
            mPasswordEdit = mDevice.findObject(new UiSelector()
                    .resourceId("com.baibai.baibai:id/et_password"));
/*
if(null == mClearButton || !mClearButton.exists())
	UiObject mClearButton = mDevice.findObject(new UiSelector());
mClearButton.clickAndWaitForNewWindow();
*/

        //输入手机号
        mAccountEdit.click();
        mAccountEdit.clearTextField();
        mAccountEdit.setText(account);
        //输入密码
        mPasswordEdit.click();
        mDevice.pressKeyCode(KeyEvent.KEYCODE_MOVE_END);
        for(int i = 0; i < 16; i++){
            mDevice.pressKeyCode(KeyEvent.KEYCODE_DEL);
        }
        mPasswordEdit.setText(password);

    }
}
