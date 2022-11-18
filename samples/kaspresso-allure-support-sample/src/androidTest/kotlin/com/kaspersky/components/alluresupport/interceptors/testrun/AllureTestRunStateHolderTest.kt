package com.kaspersky.components.alluresupport.interceptors.testrun

import org.junit.Test
import java.io.File

class AllureTestRunStateHolderTest {

    @Test
    fun shouldRememberAttachedVideos() {
        val holder = TestRunStateHolder()
        val stubFile = File("stub/path/video.mp4")
        val actualFile = File("actual/path/video.mp4")

        holder.rememberAttachedVideo(stubFile = stubFile, actualFile = actualFile)

        assert(holder.attachedVideos.size == 1)
        assert(holder.attachedVideos.contains(AttachedVideo(
            attachedStubFile = stubFile,
            actualFile = actualFile
        )))
    }
}
