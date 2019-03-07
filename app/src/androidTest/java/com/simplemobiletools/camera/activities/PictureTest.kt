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
class PictureTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.CAMERA",
                    "android.permission.WRITE_EXTERNAL_STORAGE")

    @Test
    fun pictureTest() {
        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(5000)

        // Take a picture
        val appCompatImageView = onView(
                allOf(withId(R.id.shutter),
                        childAtPosition(
                                allOf(withId(R.id.btn_holder),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                5)),
                                1),
                        isDisplayed()))
        appCompatImageView.perform(click())

        // Turn on flash
        val appCompatImageView2 = onView(
                allOf(withId(R.id.toggle_flash),
                        childAtPosition(
                                allOf(withId(R.id.btn_holder),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                5)),
                                2),
                        isDisplayed()))
        appCompatImageView2.perform(click())

        // Take a picture with flash
        val appCompatImageView3 = onView(
                allOf(withId(R.id.shutter),
                        childAtPosition(
                                allOf(withId(R.id.btn_holder),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                5)),
                                1),
                        isDisplayed()))
        appCompatImageView3.perform(click())

        // Switch to auto flash
        val appCompatImageView4 = onView(
                allOf(withId(R.id.toggle_flash),
                        childAtPosition(
                                allOf(withId(R.id.btn_holder),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                5)),
                                2),
                        isDisplayed()))
        appCompatImageView4.perform(click())

        // Take a picture with auto flash
        val appCompatImageView5 = onView(
                allOf(withId(R.id.shutter),
                        childAtPosition(
                                allOf(withId(R.id.btn_holder),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                5)),
                                1),
                        isDisplayed()))
        appCompatImageView5.perform(click())

        // Turn off flash
        val appCompatImageView6 = onView(
                allOf(withId(R.id.toggle_flash),
                        childAtPosition(
                                allOf(withId(R.id.btn_holder),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                5)),
                                2),
                        isDisplayed()))
        appCompatImageView6.perform(click())

        // Open settings
        val appCompatImageView7 = onView(
                allOf(withId(R.id.settings),
                        childAtPosition(
                                allOf(withId(R.id.view_holder),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()))
        appCompatImageView7.perform(click())

        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(5000)

        // Open last photo/video preview
        val appCompatImageView8 = onView(
                allOf(withId(R.id.last_photo_video_preview),
                        childAtPosition(
                                allOf(withId(R.id.view_holder),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()))
        appCompatImageView8.perform(click())

        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(5000)

        // Switch to front camera
        val appCompatImageView9 = onView(
                allOf(withId(R.id.toggle_camera),
                        childAtPosition(
                                allOf(withId(R.id.btn_holder),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                5)),
                                0),
                        isDisplayed()))
        appCompatImageView9.perform(click())

        // Take a picture using front camera
        val appCompatImageView10 = onView(
                allOf(withId(R.id.shutter),
                        childAtPosition(
                                allOf(withId(R.id.btn_holder),
                                        childAtPosition(
                                                withId(R.id.view_holder),
                                                5)),
                                1),
                        isDisplayed()))
        appCompatImageView10.perform(click())

        // Open settings
        val appCompatImageView11 = onView(
                allOf(withId(R.id.settings),
                        childAtPosition(
                                allOf(withId(R.id.view_holder),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()))
        appCompatImageView11.perform(click())

        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(5000)

        // Open last photo/video preview using front camera
        val appCompatImageView12 = onView(
                allOf(withId(R.id.last_photo_video_preview),
                        childAtPosition(
                                allOf(withId(R.id.view_holder),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()))
        appCompatImageView12.perform(click())
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
