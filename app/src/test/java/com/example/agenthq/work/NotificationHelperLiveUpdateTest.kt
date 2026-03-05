package com.example.agenthq.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.runs
import io.mockk.unmockkConstructor
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the live update notification helpers in [NotificationHelper].
 *
 * Tests here cover the pure-logic surface (ID calculation and channel constants) and
 * the [NotificationManager.cancel] path, all of which run on the JVM without a device.
 *
 * The notification-building paths ([NotificationHelper.postLiveAgentUpdate] and
 * [NotificationHelper.postSessionCompleted]) rely on [androidx.core.app.NotificationCompat]
 * which chains into unmockable Android-SDK internals. Those paths should be validated
 * with an instrumented test or Robolectric if added to the project in the future.
 */
class NotificationHelperLiveUpdateTest {

    private lateinit var manager: NotificationManager
    private lateinit var context: Context

    @Before
    fun setUp() {
        // Stub NotificationChannel so setDescription() doesn't throw "not mocked"
        mockkConstructor(NotificationChannel::class)
        every { anyConstructed<NotificationChannel>().description = any() } just runs

        manager = mockk(relaxed = true)
        context = mockk(relaxed = true) {
            every { getSystemService(Context.NOTIFICATION_SERVICE) } returns manager
        }
    }

    @After
    fun tearDown() {
        unmockkConstructor(NotificationChannel::class)
    }

    // -------------------------------------------------------------------------
    // liveNotificationId() — pure ID-offset calculation
    // -------------------------------------------------------------------------

    @Test
    fun `liveNotificationId offsets session ID by 3000`() {
        assert(NotificationHelper.liveNotificationId(42L) == 3042)
    }

    @Test
    fun `liveNotificationId for session 0 equals the base offset`() {
        assert(NotificationHelper.liveNotificationId(0L) == 3000)
    }

    @Test
    fun `liveNotificationId for large session ID is deterministic`() {
        assert(NotificationHelper.liveNotificationId(999L) == 3999)
    }

    @Test
    fun `liveNotificationId clamps very large session IDs without overflow`() {
        // Long.MAX_VALUE should not cause an ArithmeticException or negative ID
        val id = NotificationHelper.liveNotificationId(Long.MAX_VALUE)
        assert(id >= 3000) { "Expected id >= 3000 but was $id" }
    }

    // -------------------------------------------------------------------------
    // cancelLiveAgentUpdate() — delegates straight to NotificationManager.cancel
    // -------------------------------------------------------------------------

    @Test
    fun `cancelLiveAgentUpdate cancels the correct notification ID`() {
        val helper = NotificationHelper(context)
        helper.cancelLiveAgentUpdate(5L)

        verify(exactly = 1) { manager.cancel(NotificationHelper.liveNotificationId(5L)) }
    }

    @Test
    fun `cancelLiveAgentUpdate for different sessions cancels different IDs`() {
        val helper = NotificationHelper(context)
        helper.cancelLiveAgentUpdate(1L)
        helper.cancelLiveAgentUpdate(2L)

        verify(exactly = 1) { manager.cancel(NotificationHelper.liveNotificationId(1L)) }
        verify(exactly = 1) { manager.cancel(NotificationHelper.liveNotificationId(2L)) }
    }

    // -------------------------------------------------------------------------
    // Channel and extra constants — public API stability contract
    // -------------------------------------------------------------------------

    @Test
    fun `CHANNEL_LIVE_UPDATE constant equals agent_live_update`() {
        assert(NotificationHelper.CHANNEL_LIVE_UPDATE == "agent_live_update")
    }

    @Test
    fun `CHANNEL_ACTIVITY constant is unchanged`() {
        assert(NotificationHelper.CHANNEL_ACTIVITY == "agent_activity")
    }

    @Test
    fun `EXTRA_SESSION_ID constant is unchanged`() {
        assert(NotificationHelper.EXTRA_SESSION_ID == "session_id")
    }
}
