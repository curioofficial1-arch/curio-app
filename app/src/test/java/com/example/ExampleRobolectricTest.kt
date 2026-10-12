package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.sample.InitialCuriosities
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun readStringFromContext() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Curio", appName)
  }

  @Test
  fun initialCuriositiesContainRequestedTopics() {
    val sampleList = InitialCuriosities.sampleList
    assertTrue(sampleList.isNotEmpty())
    val titles = sampleList.map { it.title.lowercase() }
    assertTrue(titles.any { it.contains("brain") && it.contains("dying") })
    assertTrue(titles.any { it.contains("manchester city") })
  }
}
