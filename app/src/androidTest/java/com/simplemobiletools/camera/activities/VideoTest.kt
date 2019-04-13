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
import com.simplemobiletools.camera.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test

@LargeTest
class VideoTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.CAMERA",
                    "android.permission.RECORD_AUDIO",
                    "android.permission.WRITE_EXTERNAL_STORAGE")

    // This test assumes that the camera is currently in photo mode as it switches to video mode.
    @Test
    fun videoTest() {
        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(7000)

        // press settings
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

        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(2000)

        // press to toggle video mode
        val appCompatImageView2 = onView(
                allOf(withId(R.id.toggle_photo_video),
                        childAtPosition(
                                allOf(withId(R.id.view_holder),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()))
        appCompatImageView2.perform(click())

        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(7000)

        // begin record
        val appCompatImageView3 = onView(
                allOf(withId(R.id.shutter),
                        childAtPosition(
                                allOf(withId(R.id.btn_holder),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                7)),
                                1),
                        isDisplayed()))
        appCompatImageView3.perform(click())

        // Added a sleep statement to let camera record
        Thread.sleep(3000)

        // end recording
        val appCompatImageView4 = onView(
                allOf(withId(R.id.shutter),
                        childAtPosition(
                                allOf(withId(R.id.btn_holder),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                7)),
                                1),
                        isDisplayed()))
        appCompatImageView4.perform(click())

        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(7000)

        // open settings
        val appCompatImageView6 = onView(
                allOf(withId(R.id.settings),
                        childAtPosition(
                                allOf(withId(R.id.view_holder),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()))
        appCompatImageView6.perform(click())

        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(2000)

        // toggle photo mode back on
        val appCompatImageView7 = onView(
                allOf(withId(R.id.toggle_photo_video),
                        childAtPosition(
                                allOf(withId(R.id.view_holder),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()))
        appCompatImageView7.perform(click())
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
