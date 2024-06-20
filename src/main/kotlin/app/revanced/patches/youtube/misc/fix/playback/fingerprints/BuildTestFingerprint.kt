package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildTestFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL or AccessFlags.DECLARED_SYNCHRONIZED,
    returnType = "Ljava/util/Set;",
    parameters = emptyList(),
    opcodes = listOf(Opcode.RETURN_OBJECT),
    customFingerprint = { methodDef, classDef ->
        methodDef.name == "T" &&
        classDef.type.endsWith("PlayerConfigModel;")
    }
)





