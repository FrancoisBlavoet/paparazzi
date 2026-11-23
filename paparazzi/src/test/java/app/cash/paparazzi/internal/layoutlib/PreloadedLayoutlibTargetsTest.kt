package app.cash.paparazzi.internal.layoutlib

import android.widget.TextView
import app.cash.paparazzi.PaparazziTestRule
import com.android.layoutlib.bridge.impl.RenderAction
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/** Runs alone so every patch target loads before the first Paparazzi setup. */
class PreloadedLayoutlibTargetsTest {
  @get:Rule
  val fixture = PaparazziTestRule()

  @Test
  fun patchesTargetsLoadedBeforeFirstRender() {
    val queue = RenderAction.getCurrentContext().sessionInteractiveData.handlerMessageQueue
    assertTrue(queue is DeterministicHandlerMessageQueue)
    fixture.paparazzi.snapshot(TextView(fixture.paparazzi.context).apply { text = "Callbacks" })
  }

  companion object {
    @JvmStatic
    @BeforeClass
    fun preload() {
      listOf(
        "android.view.ViewRootImpl",
        "com.android.layoutlib.bridge.impl.RenderSessionImpl",
        "com.android.layoutlib.bridge.impl.BridgeWindowSession",
        "com.android.layoutlib.bridge.SessionInteractiveData",
        "com.android.internal.policy.PhoneWindow"
      ).forEach { Class.forName(it, false, PreloadedLayoutlibTargetsTest::class.java.classLoader) }
    }
  }
}
