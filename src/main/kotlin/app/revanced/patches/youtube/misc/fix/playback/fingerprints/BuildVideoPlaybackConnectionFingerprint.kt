package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object BuildVideoPlaybackConnectionFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PRIVATE or AccessFlags.FINAL,
    returnType = "Ljava/net/HttpURLConnection;",
    parameters = listOf(
        "Ljava/net/URL;", // videoplayback URL
        "I",
    ),
    strings = listOf(
        "Range",
        "User-Agent",
        "identity",
        "gzip",
        "Accept-Encoding",
    ),
)
