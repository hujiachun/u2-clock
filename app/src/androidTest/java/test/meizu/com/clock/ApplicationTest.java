package test.meizu.com.clock;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.Until;
import android.util.Log;
import com.meizu.test.common.AppInfo;
import com.meizu.test.common.CommonUtil;
import com.meizu.test.common.DeviceHelper;
import com.meizu.test.common.ResultUtil;
import com.meizu.u2.annotations.Description;
import com.meizu.u2.annotations.Module;
import com.meizu.u2.annotations.RunFor;
import com.meizu.u2.exception.TimeoutTooLongException;
import com.meizu.u2.utils.TestType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunFor(type = TestType.TEST_SANITY, version = TestType.FLYME_5)
@RunWith(AndroidJUnit4.class)
@Module("clock")
public class ApplicationTest extends TestScript {

    //-------------------------------定义成员变量-----------------------------
    private static Instrumentation mInstrumentation;
    private static Context mContext;
    private static UiDevice mDevice;
    private static CommonUtil commonUtil;
    private static DeviceHelper deviceHelper;
    private static Intent intent;
    private static ScriptService script;
    private static final int TIMEOUT = 5000;
    private static final int COUNT = 2;
    private static PrintWriter logPrintWriter;
    private static String imaDirName;


    //---------------------------------初始化与结束--------------------------
    @BeforeClass
    public static void beforeClass() throws IOException {
        //初始化参数
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        mContext = InstrumentationRegistry.getContext();
        mDevice = UiDevice.getInstance(mInstrumentation);
        commonUtil = CommonUtil.getInstance(mInstrumentation);
        deviceHelper = DeviceHelper.getInstance(mInstrumentation);
        intent = mContext.getPackageManager()
                .getLaunchIntentForPackage(AppInfo.PACKAGE_ALARMCLOCK).setClassName(AppInfo.PACKAGE_ALARMCLOCK, AppInfo.ACTIVITY_ALARMCLOCK);

        script = ScriptService.getInstance();

        String packageName = mContext.getPackageName();
        ResultUtil.initResult(packageName);
        logPrintWriter = ResultUtil.getLogPrintWriter();
        imaDirName = ResultUtil.getImgDirName();

    }

    @AfterClass
    public static void afterClass() {
        //用来清理跑完的资源，uninstall什么的，deleter什么的
        logPrintWriter.close();
    }

    //--------------------------------重写--------------------------------
    @Override
    public void before() {
        super.before();
        uiautomatorWatcher();
        device.runWatchers();
        mDevice.pressHome();
        commonUtil.startApp(AppInfo.PACKAGE_ALARMCLOCK);
        mDevice.wait(Until.hasObject(By.pkg(AppInfo.PACKAGE_ALARMCLOCK)), TIMEOUT);
    }

