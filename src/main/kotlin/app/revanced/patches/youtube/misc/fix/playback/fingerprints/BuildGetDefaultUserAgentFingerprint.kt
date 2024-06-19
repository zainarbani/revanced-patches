package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildGetDefaultUserAgentFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    returnType = "Landroid/content/Context;",
    parameters = listOf("Ljava/lang/String;"),
    opcodes = listOf(
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.RETURN_OBJECT
    ),
    strings = listOf(
        "Cannot determine package version",
        " (Linux; U; Android ",
        "; ",
        "; ",
        "; Build/",
        ";"
    )
)