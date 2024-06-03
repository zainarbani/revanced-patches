package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildVideoPlaybackConnectionFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PRIVATE or AccessFlags.FINAL,
    returnType = "Ljava/net/HttpURLConnection;",
    parameters = listOf(
        "Ljava/net/URL;", // videoplayback URL
        "I",
        "[B",
        "J",
        "J",
        "Z",
        "Z",
        "Ljava/util/Map;",
    ),
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
    ),
    strings = listOf(
        "identity",
        "gzip",
    ),
)
