package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object BuildTestTwoFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PROTECTED.value,
    returnType = "Lorg/chromium/net/UrlRequest\$Builder;",
    parameters = listOf("L"),
    opcodes = listOf(
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
    ),
    strings = listOf(
        "Content-Type",
        "Range",
    ),
)