package com.domgarr.android.volleyballscorekeeper;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mMainActivity;

    @Before
    public void setUp() throws Exception {
        mMainActivity = mMainActivityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        mMainActivity = null;
    }

    @Test
    public void isBluetoothLeSupportedTest() {
        assertEquals(true, mMainActivity.isBluetoothLeSupported());
    }

    @Test
    public void initBluetoothAdapterTest() {
        assertNotNull(mMainActivity.initBluetoothAdapter());
    }

    @Test
    public void isBluetoothEnableTest(){
        assertEquals(true, mMainActivity.isBluetoothEnabled(mMainActivity.initBluetoothAdapter()));
    }
}