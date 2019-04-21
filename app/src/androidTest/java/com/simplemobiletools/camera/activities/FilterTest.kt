package com.simplemobiletools.camera.activities

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.* // ktlint-disable no-wildcard-imports
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.simplemobiletools.camera.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test

@LargeTest
class FilterTest {

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
    fun filterTest() {
        // Added a sleep statement to match the app's execution delay.
        Thread.sleep(5000)
        
        // open settings options holder
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

        // Wait for settings to open.
        Thread.sleep(2000)

        // open effects filter activity
        val appCompatImageView2 = onView(
                allOf(withId(R.id.filterToggle),
                        childAtPosition(
                                allOf(withId(R.id.view_holder),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()))
        appCompatImageView2.perform(click())

        // Wait for filter UI to open.
        Thread.sleep(2000)

        // capture filter menu button and open menu
        val imageButton = onView(
                allOf(withId(R.id.openeffects),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()))
        imageButton.perform(click())

        // Wait for filter menu to open.
        Thread.sleep(2000)

        // select filter and apply to image
        val textView = onView(
                allOf(withId(android.R.id.title), withText("documentary"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()))
        textView.perform(click())

        // Filter is applied to sample picture.
        Thread.sleep(5000)
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
