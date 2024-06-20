package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildTestFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    returnType = "V",
    parameters = listOf(
        "Lcom/google/protos/youtube/api/innertube/LivePlayerConfigOuterClass\$LivePlayerConfig;",
        "Lcom/google/protos/youtube/api/innertube/MediaCommonConfigOuterClass\$MediaCommonConfig;",
        "Lcom/google/protos/youtube/api/innertube/ManifestlessWindowedLiveConfigOuterClass\$ManifestlessWindowedLiveConfig;",
        "I"
    ),
    opcodes = listOf(Opcode.RETURN_VOID),
    customFingerprint = { methodDef, classDef ->
        methodDef.name == "<init>" &&
        classDef.type.endsWith("MediaFetchPlayerConfig;")
    }
)





