package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object TestFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    returnType = "V",
    //opcodes = listOf(
   //     Opcode.MOVE_OBJECT
   //     Opcode.MOVE_WIDE
    //    Opcode.MOVE_OBJECT_FROM16,
 //       Opcode.MOVE_WIDE_FROM16,
  //      Opcode.INVOKE_DIRECT
  //  ),
    customFingerprint = custom@{ methodDef, _ ->
        if (methodDef.name != "<init>") return@custom false
        
        val parameterTypes = methodDef.parameterTypes
        
        if (parameterTypes.size != 10) return@custom false
        if (parameterTypes[0] != "Landroid/net/Uri;") return@custom false
        if (parameterTypes[1] != "J") return@custom false
        return@custom true
    }
)