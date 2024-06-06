package app.revanced.patches.youtube.misc.fix.playback.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object BuildPlayerRequestFingerprint : MethodFingerprint(
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
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    returnType = "V",
    parameters = listOf(
        "L",
        "Ljava/lang/String;",
        "Z",
        "L",
    ),
    opcodes = listOf(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
    ),
    customFingerprint = { methodDef, classDef ->
        methodDef.name == "<init>" &&
        classDef.type.endsWith("FormatStreamModel;")
    },
//    strings = listOf(
//        "Content-Type",
//        "application/x-www-form-urlencoded",
//        "Content-Length",
//    ),
)