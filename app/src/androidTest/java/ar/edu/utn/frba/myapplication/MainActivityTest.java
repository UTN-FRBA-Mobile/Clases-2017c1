package ar.edu.utn.frba.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import ar.edu.utn.frba.myapplication.picture.SelectPictureActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by guille on 5/8/17.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public IntentsTestRule<MainActivity> activityTestRule = new IntentsTestRule<>(MainActivity.class, true, false);
    private Instrumentation instrumentation;
    private MainActivity activity;

    @Before
    public void setUp() throws Exception {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        activity = activityTestRule.launchActivity(new Intent());
    }

    @Test(timeout = 10000)
    public void checkLayout(){
        onView(withId(R.id.button)).check(matches(allOf(isDisplayed(), withText(R.string.share))));
        onView(withId(R.id.otroButton)).check(matches(allOf(isDisplayed(), withText(R.string.otro))));
        onView(withId(R.id.termsAndConditions)).check(matches(allOf(isDisplayed(), withText(R.string.termsAndConditions))));
        onView(withId(R.id.helloButton)).check(matches(allOf(isDisplayed(), withText(R.string.hello))));

        onView(withId(R.id.editText)).perform(typeText("Hola"));
    }

    @Test (timeout = 10000)
    public void checkNavigate(){
        onView(withId(R.id.otroButton)).perform(click());
        onData(hasEntry(equalTo("50"), is("50")));
    }

    @Test (timeout = 10000)
    public void openActivity(){
        Instrumentation.ActivityMonitor activityMonitor = new Instrumentation.ActivityMonitor(SelectPictureActivity.class.getName(), null, false);
        instrumentation.addMonitor(activityMonitor);

        onView(withId(R.id.changePictureButton)).perform(click());

        Activity launchedActivity = instrumentation.waitForMonitor(activityMonitor);
        Assert.assertEquals(launchedActivity.getClass(), SelectPictureActivity.class);
        launchedActivity.finish();
    }

    @Test (timeout = 10000)
    public void checkToast(){
        onView(withId(R.id.helloButton)).perform(click());
        onView(withText(R.string.hello_toast))
                .inRoot(withDecorView(Matchers.not(is(activity.getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

}
