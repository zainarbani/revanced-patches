package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal object BuildTestTwoFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    returnType = "V",
    customFingerprint = { methodDef, classDef ->
        methodDef.name == "<init>" &&
        methodDef.parameters.size == 10 &&
        classDef.methods.any { method ->
            method.implementation?.instructions?.any { instruction ->
                instruction.opcode == Opcode.CONST_STRING &&
                instruction.getReference<MethodReference>()?.name == "media3.datasource"
                //(instruction.getLiteral() as? String == "media3.datasource")
            } ?: false
        }
    }
)