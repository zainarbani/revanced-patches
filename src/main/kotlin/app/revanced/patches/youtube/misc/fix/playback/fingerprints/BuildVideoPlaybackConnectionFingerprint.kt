package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

internal object BuildVideoPlaybackConnectionFingerprint : MethodFingerprint(
    returnType = "Ljava/net/HttpURLConnection;",
    strings = listOf(
        "Range",
        "User-Agent",
        "identity",
        "gzip",
        "Accept-Encoding",
    ),
)
