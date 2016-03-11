package test.meizu.com.clock;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Point;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiWatcher;

import com.meizu.test.common.CommonUtil;
import com.meizu.test.common.DeviceHelper;
import com.meizu.test.common.ResultUtil;
import com.meizu.u2.U2BaseTestCase;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;


/**
 * Created by hujiachun on 15/12/25.
 */
public class TestScript extends U2BaseTestCase{

    public static String TAG = "TestCase";
    public static TestScript testScript;
    public static Instrumentation mInstrumentation;
    public static Context mContext;
    public static UiDevice device;
    private static CommonUtil commonUtil;
    private static DeviceHelper deviceHelper;
    private static ScriptService script;

    public static TestScript getInstance(CommonUtil commonUtil, UiDevice mdevice) {
        commonUtil = commonUtil;
        testScript = new TestScript();
        device = mdevice;
        return testScript;
    }

    public TestScript(){
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        mContext = InstrumentationRegistry.getContext();
        device = UiDevice.getInstance(mInstrumentation);
        commonUtil = CommonUtil.getInstance(mInstrumentation);
        deviceHelper = DeviceHelper.getInstance(mInstrumentation);
        script = new ScriptService();
    }



    public void sleep(long timeout) throws InterruptedException {
        Thread.sleep(timeout);
    }


    public void assertTrue(String message, boolean condition){
        Assert.assertTrue(message, condition);
    }


    public void assertEquals(Object expected, Object actual){
        Assert.assertEquals(expected, actual);
    }


    /**
     * 添加闹钟
     * @param bool 是否连续添加
     * @throws UiObjectNotFoundException
     */
    public void addClock(Boolean bool) throws UiObjectNotFoundException, InterruptedException {

        if(bool){
            while (script.isEnabled("com.android.alarmclock:id/fab")){
                setTime(7, 0, 0);
                script.clickById("com.android.alarmclock:id/fab");
                script.clickByText("确定");
            }
        }
        else{
            setTime(7, 0, 0);
            script.clickById("com.android.alarmclock:id/fab");
            script.clickByText("确定");
        }



    }


    public void clearClock() throws InterruptedException {
        script.clickByDesc("闹钟");
        int sum = 0;
        while ((!script.isExistByText("没有闹钟", 1500)) && sum < 50){
            script.longClick("com.android.alarmclock:id/clock_layout", 2000);
            script.clickByText("删除");
            sum++;
        }

    }


    public void clearWorldClock() throws InterruptedException {
        script.clickByDesc("世界时钟");
        if(!script.isExistByText("没有世界时钟", 3000)){
            script.longClick("com.android.alarmclock:id/city_name", 1500);
            int city = device.findObjects(By.res("com.android.alarmclock:id/city_name")).size();
            if(city > 1){
                script.clickByText("全选");
            }
            script.clickByText("删除");
        }
    }


    public void addWorldClock(){
        script.clickById("com.android.alarmclock:id/fab");
        script.clickById("com.android.alarmclock:id/city_name");
    }


    public void startApp(String app) throws InterruptedException {
        int sum = 0;
        device.pressHome();
        this.sleep(500);
        while ((!script.isExistByText(app, 2000) && sum < 10)) {
            swipeLeft();//左滑屏幕
            sum++;
        }
        script.clickByText(app);
        this.sleep(500);
    }

    private void swipeLeft() {
        int dh = device.getDisplayHeight();
        int dw = device.getDisplayWidth();
        device.swipe(dw * 9 / 10, dh / 2, dw / 10, dh / 2, 100);
    }





    /**
     * 设置时间
     * @param hour
     * @param min
     * @param sec
     */
    public void setTime(int hour, int min, int sec){
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int mou = ca.get(Calendar.MONTH);
        int day = ca.get(Calendar.DAY_OF_MONTH);

        ca.set(year, mou, day, hour, min, sec);
        commonUtil.sendTime(ca.getTime());

    }


    /**
     * 返回桌面
     */
    public void pressHome() throws InterruptedException {
        int i = 0;
        while (!script.findUiObjectById("com.meizu.flyme.launcher:id/launcher").exists() && i < 15){
            device.pressBack();
            i++;
        }
    }


    public void assertClock(boolean ischecked) throws InterruptedException {
        device.openNotification();
        this.sleep(2000);
        boolean ring = script.waitUntilGone("com.android.systemui:id/settings_button", 30000);
        int deviceWidth = device.getDisplayWidth();
        int deviceHeight = device.getDisplayHeight();
        if(ischecked){
            if(ring){
                this.sleep(1000);
                device.click(deviceWidth / 1080 * 775, deviceHeight / 1920 * 265);
            }
            else {
                device.drag(deviceHeight, deviceWidth / 2, deviceHeight / 2, deviceWidth / 2, 100);
                assertTrue("闹钟未响起", ring);
            }
        }
        else{
            if(ring){
                this.sleep(1000);
                device.click(deviceWidth / 1080 * 775, deviceHeight / 1920 * 265);
                assertTrue("闹钟响起", !ring);
            }
            else {
                device.drag(deviceHeight, deviceWidth / 2, deviceHeight / 2, deviceWidth / 2, 100);
            }
        }
    }


    public Point clearStopWatch() {
        Point center = null;
        script.clickByDesc("秒表");
        boolean start = script.isExistByText("开始", 2000);
        if (!start) {//开始按钮不存在
            boolean pause = script.isExistByText("暂停", 2000);
            if (pause) {
                script.clickByText("暂停");
            }
            center = script.findUiobject2ById("com.android.alarmclock:id/btn_start_stop").getVisibleCenter();
            script.clickByText("复位");
        }
        return center;
    }


    public void clearTimer() {
        script.clickByDesc("计时器");
        if (!script.isExistByText("开始", 2000)) {
            script.clickByText("取消");
        }
    }


    public void setTimer() throws UiObjectNotFoundException, InterruptedException {
        script.findUiObjectById("com.android.alarmclock:id/horizontal_wheel_view").swipeLeft(40);
        this.sleep(1000);
    }



    public void uiautomatorWatcher(){
       device.registerWatcher("权限申请", new UiWatcher() {
           @Override
           public boolean checkForCondition() {
//               if(script.isExistById("android:id/button1", 2000) && script.isExistByText("允许", 20000)){
//                   script.clickByText("允许");
//               }
               if(script.findUiObjectById("android:id/button1").exists() && script.findUiObjectByText("允许").exists()){
                   script.clickByText("允许");
               }
               return false;
           }
       });


    }
}
