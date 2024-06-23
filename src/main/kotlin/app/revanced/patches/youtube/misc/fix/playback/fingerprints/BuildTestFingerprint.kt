package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildTestFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PROTECTED or AccessFlags.FINAL,
    returnType = "V",
    parameters = emptyList(),
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.GOTO
    ),
    strings = listOf(
        "E",
        "0000000000000000000000000000000000000000000000000000000000000000"
    )
    //opcodes = listOf(Opcode.RETURN_OBJECT),
    //customFingerprint = { methodDef, classDef ->
    //    methodDef.name == "<init>" &&
    //    classDef.type.endsWith("FormatStreamModel;")
    //}
)





