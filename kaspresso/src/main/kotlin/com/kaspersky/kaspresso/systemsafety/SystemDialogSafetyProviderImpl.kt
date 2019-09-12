package com.kaspersky.kaspresso.systemsafety

import android.support.test.uiautomator.By
import android.support.test.uiautomator.BySelector
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import android.support.test.uiautomator.Until
import android.widget.FrameLayout
import com.kaspersky.kaspresso.logger.UiTestLogger
import java.util.concurrent.TimeUnit

/**
 * The implementation of the [SystemDialogSafetyProvider] interface.
 */
class SystemDialogSafetyProviderImpl(
    private val logger: UiTestLogger,
    private val uiDevice: UiDevice
) : SystemDialogSafetyProvider {

    private val attemptsToSuppress: List<(UiDevice) -> Unit> = listOf(
        { uiDevice -> uiDevice.findObject(UiSelector().resourceId("android:id/button1")).click() },
        { uiDevice -> uiDevice.pressBack() }
    )

    /**
     * Invokes the given [action] and hides the system dialog if the invocation is failed and the system
     * dialog is actually shown via [suppressSystemDialogs] call.
     *
     * @param action the action to invoke.
     *
     * @return the result of [action]'s invocation.
     *
     * @throws Throwable if caught while [action] invocation error is not allowed
     * or if[suppressSystemDialogs] throws an exception.
     */
    @Throws(Throwable::class)
    override fun <T> passSystemDialogs(action: () -> T): T {
        return try {
            action.invoke()
        } catch (error: Throwable) {
            if (isAndroidSystemDetected()) {
                return suppressSystemDialogs(error, action)
            }
            throw error
        }
    }

    /**
     * Attempts to hide the system dialog and re-invokes the given [action].
     */
    @Throws(Throwable::class)
    private fun <R> suppressSystemDialogs(firstError: Throwable, action: () -> R): R {
        logger.i("The suppressing of SystemDialogs starts")
        var cachedError: Throwable = firstError

        attemptsToSuppress.forEachIndexed { index, attemptToSuppress ->
            try {
                logger.i("The suppressing of SystemDialogs on the try #$index starts")

                attemptToSuppress.invoke(uiDevice)
                val result = action.invoke()

                logger.i("The suppressing of SystemDialogs succeeds on the try #$index")
                return result
            } catch (error: Throwable) {
                logger.i("The try #$index of the suppressing of SystemDialogs failed")

                cachedError = error

                if (!isAndroidSystemDetected()) {
                    logger.i(
                        "The try #$index of the suppressing of SystemDialogs failed. " +
                                "The reason is the error is not allowed or " +
                                "android system dialog is suppressed but the error is existing"
                    )
                    throw cachedError
                }
            }
        }

        logger.i("The suppressing of SystemDialogs totally failed")
        throw cachedError
    }

    /**
     * Checks if error is allowed and android system dialogs/windows are overlaying the app.
     */
    private fun isAndroidSystemDetected(): Boolean {
        with(uiDevice) {
            if (isVisible(By.pkg("android").clazz(FrameLayout::class.java))) {
                logger.i("The android system dialog/window was detected")
                return true
            }
        }
        return false
    }

    /**
     * The "isVisible" method with waiting for non-app's elements.
     */
    private fun UiDevice.isVisible(
        selector: BySelector,
        timeMs: Long = TimeUnit.SECONDS.toMillis(1)
    ): Boolean {
        wait(Until.findObject(selector), timeMs)
        return findObject(selector) != null
    }
}