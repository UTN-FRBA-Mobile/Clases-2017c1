package ar.edu.utn.frba.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import ar.edu.utn.frba.myapplication.api.UrlRequest;
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
    private CountingIdlingResource countingResource;
    private SelectPictureActivity activity;

    @Before
    public void setUp() throws Exception {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        activity = activityTestRule.launchActivity(new Intent());
        countingResource = new CountingIdlingResource("MyRequest");
        UrlRequest.setFactory(new UrlRequest.RequestFactory() {
            @NonNull
            @Override
            public UrlRequest makeRequest(URL url, UrlRequest.Listener listener) {
                return new UrlRequest(url, listener) {
                    @Override
                    public void run() {
                        countingResource.increment();
                        super.run();
                        countingResource.decrement();
                    }
                };
            }
        });
        Espresso.registerIdlingResources(countingResource);
    }

    @Test(timeout = 10000)
    public void checkLayout(){
        onView(withId(R.id.cameraButton)).check(matches(allOf(isDisplayed(), withText(R.string.camera))));
        onView(withId(R.id.galleryButton)).check(matches(allOf(isDisplayed(), withText(R.string.photo_gallery))));
        onView(withId(R.id.urlButton)).check(matches(allOf(isDisplayed(), withText(R.string.from_url))));
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

    @Test
    public void checkDownloadUrl() {
        // CountingIdleResource sirve para sincronizar tests cuando se ejecutan tareas asíncronas.
        // En este caso pausa el avance del test mientras descarga un archivo y eso lo hicimos
        // pisando el Factory de UrlRequest para que lo utilice.
        onView(withId(R.id.urlEditText)).perform(ViewActions.typeText("http://esotericos.org/wp-content/uploads/2012/08/Buscar-la-felicidad.jpg"));
        // La siguiente instrucción detiene el test porque arranca una descarga:
        onView(withId(R.id.urlButton)).perform(click());
        // La siguiente instrucción se ejecuta una vez que la descarga termina:
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
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
