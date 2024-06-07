package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildVideoStreamingDataFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    returnType = "Z",
    parameters = listOf(
        "Lcom/google/protos/youtube/api/innertube/StreamingDataOuterClass\$StreamingData;",
        "Lcom/google/android/libraries/youtube/innertube/model/media/PlayerConfigModel;",
    ),
    customFingerprint = { _, classDef ->
        classDef.type.endsWith("VideoStreamingData;")
    }, 
)