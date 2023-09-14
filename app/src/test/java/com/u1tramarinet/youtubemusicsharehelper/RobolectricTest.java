package com.u1tramarinet.youtubemusicsharehelper;

import android.os.Looper;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.u1tramarinet.youtubemusicsharehelper.screen.MainActivity;
import com.u1tramarinet.youtubemusicsharehelper.screen.main.MainFragment;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.LooperMode;

@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.PAUSED)
public class RobolectricTest {
    @Test
    public void testStartScreen() {
        try (ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class)) {
            controller.setup().start();
            MainActivity activity = controller.get();
            Assert.assertNotNull(activity);
            Fragment navHost = activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            Assert.assertNotNull(navHost);

            Fragment currentFragment = navHost.getChildFragmentManager().getFragments().get(0);
            Assert.assertNotNull(currentFragment);

            Shadows.shadowOf(Looper.getMainLooper()).idle();
            Assert.assertEquals(MainFragment.class.getName(), currentFragment.getClass().getName());

            View fragmentRoot = currentFragment.getView();
            Assert.assertNotNull(fragmentRoot);
        }
    }
}
