package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildVideoPlaybackConnectionFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    returnType = "Lorg/chromium/net/UrlRequest;",
    parameters = listOf(
        "L",
        "Ljava/util/Map;",
        "[B",
        "L",
        "L",
        "L",
        "Lorg/chromium/net/UrlRequest\$Callback;"
    ),
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
    ),
)

//    customFingerprint = { methodDef, classDef ->
//        methodDef.name == "openConnection" &&
//        classDef.type.endsWith("CronetUrlRequestContext;")
//    }, 

