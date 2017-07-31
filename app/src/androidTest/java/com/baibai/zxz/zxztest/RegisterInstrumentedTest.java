package com.baibai.zxz.zxztest;

/**
 * Created by Administrator on 2017/7/29.
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
import android.widget.ImageView;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RegisterInstrumentedTest {

    private String incorrectCode = null;
    private String correctCode = null;
    private String VALUES_CASE[] = null;
    private String PACKAGE_NAME = "com.baibai.baibai";  //包名
    private String CORRECT_PHONE = "18819425652";       //正确的手机号

    private Instrumentation mInstrumentation = null;
    private UiDevice mDevice = null;
    private Context mContext = null;


    private UiObject mRegisterLink = null;
    private UiObject mPhoneEdit = null;
    private UiObject mPassword1Edit = null;
    private UiObject mPassword2Edit = null;
    private UiObject mVerCodeEdit = null;
    private UiObject mChangeCode = null;
    private UiObject mSendButton = null;

    @Before
    public void setUp(){
        if(null == mInstrumentation)
            mInstrumentation = InstrumentationRegistry.getInstrumentation();
        if(null == mDevice)
            mDevice = UiDevice.getInstance(mInstrumentation);
        if(null == mContext)
            mContext = InstrumentationRegistry.getContext();

        mDevice.wait(Until.hasObject(By.res("com.baibai.baibai:id/iv_user_head"))
                , 60 * 1000);
        if(null == mRegisterLink)
            mRegisterLink = mDevice.findObject(new UiSelector()
                    .resourceId("com.baibai.baibai:id/tv_register"));
    }

    @After
    public void tearDown(){

    }

    @Test
    public void testRegisterMain() throws  Exception{

//        testBackFromPageSMSVerification();
        testBackFromPageRegistered();

        testBackFromPageSMSVerification();

        testRegisterCase();
    }

    private void testBackFromPageSMSVerification() throws Exception{
        mRegisterLink.clickAndWaitForNewWindow();
        //左上角返回键
        UiObject mBarBack = mDevice.findObject(new UiSelector()
                .resourceId("com.baibai.baibai:id/ll_title_left"));
        mBarBack.clickAndWaitForNewWindow();
        //系统返回键
        mRegisterLink.clickAndWaitForNewWindow();
        mDevice.pressBack();
    }

    private void testBackFromPageRegistered() throws Exception{
        mRegisterLink.clickAndWaitForNewWindow();
        UiObject mBarBack = mDevice.findObject(new UiSelector()
                .resourceId("com.baibai.baibai:id/ll_title_left"));
        UiObject button = mDevice.findObject(new UiSelector()
                .resourceId("com.baibai.baibai:id/tv_one_key_login"));
        //连续两次左上角返回键
        button.clickAndWaitForNewWindow();
        mBarBack.clickAndWaitForNewWindow();
        mBarBack.clickAndWaitForNewWindow();
        //连续两次系统返回键
        mRegisterLink.clickAndWaitForNewWindow();
        button.clickAndWaitForNewWindow();
        mDevice.pressBack();
        mDevice.pressBack();
        mDevice.pressBack();
    }

    private void testRegisterCase() throws Exception{
        mRegisterLink.clickAndWaitForNewWindow();
        UiObject button = mDevice.findObject(new UiSelector()
                .resourceId("com.baibai.baibai:id/tv_one_key_login"));
        button.clickAndWaitForNewWindow();
        mDevice.pressBack();

        mPhoneEdit = mDevice.findObject(new UiSelector().resourceId
                ("com.baibai.baibai:id/et_account"));
        mPassword1Edit = mDevice.findObject(new UiSelector().resourceId
                ("com.baibai.baibai:id/et_password1"));
        mPassword2Edit = mDevice.findObject(new UiSelector().resourceId
                ("com.baibai.baibai:id/et_password2"));
        mVerCodeEdit = mDevice.findObject(new UiSelector().resourceId
                ("com.baibai.baibai:id/et_graph"));
        mChangeCode = mDevice.findObject(new UiSelector().resourceId
                ("com.baibai.baibai:id/tv_cut"));
        mSendButton = mDevice.findObject(new UiSelector().resourceId
                ("com.baibai.baibai:id/tv_next"));

        //第一次输入的时候，就输入正确的图片验证码
        input(VALUES_CASE[0], VALUES_CASE[1], VALUES_CASE[2]);
        SystemClock.sleep(30 * 1000);
        correctCode = mVerCodeEdit.getText();
        initCase();
        if(correctCode.length() ==0){
            //当测试人员忘了操作时，退出
            Logger.t("Tester Error").e("You must input the Verification Code");
            assertEquals(0, correctCode.length());
        }
        for(int i = 3; i < VALUES_CASE.length; i += 3){
            input(VALUES_CASE[i], VALUES_CASE[i + 1], VALUES_CASE[i + 2]);
            mVerCodeEdit.clearTextField();
            mVerCodeEdit.setText(correctCode);
        }
    }

    //输入各项内容
    private void input(String phone, String password1, String password2) throws Exception{
        mPhoneEdit.click();
        mPhoneEdit.clearTextField();
        mPhoneEdit.setText(phone);

        mPassword1Edit.click();
        mDevice.pressKeyCode(KeyEvent.KEYCODE_MOVE_END);
        for(int i = 0; i < 16; i++){
            mDevice.pressKeyCode(KeyEvent.KEYCODE_DEL);
        }
        mPassword1Edit.setText(password1);

        mPassword2Edit.click();
        mDevice.pressKeyCode(KeyEvent.KEYCODE_MOVE_END);
        for(int i = 0; i < 16; i++){
            mDevice.pressKeyCode(KeyEvent.KEYCODE_DEL);
        }
        mPassword2Edit.setText(password2);
    }

    /*
    初始化测试用例
     */
    private void initCase(){
        //确保错误的验证码与正确的是不一致的
        Random random = new Random();
        while(incorrectCode.equals(correctCode))
             incorrectCode = "" + random.nextInt(10)+ random.nextInt(10)+ random.nextInt(10)+ random.nextInt(10);
        //初始化数据
        VALUES_CASE =new String[]{
                "", "123456", "123456", correctCode, "不输入手机号，发送短信失败",
                "0987654321", "123456", "123456", correctCode, "手机号长度为10，发送短信失败",
                "098765432109", "123456", "123456", correctCode, "手机号长度为12，发送短信失败",
                "abck123^*&^*", "123456", "123456", correctCode, "手机号包含非数字，不能输入",
                CORRECT_PHONE, "", "123456", correctCode, "不输入密码，发送短信失败",
                CORRECT_PHONE, "12345", "12345", correctCode, "密码长度为5，发送失败",
                CORRECT_PHONE, "12345678901234567", "12345678901234567", correctCode, "密码长度为17，不能输入",
                CORRECT_PHONE, "123456", "", correctCode, "不输入确认密码，发送失败",
                CORRECT_PHONE, "123456", "654321", correctCode, "确认密码和密码不一致，发送失败",
                CORRECT_PHONE, "123456", "123456", correctCode, "不输入验证码，发送失败",  //不输入验证码
                CORRECT_PHONE, "123456", "123456", incorrectCode, "验证码错误，发送失败", //验证码错误
                CORRECT_PHONE, "123456", "123456", correctCode, "密码长度为6，发送成功",  //正确
                CORRECT_PHONE, "1234567890123456", "1234567890123456", correctCode, "密码长度为16，发送成功\n",//正确
        };
    }
}
