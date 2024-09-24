package app.revanced.patches.youtube.layout.buttons.player.flyout

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.layout.buttons.player.flyout.fingerprints.SleepTimerFeatureFingerprint
import app.revanced.patches.youtube.layout.buttons.player.flyout.fingerprints.SleepTimerFlyoutMenuFeatureFingerprint
import app.revanced.util.resultOrThrow

@Patch(
    name = "Remove sleep timer menu restrictions",
    description = "Show sleep timer options in player flyout menu.",
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "19.30.39", // Only enable this patch on 19.30+ for now.
            ]
        )
    ]
)
@Suppress("unused")
object SleepTimerMenuPatch : BytecodePatch(
    setOf(SleepTimerFeatureFingerprint, SleepTimerFlyoutMenuFeatureFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        SleepTimerFeatureFingerprint.resultOrThrow().mutableMethod.addInstructions(
            0,
            """
                const/4 v0, 0x1
                return v0
            """
        )

        // Feature is experimental, and this fingerprint may change in the future.
        SleepTimerFlyoutMenuFeatureFingerprint.resultOrThrow().let {
            it.mutableMethod.apply {
                val targetIndex = it.scanResult.patternScanResult!!.startIndex

                addInstruction(
                    targetIndex, "const/4 p1, 0x1"
                )
            }
        }
    }
}