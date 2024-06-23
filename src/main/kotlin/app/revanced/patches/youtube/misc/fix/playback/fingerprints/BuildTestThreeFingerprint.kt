package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildTestThreeFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    returnType = "V",
    parameters = listOf(
        "Ljava/lang/String;",
        "Ljava/lang/String;",
        "L",
        "L",
        "L",
        "Landroid/content/Context;"
    ),
    strings = listOf("cbr"),
    customFingerprint = { methodDef, _ -> methodDef.name == "<init>" }
)
