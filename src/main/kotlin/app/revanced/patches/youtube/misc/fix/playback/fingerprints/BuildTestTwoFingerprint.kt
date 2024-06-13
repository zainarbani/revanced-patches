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
        "Ljava/lang/Object;",
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.name == "<init>"
    }
)