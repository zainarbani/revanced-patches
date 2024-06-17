package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.StringReference

internal object BuildTestTwoFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    returnType = "V",
    customFingerprint = { methodDef, classDef ->
        methodDef.name == "<init>" &&
        methodDef.parameters.size == 10 &&
        classDef.methods.any { method ->
            method.implementation?.instructions?.any { instruction ->
                if (instruction.opcode != Opcode.CONST_STRING) return@any false
                
                val reference = instruction as StringReference
                
                if (reference.string != "media3.datasource") return@any false
                true
            } ?: false
        }
    }
)