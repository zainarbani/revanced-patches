package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildShortRecompositionFragmentPeerFingerprint : MethodFingerprint(
//    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "V",
    opcodes = listOf(
        Opcode.CONST_STRING,
    ),
    strings = listOf(
        "cbr",
    ),
//    parameters = listOf(
//        "Landroid/os/Bundle;",
//    ),
//    customFingerprint = { methodDef, _ ->
//        methodDef.definingClass.endsWith("RecompositionFragmentPeer;")
//    }, 
)