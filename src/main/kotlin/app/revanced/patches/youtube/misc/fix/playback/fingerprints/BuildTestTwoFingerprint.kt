package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildTestTwoFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PROTECTED.value,
    returnType = "Lorg/chromium/net/UrlRequest\$Builder;",
    parameters = listOf("L"),
    opcodes = listOf(
        Opcode.IGET_OBJECT, // stream uri.
        Opcode.INVOKE_VIRTUAL,
        Opcode.NEW_INSTANCE,
        Opcode.INVOKE_DIRECT,
        Opcode.INVOKE_INTERFACE,
        Opcode.INVOKE_INTERFACE, // headers map.
    ),
    strings = listOf(
        "Content-Type",
        "Range",
    ),
)