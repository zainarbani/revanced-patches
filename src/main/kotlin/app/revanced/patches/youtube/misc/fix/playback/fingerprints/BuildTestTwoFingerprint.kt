package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildTestTwoFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PRIVATE or AccessFlags.FINAL,
    returnType = "Ljava/net/HttpURLConnection;",
    parameters = listOf(
        "Ljava/net/URL;",
        "Ljava/lang/String;"
    ),
    strings = listOf("Failed to get heartbeats header")
)
