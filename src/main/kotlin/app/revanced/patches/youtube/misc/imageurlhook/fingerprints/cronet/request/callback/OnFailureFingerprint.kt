package app.revanced.patches.youtube.misc.imageurlhook.fingerprints.cronet.request.callback

import app.revanced.patcher.fingerprint.methodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal val onFailureFingerprint = methodFingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("Lorg/chromium/net/UrlRequest;", "Lorg/chromium/net/UrlResponseInfo;", "Lorg/chromium/net/CronetException;")
    custom { method, _ ->
        method.name == "onFailed"
    }
}
