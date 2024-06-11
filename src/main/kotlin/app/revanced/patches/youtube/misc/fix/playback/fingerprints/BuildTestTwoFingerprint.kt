package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patches.youtube.misc.fix.playback.fingerprints.BuildTestTwoFingerprint.constWideIndex
import app.revanced.util.indexOfFirstInstruction
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method

internal object BuildTestTwoFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "Z",
    customFingerprint = { methodDef, _ ->
        constWideIndex(methodDef) >= 0
    }
) {
    fun constWideIndex(methodDef: Method) =
        methodDef.indexOfFirstInstruction {
            opcode == Opcode.CONST_WIDE_32 &&
            literal == 0x2b46463L
        }
}