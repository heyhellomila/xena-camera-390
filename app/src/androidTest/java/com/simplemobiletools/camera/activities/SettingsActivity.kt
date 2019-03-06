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
class SettingsActivity {

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
    fun settingsActivity() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(5000)

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

        val relativeLayout = onView(
                allOf(withId(R.id.settings_avoid_whats_new_holder),
                        childAtPosition(
                                allOf(withId(R.id.settings_holder),
                                        childAtPosition(
                                                withId(R.id.settings_scrollview),
                                                0)),
                                3)))
        relativeLayout.perform(scrollTo(), click())

        val relativeLayout2 = onView(
                allOf(withId(R.id.settings_photo_quality_holder),
                        childAtPosition(
                                allOf(withId(R.id.settings_holder),
                                        childAtPosition(
                                                withId(R.id.settings_scrollview),
                                                0)),
                                19)))
        relativeLayout2.perform(scrollTo(), click())

        val relativeLayout3 = onView(
                allOf(withId(R.id.settings_photo_quality_holder),
                        childAtPosition(
                                allOf(withId(R.id.settings_holder),
                                        childAtPosition(
                                                withId(R.id.settings_scrollview),
                                                0)),
                                19)))
        relativeLayout3.perform(scrollTo(), click())

        val myCompatRadioButton = onView(
                allOf(withText("90%"),
                        childAtPosition(
                                allOf(withId(R.id.dialog_radio_group),
                                        childAtPosition(
                                                withId(R.id.dialog_radio_holder),
                                                0)),
                                2)))
        myCompatRadioButton.perform(scrollTo(), click())

        val myCompatRadioButton2 = onView(
                allOf(withText("90%"),
                        childAtPosition(
                                allOf(withId(R.id.dialog_radio_group),
                                        childAtPosition(
                                                withId(R.id.dialog_radio_holder),
                                                0)),
                                2)))
        myCompatRadioButton2.perform(scrollTo(), click())

        val relativeLayout4 = onView(
                allOf(withId(R.id.settings_turn_flash_off_at_startup_holder),
                        childAtPosition(
                                allOf(withId(R.id.settings_holder),
                                        childAtPosition(
                                                withId(R.id.settings_scrollview),
                                                0)),
                                12)))
        relativeLayout4.perform(scrollTo(), click())

        val actionMenuItemView = onView(
                allOf(withId(R.id.action_save), withText("SAVE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        2),
                                1),
                        isDisplayed()))
        actionMenuItemView.perform(click())

        val actionMenuItemView2 = onView(
                allOf(withId(R.id.action_open), withText("OPEN"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        2),
                                0),
                        isDisplayed()))
        actionMenuItemView2.perform(click())

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
