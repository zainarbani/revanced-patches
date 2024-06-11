package app.revanced.patches.reddit.customclients.sync.syncforreddit.fix.slink

import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.reddit.customclients.RESOLVE_S_LINK_METHOD
import app.revanced.patches.reddit.customclients.SET_ACCESS_TOKEN_METHOD
import app.revanced.patches.reddit.customclients.fixSLinksPatch
import app.revanced.patches.reddit.customclients.sync.syncforreddit.fix.slink.fingerprints.linkHelperOpenLinkFingerprint
import app.revanced.patches.reddit.customclients.sync.syncforreddit.fix.slink.fingerprints.setAuthorizationHeaderFingerprint
import app.revanced.patches.reddit.customclients.sync.syncforreddit.misc.integrations.integrationsPatch

const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/syncforreddit/FixSLinksPatch;"

@Suppress("unused")
val fixSLinksPatch = fixSLinksPatch(
    integrationsPatch = integrationsPatch,
) {
    compatibleWith(
        "com.laurencedawson.reddit_sync",
        "com.laurencedawson.reddit_sync.pro",
        "com.laurencedawson.reddit_sync.dev",
    )

    val handleNavigationResult by linkHelperOpenLinkFingerprint
    val setAccessTokenResult by setAuthorizationHeaderFingerprint

    execute {
        // region Patch navigation handler.

        handleNavigationResult.mutableMethod.apply {
            val urlRegister = "p3"
            val tempRegister = "v2"

            addInstructionsWithLabels(
                0,
                """
                    invoke-static { $urlRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->$RESOLVE_S_LINK_METHOD
                    move-result $tempRegister
                    if-eqz $tempRegister, :continue
                    return $tempRegister
                """,
                ExternalLabel("continue", getInstruction(0)),
            )
        }

        // endregion

        // region Patch set access token.

        setAccessTokenResult.mutableMethod.addInstruction(
            0,
            "invoke-static { p0 }, $INTEGRATIONS_CLASS_DESCRIPTOR->$SET_ACCESS_TOKEN_METHOD",
        )

        // endregion
    }
}