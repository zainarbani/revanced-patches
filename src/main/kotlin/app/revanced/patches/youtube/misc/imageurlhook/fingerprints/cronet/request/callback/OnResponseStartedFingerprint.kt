package app.revanced.patches.youtube.misc.imageurlhook.fingerprints.cronet.request.callback

import app.revanced.patcher.fingerprint.methodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

// Acts as a parent fingerprint.
internal val onResponseStartedFingerprint = methodFingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("Lorg/chromium/net/UrlRequest;", "Lorg/chromium/net/UrlResponseInfo;")
    strings(
        "Content-Length",
        "Content-Type",
        "identity",
        "application/x-protobuf",
    )
    custom { method, _ ->
        method.name == "onResponseStarted"
    }
}
