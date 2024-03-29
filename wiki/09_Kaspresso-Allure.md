# Kaspresso-allure support

## _What's new_

### 1.3.0
In the new **1.3.0** Kaspresso release the [**allure-framework**](https://github.com/allure-framework/allure-kotlin) support was added. Now it is very easy to generate pretty test reports using both Kaspresso and Allure frameworks.

In this release, the file-managing classes family that is responsible for providing files for screenshots and logs has been refactored for better usage and extensibility. This change has affected the old classes that are deprecated now (see package com.kaspersky.kaspresso.files). Usage example: [**CustomizedSimpleTest**](../samples/kaspresso-sample/src/androidTest/kotlin/com/kaspersky/kaspressample/simple_tests/CustomizedSimpleTest.kt).

Also, the following interceptors were added:
1. [**VideoRecordingInterceptor**](../kaspresso/src/main/kotlin/com/kaspersky/kaspresso/interceptors/watcher/testcase/impl/video/VideoRecordingInterceptor.kt). Tests video recording interceptor (please not that it was fully tested on emulators with android api 29 and older).
2. [**DumpViewsInterceptor**](../kaspresso/src/main/kotlin/com/kaspersky/kaspresso/interceptors/watcher/testcase/impl/views/DumpViewsInterceptor.kt). Interceptor that dumps XML-representation of view hierarchy in case of a test failure.

In the package [**com.kaspersky.components.alluresupport.interceptors**](../allure-support/src/main/kotlin/com/kaspersky/components/alluresupport/interceptors), there are special Kaspresso interceptors helping to link and process files for Allure-report.

### 1.4.3

Video recordings support brought back. Kaspresso 1.4.2 release used an updated allure dependency which brought breaking change:
allure changed default report directory to /data/data/your.package.com/files/allure-results. This raised another issue - allure 
could no longer attach video files (see this [issue](https://issuetracker.google.com/issues/258277873)).

Kaspresso 1.4.3 release introduces a solution that allows to both store allure report on external memory and attach videos.
To use updated allure support, please use `Kaspresso.Builder.withForcedAllureSupport` builder and add kaspresso runner to 
your build.gradle.

```groovy
android {
    defaultConfig {
        //...
        testInstrumentationRunner = "com.kaspersky.kaspresso.runner.KaspressoRunner"
    }
    //...
}
```

## _How to use_
First of all, add the following Gradle dependency and Kaspresso runner to your project's gradle file to include **allure-support** Kaspresso module:
```groovy
android {
    defaultConfig {
        //...    
        testInstrumentationRunner = "com.kaspersky.kaspresso.runner.KaspressoRunner"
    }
    //...
}

dependencies {
    //...
    androidTestImplementation "com.kaspersky.android-components:kaspresso-allure-support:<latest_version>"
}
```
Next, use special [**withAllureSupport**](../allure-support/src/main/kotlin/com/kaspersky/components/alluresupport/AllureSupportKaspressoBuilder.kt) function in your TestCase constructor or in your TestCaseRule to turn on all available Allure-supporting interceptors:
```kotlin
class AllureSupportTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withAllureSupport()
) {
```
If you want to specify the parameters or add more interceptors you can use [**addAllureSupport**](../allure-support/src/main/kotlin/com/kaspersky/components/alluresupport/AllureSupportKaspressoBuilder.kt) function:
```kotlin
class AllureSupportCustomizeTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.simple(
        customize = {
            videoParams = VideoParams(bitRate = 10_000_000)
            screenshotParams = ScreenshotParams(quality = 1)
        }
    ).addAllureSupport().apply {
        testRunWatcherInterceptors.apply {
            add(object : TestRunWatcherInterceptor {
                override fun onTestFinished(testInfo: TestInfo, success: Boolean) {
                    viewHierarchyDumper.dumpAndApply("ViewHierarchy") { attachViewHierarchyToAllureReport() }
                }
            })
        }
    }
) {
...
}
```
If you don't need all of these interceptors providing by [**withAllureSupport**](../allure-support/src/main/kotlin/com/kaspersky/components/alluresupport/AllureSupportKaspressoBuilder.kt) and [**addAllureSupport**](../allure-support/src/main/kotlin/com/kaspersky/components/alluresupport/AllureSupportKaspressoBuilder.kt) functions then you may add only interceptors that you prefer. But please note that [**AllureMapperStepInterceptor.kt**](../allure-support/src/main/kotlin/com/kaspersky/components/alluresupport/interceptors/step/AllureMapperStepInterceptor.kt) is mandatory for Allure support work. For example, if you don't need videos and view hierarchies after test failures then you can do something like:
```kotlin
class AllureSupportCustomizeTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.simple().apply {
        stepWatcherInterceptors.addAll(
            listOf(
                ScreenshotStepInterceptor(screenshots),
                AllureMapperStepInterceptor()
            )
        )
        testRunWatcherInterceptors.addAll(
            listOf(
                DumpLogcatTestInterceptor(logcatDumper),
                ScreenshotTestInterceptor(screenshots),
            )
        )
    }
) {
...
}
```
[**kaspresso-allure-support-sample**](../samples/kaspresso-allure-support-sample/src/androidTest/kotlin/com/kaspersky/kaspresso/alluresupport/sample) is available to watch, to launch and to experiment with all of this staff.

## _Watch result_
So you added the list of needed Allure-supporting interceptors to your Kaspresso configuration and launched the test. After the test finishes there will be **sdcard/allure-results** dir created on the device with all the files processed to be included to Allure-report.

This dir should be moved from the device to the host machine which will do generate the report.

Assuming your package is com.example
```
adb exec-out sh -c "cd /sdcard/Documents && tar cf - allure-results" > ~/allure-results.tar
```
`exec-out` runs passed command and returns result as a file which we save by `> allure-results.tar` in the end 

If there are few devices connected to your host you should specify the needed device id. To watch the list of connected devices you can call:
```
adb devices
```
The output will be something like:
```
List of devices attached
CLCDU18508004769	device
emulator-5554	device
```
Select the needed device and call:
```
adb -s emulator-5554 exec-out sh -c "cd /sdcard/Documents && tar cf - allure-results" > ~/allure-results.tar
```
And that's it, the **allure-results** archive with all the test resources is now at your home directory.

Now, we want to generate and watch the report. The Allure server must be installed on our machine for this. To find out how to do it with all the details please follow the [**Allure docs**](https://docs.qameta.io/allure/).

For example to install Allure server on MacOS we can use the following command:
```
brew install allure
```
Now we are ready to generate and watch the report, just call:
```
tar -xvf allure-results.tar
allure serve ~/allure-results
```
Next, the Allure server generates the html-page representing the report and puts it to temp dir in your system. You will see the report opening in the new tab in your browser (the tab is opening automatically).

If you want to save the generated html-report to a specific dir for future use you can just call:
```
allure generate -o ~/kaspresso-allure-report /Users/username/Desktop/allure-results
```
And to watch it then in your browser you just call:
```
allure open ~/kaspresso-allure-report
```
After all of this actions you see something like:
![](https://habrastorage.org/webt/9e/i1/ks/9ei1ks9txbqzquyk5egywvqxj6k.png)

Details for succeeded test:
![](https://habrastorage.org/webt/tq/t7/ch/tqt7chcdczrgduhoukqhx1ertfc.png)

Details for failed test:
![](https://habrastorage.org/webt/z_/ml/bj/z_mlbjspdd8uvkw4t3cafh6-g6k.png)

## _Details that you need to know_

Kaspresso expects to find allure report in default **allure-results** directory. If you configured allure report directory name
through `allure.properties`, please, use default allure report dir name.

By default, Kaspresso-Allure introduces additional timeouts to assure the correctness of a Video recording as much as possible. To summarize, these timeouts increase a test execution time by 5 seconds.
You are free to change these values by customizing `videoParams` in `Kaspresso.Builder`. See the example above.

### _How kaspresso attaches videos to allure report_

To support storage restrictions allure saves reports to package private directory e.g. /data/data/your.package.name/files/allure-report whereas screen recordings are created by calling "screenrecord" utility through adb shell which is unable to
save video files to package private directories (see this [issue](https://issuetracker.google.com/issues/258277873)).
Because report created by one user (your package), but videos are created by another one leads to inability to attach videos 
to report because your package has no rights on video files.

This fact makes implementing screen recordings attachments rather tedious. Here's kaspresso approach:
1. During the test we attach a stub video file to the report. This stub is placed in /data/data
2. Once the test is finished we force allure to save the report 
3. Kaspresso parses the report and saves file with names generated by allure (when you attach a file to report allure renames it with
UUID and saves it's initial name in report)
4. The report is moved to the external storage (/sdcard)
5. Kaspresso calls adb shell to move the actual files to allure report dir to replace the stubs

![](img/allure_report_workaround.svg)
