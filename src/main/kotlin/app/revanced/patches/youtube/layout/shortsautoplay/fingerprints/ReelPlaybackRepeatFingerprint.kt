package app.revanced.patches.youtube.layout.shortsautoplay.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint

internal object ReelPlaybackRepeatFingerprint : MethodFingerprint(
    parameters = listOf("L"),
    returnType = "V",
    strings = listOf("YoutubePlayerState is in throwing an Error.")
)
