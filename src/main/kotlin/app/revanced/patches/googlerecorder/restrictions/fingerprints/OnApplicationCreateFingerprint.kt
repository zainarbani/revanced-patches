package app.revanced.patches.googlerecorder.restrictions.fingerprints

import app.revanced.patcher.fingerprint.methodFingerprint

internal val onApplicationCreateFingerprint = methodFingerprint {
    strings("com.google.android.feature.PIXEL_2017_EXPERIENCE")
    custom custom@{ methodDef, classDef ->
        if (methodDef.name != "onCreate") return@custom false

        classDef.endsWith("RecorderApplication;")
    }
}