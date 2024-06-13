package app.revanced.patches.youtube.misc.fix.playback.fingerprints

//import app.revanced.patches.youtube.misc.fix.playback.fingerprints.BuildTestTwoFingerprint.constWideIndex
//import app.revanced.util.indexOfFirstInstruction
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
//import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.dexbacked.instruction.DexBackedInstruction31i
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction


internal object BuildTestTwoFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PROTECTED,
    returnType = "Lorg/chromium/net/UrlRequest\$Builder;",
    parameters = listOf(
        "L"
    ),
    strings = listOf(
        "Content-Type",
        "Range",
    ),
)