package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildVideoPlaybackConnectionFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PRIVATE or AccessFlags.FINAL or AccessFlags.DECLARED_SYNCHRONIZED,
    returnType = "L",
//    opcodes = listOf(
//        Opcode.IGET_OBJECT,
//        Opcode.IGET_OBJECT,
//    ),
    strings = listOf(
        "c.cpn_mismatch.",
    ),
//    customFingerprint = { methodDef, classDef ->
//        methodDef.name == "openConnection" &&
//        classDef.type.endsWith("CronetUrlRequestContext;")
//   }, 
)
