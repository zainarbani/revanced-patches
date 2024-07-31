package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object TestFingerprint : MethodFingerprint(
    "V",
    customFingerprint = custom@{ methodDef, _ ->
        if (methodDef.name != "onReadCompleted") return@custom false
        if (methodDef.implementation!!.instructions.count() != 11) return@custom false
    }
)
