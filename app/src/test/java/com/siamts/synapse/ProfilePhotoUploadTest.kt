package com.siamts.synapse

import com.siamts.synapse.data.remote.RetrofitClient
import com.siamts.synapse.data.repository.ProfileRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileOutputStream

class ProfilePhotoUploadTest {

    private lateinit var profileRepository: ProfileRepositoryImpl

    @Before
    fun setUp() {
        // Use local host when running JVM unit tests
        RetrofitClient.updateBaseUrl("http://localhost/synapse_backend/")
        profileRepository = ProfileRepositoryImpl()
    }

    @Test
    fun testFormatPhotoUrl_resolvesRelativeAndLocalhost() {
        // Relative path
        val relative = "upload/profile/test.jpg"
        val resolvedRelative = RetrofitClient.formatPhotoUrl(relative)
        assertEquals("http://localhost/synapse_backend/upload/profile/test.jpg", resolvedRelative)

        // Null / blank
        assertNull(RetrofitClient.formatPhotoUrl(null))
        assertNull(RetrofitClient.formatPhotoUrl(""))

        // Content URI preserved
        val contentUri = "content://media/external/images/media/42"
        assertEquals(contentUri, RetrofitClient.formatPhotoUrl(contentUri))

        // File URI preserved
        val fileUri = "file:///data/user/0/com.siamts.synapse/files/avatar.jpg"
        assertEquals(fileUri, RetrofitClient.formatPhotoUrl(fileUri))
    }

    @Test
    fun testGetProfile_returnsSavedProfilePhoto() = runBlocking {
        val result = profileRepository.getProfile(userId = 99999, email = "test_robot_99999@synapse.ai")
        assertTrue("Expected getProfile to succeed: ${result.exceptionOrNull()?.message}", result.isSuccess)

        val profile = result.getOrNull()
        assertNotNull(profile)
        assertEquals(99999, profile?.id)
        assertNotNull(profile?.name)
        assertNotNull(profile?.email)
    }

    @Test
    fun testUploadProfilePhoto_uploadsAndUpdatesDatabase() = runBlocking {
        // Create 1x1 dummy test JPEG file
        val tempFile = File.createTempFile("test_upload_", ".jpg")
        try {
            FileOutputStream(tempFile).use { out ->
                // Minimal 1x1 JPEG bytes
                val dummyBytes = byteArrayOf(
                    0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte(), 0x00.toByte(),
                    0x10.toByte(), 0x4A.toByte(), 0x46.toByte(), 0x49.toByte(), 0x46.toByte(),
                    0x00.toByte(), 0x01.toByte(), 0x01.toByte(), 0x00.toByte(), 0x00.toByte(),
                    0x01.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte(), 0x00.toByte(),
                    0xFF.toByte(), 0xDB.toByte(), 0x00.toByte(), 0x43.toByte(), 0x00.toByte(),
                    0x08.toByte(), 0x06.toByte(), 0x06.toByte(), 0x07.toByte(), 0x06.toByte(),
                    0xFF.toByte(), 0xC0.toByte(), 0x00.toByte(), 0x0B.toByte(), 0x08.toByte(),
                    0x00.toByte(), 0x01.toByte(), 0x00.toByte(), 0x01.toByte(), 0x01.toByte(),
                    0x01.toByte(), 0x11.toByte(), 0x00.toByte(), 0xFF.toByte(), 0xC4.toByte(),
                    0x00.toByte(), 0x14.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte(),
                    0xFF.toByte(), 0xDA.toByte(), 0x00.toByte(), 0x08.toByte(), 0x01.toByte(),
                    0x01.toByte(), 0x00.toByte(), 0x00.toByte(), 0x3F.toByte(), 0x00.toByte(),
                    0x37.toByte(), 0xFF.toByte(), 0xD9.toByte()
                )
                out.write(dummyBytes)
            }

            val result = profileRepository.uploadProfilePhoto(
                userId = 99999,
                email = "test_robot_99999@synapse.ai",
                photoFile = tempFile,
                name = "Test Bot",
                phone = "+91 98765-43210",
                bio = "Test Robot Account for automated unit tests"
            )

            assertTrue("Expected upload to succeed: ${result.exceptionOrNull()?.message}", result.isSuccess)
            val updated = result.getOrNull()
            assertNotNull(updated)
            assertNotNull("Photo URL should not be null", updated?.photoUrl)
            assertTrue("Photo path should contain avatar or upload path", updated?.profilePhoto?.contains("avatars") == true || updated?.profilePhoto?.contains("upload") == true)
        } finally {
            tempFile.delete()
        }
    }
}

