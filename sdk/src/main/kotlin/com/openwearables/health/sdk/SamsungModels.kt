package com.openwearables.health.sdk

/**
 * Internal raw data models used by [SamsungHealthManager].
 * These are Samsung-specific and not exposed via the [HealthDataProvider] interface.
 */

data class HealthDataRecord(
    val uid: String,
    val dataType: String,
    val startTime: Long,
    val endTime: Long?,
    val zoneOffset: String?,
    val dataSource: RawDataSource,
    val device: DeviceInfo,
    val fields: Map<String, Any?>
)

data class RawDataSource(val appId: String?, val deviceId: String?)

data class DeviceInfo(
    val deviceId: String?,
    val manufacturer: String,
    val model: String,
    val name: String,
    val brand: String,
    val product: String,
    val osType: String,
    val osVersion: String,
    val sdkVersion: Int,
    val deviceType: String?,
    val isSourceDevice: Boolean = false
)

/**
 * The source a Samsung record is sent with.
 *
 * Only a device Samsung Health listed is described. A record whose device id is not
 * in that list (a watch since unpaired, a ring not returned by any group) used to be
 * described with the phone's own Build values and typed "phone", so its data read as
 * the handset's. The id is still sent, so the backend keys the unit; its name, model
 * and type are left unsaid rather than guessed. "Unknown" placeholders are unsaid too:
 * the backend would otherwise store the word as a model.
 */
internal fun samsungUnifiedSource(dataSource: RawDataSource, device: DeviceInfo): UnifiedSource {
    fun stated(value: String?): String? = value?.trim()?.takeIf { it.isNotEmpty() && it != "Unknown" }
    if (!device.isSourceDevice) {
        return UnifiedSource(
            appId = dataSource.appId,
            deviceId = dataSource.deviceId,
            deviceName = null,
            deviceManufacturer = null,
            deviceModel = null,
            deviceType = null,
            recordingMethod = null
        )
    }
    val name = stated(device.name)
    val model = stated(device.model)
    return UnifiedSource(
        appId = dataSource.appId,
        deviceId = dataSource.deviceId,
        deviceName = name,
        deviceManufacturer = stated(device.manufacturer),
        deviceModel = model,
        deviceType = DeviceTypeMapper.fromSamsungDeviceType(device.deviceType, name, model),
        recordingMethod = null
    )
}
