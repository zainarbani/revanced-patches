package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildVideoPlaybackConnectionFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "V",
    parameters = listOf(
        "Lorg/chromium/net/UrlRequest;",
        "Lorg/chromium/net/UrlResponseInfo;",
        "Ljava/nio/ByteBuffer;",
    ),
    opcodes = listOf(
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.INVOKE_STATIC,
        Opcode.RETURN_VOID,
        Opcode.MOVE_EXCEPTION,
        Opcode.INVOKE_STATIC,
        Opcode.THROW,
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.name == "onReadCompleted"
    }, 
//    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
//    returnType = "Lorg/chromium/net/UrlRequest;",
//    parameters = listOf(
//        "L",
//        "Ljava/util/Map;",
//        "[B",
//        "L",
//        "L",
//        "L",
//        "Lorg/chromium/net/UrlRequest\$Callback;"
//    ),
//    opcodes = listOf(
//        Opcode.IGET_OBJECT,
//        Opcode.IGET_OBJECT,
//    ),
//    strings = listOf(
//        "c.cpn_mismatch.",
//    ),
//    customFingerprint = { methodDef, classDef ->
//        methodDef.name == "openConnection" &&
//        classDef.type.endsWith("CronetUrlRequestContext;")
//    }, 
)
