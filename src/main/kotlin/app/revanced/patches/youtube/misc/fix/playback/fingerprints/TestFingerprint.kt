package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object TestFingerprint : MethodFingerprint(
//    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
//    returnType = "V",
    parameters = listOf(
        "Landroid/net/Uri;",
        "J",
        "I",
        "[B",
        "Ljava/util/Map;",
        "J",
        "J",
        "Ljava/lang/String;",
        "I",
        "Ljava/lang/Object;"
    ),
    //opcodes = listOf(
    //    Opcode.MOVE_OBJECT_FROM16,
   //     Opcode.MOVE_WIDE_FROM16,
  //      Opcode.INVOKE_DIRECT
  //  ),
//    customFingerprint = { methodDef, _ ->
//        methodDef.name == "<init>" && methodDef.annotations.isEmpty()
//    }
)