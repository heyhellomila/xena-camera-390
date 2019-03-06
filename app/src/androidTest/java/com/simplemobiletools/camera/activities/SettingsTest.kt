package com.simplemobiletools.camera.activities

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.*
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
class SettingsTest {

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
    fun settingsTest() {
        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(7000)

        //open settings
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

        //view settings
        val appCompatImageView2 = onView(
                allOf(withId(R.id.settings),
                        childAtPosition(
                                allOf(withId(R.id.view_holder),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()))
        appCompatImageView2.perform(click())

        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(7000)

        //toggle a setting to ensure UI is reactive
        val relativeLayout2 = onView(
                allOf(withId(R.id.settings_turn_flash_off_at_startup_holder),
                        childAtPosition(
                                allOf(withId(R.id.settings_holder),
                                        childAtPosition(
                                                withId(R.id.settings_scrollview),
                                                0)),
                                12)))
        relativeLayout2.perform(scrollTo(), click())

        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(2000)

        //Save the settings
        val actionMenuItemView = onView(
                allOf(withId(R.id.action_save), withText("SAVE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        2),
                                1),
                        isDisplayed()))
        actionMenuItemView.perform(click())

        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(2000)

        //Return to camera preview
        val appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()))
        appCompatImageButton.perform(click())
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
