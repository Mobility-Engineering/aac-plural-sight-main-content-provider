package com.dexcom.sdk.aac_fullcontentapp

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.CoreMatchers.any
import org.hamcrest.Matcher

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class NavigationDrawerActivityTest {
    @get: Rule
    val drawerActivityTestRule =
        ActivityScenarioRule<NavigationDrawerActivity>(NavigationDrawerActivity::class.java)
    @get: Rule
    val noteTestRule =
        ActivityScenarioRule<NoteActivity>(NoteActivity::class.java)
    @Test
    fun nextThroughNotes() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_home))
        onView(withId(R.id.list_items)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                10,
                recyclerClick()
            )
        )

        val notes = DataManager.instance?.notes
        var index = 10
        var note = notes?.get(index)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            run() {

            }
        }, 200000)

        onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(note?.course?.title))
        )
        onView(withId(R.id.text_note_title)).check(matches(
            isDisplayed()))//withText(note?.title)))
        onView(withId(R.id.text_note_text)).check(matches(withText(note?.text)))
    }
    fun recyclerClick(): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return any(View::class.java)
            }

            override fun getDescription(): String {
                return "performing click() on recycler view item"
            }

            override fun perform(uiController: UiController?, view: View) {
                view.performClick()
            }
        }
    }
}