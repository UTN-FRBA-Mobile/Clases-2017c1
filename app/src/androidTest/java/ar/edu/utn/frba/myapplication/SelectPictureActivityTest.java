package ar.edu.utn.frba.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ar.edu.utn.frba.myapplication.picture.SelectPictureActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by guille on 5/8/17.
 */

@RunWith(AndroidJUnit4.class)
public class SelectPictureActivityTest {

    @Rule
    public IntentsTestRule<SelectPictureActivity> activityTestRule = new IntentsTestRule<>(SelectPictureActivity.class, true, false);
    private Instrumentation instrumentation;
    private SelectPictureActivity activity;


    @Before
    public void setUp() throws Exception {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        activity = activityTestRule.launchActivity(new Intent());
    }

    @Test(timeout = 10000)
    public void checkLayout(){
        onView(withId(R.id.cameraButton)).check(matches(allOf(isDisplayed(), withText(R.string.camera))));
        onView(withId(R.id.galleryButton)).check(matches(allOf(isDisplayed(), withText(R.string.photo_gallery))));
    }

    @Test(timeout = 10000)
    public void checkCamera() {
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(getPhotoIdActivityResult());
        onView(withId(R.id.cameraButton)).perform(click());
    }

    @Test(timeout = 10000)
    public void checkGallery() {
        intending(not(isInternal())).respondWith(getPhotoIdActivityResult());
        onView(withId(R.id.galleryButton)).perform(click());
    }

    private Instrumentation.ActivityResult getPhotoIdActivityResult() {
        Intent resultData = new Intent();

        resultData.setData(
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                        activity.getResources().getResourcePackageName(R.raw.test) + '/' +
                        activity.getResources().getResourceTypeName(R.raw.test) + '/' +
                        activity.getResources().getResourceEntryName(R.raw.test)) );

        return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
    }
}
