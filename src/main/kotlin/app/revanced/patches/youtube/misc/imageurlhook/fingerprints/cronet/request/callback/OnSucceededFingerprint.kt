package app.revanced.patches.youtube.misc.imageurlhook.fingerprints.cronet.request.callback

import app.revanced.patcher.fingerprint.methodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal val onSucceededFingerprint = methodFingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("Lorg/chromium/net/UrlRequest;", "Lorg/chromium/net/UrlResponseInfo;")
    custom { method, _ ->
        method.name == "onSucceeded"
    }
}
