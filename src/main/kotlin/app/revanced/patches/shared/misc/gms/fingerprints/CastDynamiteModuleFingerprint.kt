package app.revanced.patches.shared.misc.gms.fingerprints

import com.android.tools.smali.dexlib2.AccessFlags

internal val castDynamiteModuleFingerprint = methodFingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returns("I")
    parameters("L", "I")
    strings(
        "com.google.android.gms.cast.framework.internal.CastDynamiteModuleImpl"
    )
}
