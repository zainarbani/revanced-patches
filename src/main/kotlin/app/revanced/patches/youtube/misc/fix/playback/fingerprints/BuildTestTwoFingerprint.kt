package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c

internal object BuildTestTwoFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.STATIC or AccessFlags.CONSTRUCTOR,
    returnType = "V",
    strings = "media3.datasource"
)