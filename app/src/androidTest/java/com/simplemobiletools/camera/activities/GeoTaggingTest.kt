package com.simplemobiletools.camera.activities

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import com.simplemobiletools.camera.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class GeoTaggingTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.CAMERA",
                    "android.permission.WRITE_EXTERNAL_STORAGE")

    @Test
    fun geoTaggingTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)
        try {
            val appCompatImageView = onView(
                    allOf(withId(R.id.settings),
                            childAtPosition(
                                    allOf(withId(R.id.view_holder),
                                            childAtPosition(
                                                    withId(android.R.id.content),
                                                    0)),
                                    1),
                            isDisplayed()))
            appCompatImageView.perform(click())
        } catch (E: Exception) {
            print("Settings Error")
        }
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(2000)
        try {
            val appCompatImageView2 = onView(
                    allOf(withId(R.id.toggle_geotag),
                            childAtPosition(
                                    allOf(withId(R.id.view_holder),
                                            childAtPosition(
                                                    withId(android.R.id.content),
                                                    0)),
                                    7),
                            isDisplayed()))
            appCompatImageView2.perform(click())
        } catch (E: Exception) {
            print("Toggle GeoTag Error")
        }
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)
        try {
            val appCompatImageView3 = onView(
                    allOf(withId(R.id.shutter),
                            childAtPosition(
                                    allOf(withId(R.id.btn_holder),
                                            childAtPosition(
                                                    withId(R.id.view_holder),
                                                    9)),
                                    1),
                            isDisplayed()))
            appCompatImageView3.perform(click())
        } catch (E: Exception) {
            print("Shutter Error")
        }
        Thread.sleep(5000)
        try {
            val appCompatImageView4 = onView(
                    allOf(withId(R.id.settings),
                            childAtPosition(
                                    allOf(withId(R.id.view_holder),
                                            childAtPosition(
                                                    withId(android.R.id.content),
                                                    0)),
                                    1),
                            isDisplayed()))
            appCompatImageView4.perform(click())
        } catch (E: Exception) {
            print("Settings Error")
        }
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(2000)
        try {
            val appCompatImageView5 = onView(
                    allOf(withId(R.id.toggle_geotag),
                            childAtPosition(
                                    allOf(withId(R.id.view_holder),
                                            childAtPosition(
                                                    withId(android.R.id.content),
                                                    0)),
                                    7),
                            isDisplayed()))
            appCompatImageView5.perform(click())
        } catch (E: Exception) {
            print("Toggle GeoTag Error")
        }
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(2000)
        try {
            val appCompatImageView6 = onView(
                    allOf(withId(R.id.shutter),
                            childAtPosition(
                                    allOf(withId(R.id.btn_holder),
                                            childAtPosition(
                                                    withId(R.id.view_holder),
                                                    9)),
                                    1),
                            isDisplayed()))
            appCompatImageView6.perform(click())
        } catch (E: Exception) {
            print("Shutter Error")
        }
    }

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent) && view == parent.getChildAt(position)
            }
        }
    }
}
