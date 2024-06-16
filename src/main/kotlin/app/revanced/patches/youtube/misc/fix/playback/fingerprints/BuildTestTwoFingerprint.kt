package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildTestTwoFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    returnType = "V",
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
    opcodes = listOf(
        Opcode.IPUT_OBJECT,
        Opcode.IPUT_WIDE
    ),
    customFingerprint = { methodDef, classDef ->
        methodDef.name == "<init>" &&
        classDef.methods.any { method ->
            method.implementation?.instructions?.any { instruction ->
                instruction.toString().contains("media3.datasource")
            }
        }
    }
) 