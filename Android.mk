LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
        $(call all-logtags-files-under, src)

LOCAL_MODULE := settings-logtags

include $(BUILD_STATIC_JAVA_LIBRARY)

# Build the Settings APK
include $(CLEAR_VARS)

LOCAL_JAVA_LIBRARIES := bouncycastle core-oj telephony-common telephony-ext ims-common

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
    android-support-v13 \
    android-support-v7-recyclerview \
    android-support-v7-preference \
    android-support-v7-appcompat \
    android-support-v14-preference \
    jsr305 \
    settings-logtags \
    org.cyanogenmod.platform.internal \
    okhttpcustom \
    okio

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
        $(call all-java-files-under, src) \
        $(call all-java-files-under, ../Venom/src) \
        $(call all-java-files-under, ../../../vendor/viper/colorengine/common/src) \
        $(call all-java-files-under, ../../../vendor/viper/colorengine/themechooser/src) \
        src/com/android/settings/EventLogTags.logtags

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res \
    frameworks/support/v7/preference/res \
    frameworks/support/v14/preference/res \
    frameworks/support/v7/appcompat/res \
    frameworks/support/v7/recyclerview/res \
    packages/apps/Venom/res \
    vendor/viper/colorengine/themechooser/res

# Viper Color Engine
$(warning *** Including Viper Color Engine ***)
LOCAL_RESOURCE_DIR += vendor/viper/colorengine/$(LOCAL_PATH)/res

LOCAL_PACKAGE_NAME := Settings
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_AAPT_FLAGS := --auto-add-overlay \
    --extra-packages android.support.v7.preference:android.support.v14.preference:android.support.v17.preference:android.support.v7.appcompat:android.support.v7.recyclerview:com.viper.venom:com.viper.themechooser

ifneq ($(INCREMENTAL_BUILDS),)
    LOCAL_PROGUARD_ENABLED := disabled
    LOCAL_JACK_ENABLED := incremental
    LOCAL_DX_FLAGS := --multi-dex
    LOCAL_JACK_FLAGS := --multi-dex native
endif

include frameworks/opt/setupwizard/library/common-full-support.mk
include frameworks/base/packages/SettingsLib/common.mk

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    okhttpcustom:libs/okhttp-3.8.1.jar \
    okio:libs/okio-1.13.0.jar

include $(BUILD_MULTI_PREBUILT)

# Use the following include to make our test apk.
ifeq (,$(ONE_SHOT_MAKEFILE))
include $(call all-makefiles-under,$(LOCAL_PATH))
endif
