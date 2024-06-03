package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildVideoPlaybackConnectionFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "Ljava/net/URLConnection;",
    parameters = listOf("Ljava/net/URL;"),
    opcodes = listOf(
        Opcode.SGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.RETURN_OBJECT,
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.name == "openConnection" &&
        methodDef.definingClass.endsWith("ExperimentalCronetEngine;")
   }, 
)
