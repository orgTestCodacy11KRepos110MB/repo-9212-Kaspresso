[kaspresso](../../index.md) / [com.kaspersky.kaspresso.interceptors.watcher.testcase.impl.screenshot](../index.md) / [TestRunnerScreenshotWatcherInterceptor](index.md) / [onAfterSectionFinishedFailed](./on-after-section-finished-failed.md)

# onAfterSectionFinishedFailed

`fun onAfterSectionFinishedFailed(testInfo: `[`TestInfo`](../../com.kaspersky.kaspresso.testcases.models.info/-test-info/index.md)`, throwable: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Overrides [TestRunWatcherInterceptor.onAfterSectionFinishedFailed](../../com.kaspersky.kaspresso.interceptors.watcher.testcase/-test-run-watcher-interceptor/on-after-section-finished-failed.md)

Takes a screenshot of the screen on which the "after" section failed.

### Parameters

`testInfo` - the test info to use in screenshots name.

`throwable` - the error occurred to use in screenshots name.