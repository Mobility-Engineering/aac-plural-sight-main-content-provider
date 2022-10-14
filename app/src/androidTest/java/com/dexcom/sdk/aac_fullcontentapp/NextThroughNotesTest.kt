package com.dexcom.sdk.aac_fullcontentapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.math.absoluteValue

@RunWith(JUnit4::class)
class NoteActivityViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    lateinit var viewModel: NoteActivityViewModel

    @Before
    fun init() {
        viewModel = NoteActivityViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun validateViewModelWhenObserved() {
        val observer = Observer<Int> {}
        try {
            viewModel.courseIndex.observeForever(observer)
            //viewModel.courseIndex = 537
            viewModel.courseIndex.value = 537
            val value = viewModel.courseIndex.value
            assertThat(value, not(nullValue()))

        } finally {
            viewModel.courseIndex.removeObserver(observer)
        }
    }
}