    @Override
    public void after() {
        commonUtil.sendRestoreTime();
        try {
            pressHome();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.after();
    }



    public void assertResultById(String id, int i){
        try {
            script.assertById(id);
        } catch (Exception e) {
            e.printStackTrace();
            logPrintWriter.append("\n" + this.getName() + "_" + i + "----------" + "\n");
            logPrintWriter.flush();

            String imaFileName = imaDirName + "/" + this.getName() + "_" + i + ".png";
            try {
                ResultUtil.createFile(new File(imaFileName));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            mDevice.takeScreenshot(new File(imaFileName));
        }
    }


    public void assertResultByText(String text, int i){
        try {
            script.assertByText(text);
        } catch (Exception e) {
            e.printStackTrace();
            logPrintWriter.append("\n" + this.getName() + "_" + i + "----------" + "\n");
            logPrintWriter.flush();

            String imaFileName = imaDirName + "/" + this.getName() + "_" + i + ".png";
            try {
                ResultUtil.createFile(new File(imaFileName));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            mDevice.takeScreenshot(new File(imaFileName));
        }
    }


    @Test
    @Description(steps = "冷启动", expectation = "1、不报错,响应正常", priority = Description.P1)
    public void test001CoolStart() throws IOException, InterruptedException {

        for(int i = 0; i < COUNT; i++) {
            commonUtil.exitApp(AppInfo.PACKAGE_ALARMCLOCK);
            startApp("时钟");
            assertResultById("com.android.alarmclock:id/mz_action_bar_tab_scroll_view", i);

        }
    }


    @Test
    @Description(steps = "热启动", expectation = "1、不报错,响应正常", priority = Description.P1)
    public void test002HotStart() throws UiObjectNotFoundException, InterruptedException {

        for(int i = 0; i < COUNT; i++){
            device.pressBack();
            startApp("时钟");
            assertResultById("com.android.alarmclock:id/mz_action_bar_tab_scroll_view", i);
            device.pressBack();

        }
    }


    @Test
    @Description(steps = "1、点击闹钟、世界时钟、秒表、计时器页面，不停顿", expectation = "1、不报错", priority = Description.P1)
    public void test003Change() {
        for(int i = 0; i < COUNT; i++){
            script.clickByDesc("闹钟");
            script.clickByDesc("世界时钟");
            script.clickByDesc("秒表");
            script.clickByDesc("计时器");

        }


    }

    @Test
    @Description(steps = "反复关闭和打开闹钟开关", expectation = "1、若是打开状态、闹钟到点响起，若是关闭状态、闹钟到点后不响起\n" +
            "2、不报错", priority = Description.P1)
    public void test004ClockSwitch() throws InterruptedException, UiObjectNotFoundException {
        clearClock();
        addClock(false);
        for(int i = 0; i < COUNT; i++){
            script.clickById("com.android.alarmclock:id/switch_onoff_sign");
            this.sleep(100);
        }
        this.sleep(1000);
        boolean ischecked = script.isChecked("com.android.alarmclock:id/switch_onoff_sign");
        setTime(6, 59, 50);
        assertClock(ischecked);
    }


    @Test
    @Description(steps = "1、常按闹钟进去选择状态2、反复点击世界时钟", expectation = "1、不报错2、选择功能正常", priority = Description.P1)
    public void test005LongclickClock() throws InterruptedException {
        clearWorldClock();
        addWorldClock();
        script.longClick("com.android.alarmclock:id/city_name", 1500);
        for(int i = 0; i < COUNT; i++){
            script.clickById("android:id/checkbox");
            this.sleep(100);
        }
        assertTrue("选择功能失败", script.isExistByText("已选择1项", 2000));

    }


    @Test
    @Description(steps = "1、开始秒表、计时2、点击暂停/继续按钮", expectation = "1、不报错2、选择功能正常", priority = Description.P1)
    public void test006Stopwatch() throws InterruptedException {
        Point point ;
        point = clearStopWatch();
        script.clickByText("开始");
        if(point == null){
            script.clickByText("暂停");
            point = script.getPoint("com.android.alarmclock:id/btn_start_stop");
        }
        Log.e("TestCase", point.x + "," + point.y);
        for(int i = 0; i < COUNT; i++){
            device.click(point.x, point.y);
            this.sleep(500);
        }
        assertTrue("暂停/继续按钮", script.isExistByText("继续", 2000));
    }


    @Test
    @Description(steps = "开始计时器后点击暂停/继续按钮", expectation = "1、不报错2、选择功能正常", priority = Description.P1)
    public void test007Timer() throws InterruptedException, UiObjectNotFoundException {
        clearTimer();
        Point point = script.getPoint("com.android.alarmclock:id/btn_start_counting");
        int lenght = script.getlenght("com.android.alarmclock:id/btn_start_counting");
        int pauseX = point.x - lenght / 2;
        setTimer();
        device.click(point.x, point.y);//开始
        this.sleep(1000);
        for(int i = 0; i < COUNT; i++){
            device.click(pauseX, point.y);
            this.sleep(500);
        }
        boolean result = script.isExistByText("暂停", 2000);
        script.clickByText("取消");
        assertTrue("暂停/继续按钮", result);
    }


    @Test
    @Description(steps = "开始计时器后点击开始/取消按钮", expectation = "1、不报错2、选择功能正常", priority = Description.P1)
    public void test008StartWatch() throws InterruptedException, UiObjectNotFoundException {
        clearTimer();
        Point point = script.getPoint("com.android.alarmclock:id/btn_start_counting");
        int lenght = script.getlenght("com.android.alarmclock:id/btn_start_counting");
        int cancelX = point.x + lenght / 2;
        setTimer();
        for(int i = 0; i < COUNT; i++){
            device.click(point.x, point.y);//开始
            this.sleep(1000);
            device.click(cancelX, point.y);//取消
            this.sleep(1000);
        }
        assertTrue("开始/取消按钮", script.isExistByText("开始", 2000));
    }


    @Test
    @Description(steps = "1、设置闹钟、开始计时器、开始秒表2、查看闹钟、计时器、秒表状态", expectation = "1、不报错2、各个功能正常", priority = Description.P1)
    public void test009Concurrent() throws InterruptedException, UiObjectNotFoundException {
        clearClock();
        addClock(false);
        clearStopWatch();
        script.clickByText("开始");
        clearTimer();
        setTimer();
        script.clickByText("开始");
        setTime(6, 59, 50);
        assertClock(true);
    }


    @Test
    @Description(steps = "闹钟按钮,点击添加闹钟按钮", expectation = "1、不报错2、响应正常", priority = Description.P1)
    public void test010addClock() throws InterruptedException {
        clearClock();
        for(int i = 0; i < COUNT; i++){
            script.clickById("com.android.alarmclock:id/fab");
                assertResultByText("添加闹钟", i);
                device.pressBack();

        }

    }


    @Test
    @Description(steps = "世界时钟按钮,点击添加闹钟按钮", expectation = "1、不报错2、响应正常", priority = Description.P1)
    public void test011AddWorldClcok() throws InterruptedException {
        clearWorldClock();
        for(int i = 0; i < COUNT; i++){
            script.clickById("com.android.alarmclock:id/fab");
            assertResultByText("搜索", i);
            device.pressBack();

        }


    }

}