package test.meizu.com.clock;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Point;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import com.meizu.test.common.CommonUtil;
import com.meizu.test.common.DeviceHelper;


/**
 * Created by hujiachun on 15/12/25.
 */
public class ScriptService {

    private static Instrumentation mInstrumentation;
    private static Context mContext;
    private static UiDevice device;
    private static CommonUtil commonUtil;
    private static DeviceHelper deviceHelper;
    private static int WAIT = 3000;


    public ScriptService() {
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        mContext = InstrumentationRegistry.getContext();
        device = UiDevice.getInstance(mInstrumentation);
        commonUtil = CommonUtil.getInstance(mInstrumentation);
        deviceHelper = DeviceHelper.getInstance(mInstrumentation);

    }


    public static ScriptService getInstance() {

        return new ScriptService();
    }

    public UiSelector byId(String id){

        return new UiSelector().resourceId(id);
    }


    public UiSelector byId(String id, int instance){

        return new UiSelector().resourceId(id).instance(instance);
    }


    public UiSelector byId(String id, Integer index){

        return new UiSelector().resourceId(id).index(index);
    }


    public UiSelector byText(String text){

        return new UiSelector().text(text);
    }


    public UiSelector byClassName(String className){

        return new UiSelector().className(className);
    }


    public UiSelector byDesc(String desc){

        return new UiSelector().description(desc);
    }


    public boolean isEnabled(String id) throws UiObjectNotFoundException {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return device.findObject(byId(id)).isEnabled();
    }


    public boolean isChecked(String id) throws UiObjectNotFoundException {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return device.findObject(byId(id)).isChecked();
    }


    public boolean isExistById(String id, int time)  {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UiSelector selector_id = new UiSelector().resourceId(id);
        return device.findObject(selector_id).waitForExists(time);
    }


    public void assertById(String id) throws UiObjectNotFoundException, InterruptedException {
        device.wait(Until.findObject(By.res(id)), WAIT);
        device.findObject(By.res(id)).getVisibleBounds();

    }


    public void assertByText(String text) throws UiObjectNotFoundException, InterruptedException {
        device.wait(Until.findObject(By.text(text)), WAIT);
        device.findObject(By.text(text)).getVisibleBounds();

    }


    public boolean isExistByText(String text, int time) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UiSelector selector_text = new UiSelector().text(text);

        return device.findObject(selector_text).waitForExists(time);
    }


    public void clickByText(String text) {
        device.wait(Until.findObject(By.text(text)), WAIT);
        device.findObject(By.text(text)).click();
    }


    public void clickById(String id) {
        device.wait(Until.findObject(By.res(id)), WAIT);
        device.findObject(By.res(id)).click();
    }


    public void clickByDesc(String desc) {
        device.wait(Until.findObject(By.desc(desc)), WAIT);
        device.findObject(By.desc(desc)).click();
    }


    public UiObject2 findUiobject2ById(String id){
        return device.findObject(By.res(id));
    }


    public UiObject findUiObjectById(String id){
        return device.findObject(new UiSelector().resourceId(id));
    }


    public UiObject findUiObjectByText(String text){
        return device.findObject(new UiSelector().text(text));
    }


    public boolean waitUntilGone(String id, long timeout){
        return findUiObjectById(id).waitUntilGone(timeout);
    }


    public Point getPoint(String id){
        Point point = findUiobject2ById(id).getVisibleCenter();
        return point;
    }


    public int getlenght(String id){
        int lenght = findUiobject2ById(id).getVisibleBounds().right -
                findUiobject2ById(id).getVisibleBounds().left;
        return lenght;
    }

    /**
     * 长按
     * @param id
     * @param milliseconds
     */
    public void longClick(String id, int milliseconds) throws InterruptedException {
        Thread.sleep(200);
        int x = findUiobject2ById(id).getVisibleBounds().centerX(), y = findUiobject2ById(id).getVisibleBounds().centerY();
        int steps = (int) (milliseconds / 20);
        device.swipe(x, y, x, y, steps);

    }


}