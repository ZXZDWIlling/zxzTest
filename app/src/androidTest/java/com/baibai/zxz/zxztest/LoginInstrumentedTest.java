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
import android.view.View;
import android.widget.ImageView;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
public class LoginInstrumentedTest {
    private String correctPhone = "05521397022";    //正确的手机号
    //登录的手机号、密码
    //[0]:手机号, [1]:密码, [2]:对应的测试用例标题
    private String ACCOUNT_PASSWORD[] = {
            "", "", "手机号和密码为空，登录失败",
            "", "123456", "手机号为空，登录失败",
            "18819425652", "123456", "手机号错误，登录失败",
            correctPhone, "", "手机号正确，密码为空，登录失败",
            correctPhone, "1234567", "手机号正确，密码为空，登录失败",
            correctPhone, "123456", "手机号正确，密码正确，登录成功"
    };

    private String PACKAGE_NAME = "com.baibai.baibai";//包名
    private Instrumentation mInstrumentation = null;
    private UiDevice mDevice = null;
    private Context mContext = null;
	private int countClickClearButton = 0;  //点击‘x’的测试

    //组件
    private UiObject mAccountEdit = null;
    private UiObject mPasswordEdit = null;
    private UiObject mLoginButton = null;

    @Before
    public void setUp(){
        if(null == mInstrumentation)
            mInstrumentation = InstrumentationRegistry.getInstrumentation();
        if(null == mDevice)
            mDevice = UiDevice.getInstance(mInstrumentation);
        if(null == mContext)
            mContext = InstrumentationRegistry.getContext();

        //添加第三方日志框架
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(1)
                .tag("zxz-login")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    @After
    public void tearDown(){

    }


    /**
     * 主方法
     */
    @Test
    public void testLoginMain() throws Exception{
        Logger.i("test start=================================================");
        testSplashTime();//测试启动页时间为5-10秒

        testLoginCase();//执行参数列表
    }

    /*
    按照手机号、密码不同情况测试登陆
     */
    private void testLoginCase() throws Exception{
        Random random = new Random();
        //确保登陆按钮存在
        if(null == mLoginButton || !mLoginButton.exists()){
            mLoginButton = mDevice.findObject(new UiSelector()
                    .resourceId("com.baibai.baibai:id/tv_login"));
        }
        //输入手机号和密码
        for(int i = 0; i < ACCOUNT_PASSWORD.length; i += 3){
            inputAccountAndPassword(ACCOUNT_PASSWORD[i], ACCOUNT_PASSWORD[i + 1]
                    , random.nextBoolean());
            mLoginButton.clickAndWaitForNewWindow();
        }
        //检查登陆成功
        UiObject enterButton = mDevice.findObject(new UiSelector()
                .resourceId("com.baibai.baibai:id/guide_start_tv"));
        //打印日志
        if(enterButton.exists()){
            if(countClickClearButton == 0)
                //统计点击清空按钮的次数
                Logger.w("Login success" + "\nClick ClearTextButton:\t\t" + countClickClearButton);
            else
                Logger.i("Login success" + "\nClick ClearTextButton:\t\t" + countClickClearButton);
        }
        else
            Logger.e("Login Failure:\n\t" + correctPhone + ":" + "123456");
        enterButton.clickAndWaitForNewWindow();//进入首页
    }

    /*
    测试启动页时间
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
            long start = SystemClock.uptimeMillis();
            //splash时间
            mDevice.wait(Until.hasObject(By.clazz(View.class)
                    .res("com.baibai.baibai:id/circleprogressview")), 60 * 1000);
            //广告时间
            long splashTime = SystemClock.uptimeMillis() - start;
            mDevice.wait(Until.hasObject(By.clazz(ImageView.class)
                    .res("com.baibai.baibai:id/iv_user_head")), 60 * 1000);
            //总时间
		    long totalTime = SystemClock.uptimeMillis() - start;
            //打印日志，显示时间
            Logger.t("Time").i("TotalTime:\t\t" + totalTime
                    + "\nSplashTime:\t\t" + splashTime
                    + "\nAdvTime:\t\t" + (totalTime - splashTime));
        }
    }

    /*
    输入账号和密码
     */
    private void inputAccountAndPassword(String account, String password
            , boolean clickClearButton) throws Exception{
        //打印日志，方便追踪
        Logger.t("input").d("%s:%s", account, password);
        //找到手机号和密码输入框
        if(null == mAccountEdit || !mAccountEdit.exists())
            mAccountEdit = mDevice.findObject(new UiSelector()
                    .resourceId("com.baibai.baibai:id/et_account"));
        if(null == mPasswordEdit || !mPasswordEdit.exists())
            mPasswordEdit = mDevice.findObject(new UiSelector()
                    .resourceId("com.baibai.baibai:id/et_password"));

        //输入手机号
        mAccountEdit.click();
        //根据传入参数决定如何清空手机号输入框,在于测试清空按钮的功能
        if(clickClearButton && mAccountEdit.getText().length() > 0){
			countClickClearButton++;
            UiObject clearButton = mDevice.findObject(new UiSelector()
                    .resourceId("com.baibai.baibai:id/fl_cancel"));
            if(!clearButton.exists())
                Logger.t("Input").e("ClearTextButton is not found");

            //点击按钮清空输入框
            clearButton.clickAndWaitForNewWindow();
        }else{
            //手动清空
            mAccountEdit.clearTextField();
        }
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
