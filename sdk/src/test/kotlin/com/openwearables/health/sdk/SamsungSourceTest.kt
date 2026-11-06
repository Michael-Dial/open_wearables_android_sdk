package com.openwearables.health.sdk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SamsungSourceTest {

    private fun device(
        id: String?,
        name: String,
        model: String,
        manufacturer: String,
        type: String?,
        isSourceDevice: Boolean,
    ) = DeviceInfo(
        deviceId = id,
        manufacturer = manufacturer,
        model = model,
        name = name,
        brand = manufacturer,
        product = model,
        osType = "Android",
        osVersion = "",
        sdkVersion = 0,
        deviceType = type,
        isSourceDevice = isSourceDevice,
    )

    @Test
    fun aListedWatchIsDescribed() {
        val s = samsungUnifiedSource(
            RawDataSource("com.sec.android.app.shealth", "w-1"),
            device("w-1", "Galaxy Watch7", "SM-L315F", "Samsung", "WATCH", isSourceDevice = true),
        )
        assertEquals("w-1", s.deviceId)
        assertEquals("Galaxy Watch7", s.deviceName)
        assertEquals("SM-L315F", s.deviceModel)
        assertEquals("Samsung", s.deviceManufacturer)
        assertEquals("watch", s.deviceType)
    }

    /** An unlisted device was described as the phone (its Build values, typed "phone"). */
    @Test
    fun anUnlistedDeviceKeepsItsIdAndIsNotDescribedAsThePhone() {
        val s = samsungUnifiedSource(
            RawDataSource("com.sec.android.app.shealth", "w-old"),
            device("w-old", "e3q", "SM-S928B", "samsung", "MOBILE", isSourceDevice = false),
        )
        assertEquals("w-old", s.deviceId)
        assertEquals("com.sec.android.app.shealth", s.appId)
        assertNull(s.deviceName)
        assertNull(s.deviceModel)
        assertNull(s.deviceManufacturer)
        assertNull(s.deviceType)
    }

    @Test
    fun unknownPlaceholdersAreNotSentAsValues() {
        val s = samsungUnifiedSource(
            RawDataSource(null, "r-1"),
            device("r-1", "Unknown", "Unknown", "Unknown", "RING", isSourceDevice = true),
        )
        assertNull(s.deviceName)
        assertNull(s.deviceModel)
        assertNull(s.deviceManufacturer)
        assertEquals("ring", s.deviceType)
    }
}
