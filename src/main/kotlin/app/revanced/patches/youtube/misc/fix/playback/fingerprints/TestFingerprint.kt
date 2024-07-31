package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object TestFingerprint : MethodFingerprint(
    "V",
    customFingerprint = { methodDef, _ ->
        methodDef.name == "onReadCompleted" &&
        methodDef.implementation!!.instructions.count() == 11
    }
)
