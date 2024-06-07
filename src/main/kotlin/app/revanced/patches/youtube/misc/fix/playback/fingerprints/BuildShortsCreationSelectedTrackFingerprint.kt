package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildShortsCreationSelectedTrackFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "Ljava/lang/Object;",
    parameters = listOf(
        "Ljava/lang/Object;",
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.name == "apply" &&
        methodDef.definingClass.endsWith("ShortsCreationSelectedTrack;")
    }, 
)