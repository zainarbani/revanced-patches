package app.revanced.patches.youtube.misc.fix.playback

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.all.misc.resources.AddResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.PreferenceScreen
import app.revanced.patches.shared.misc.settings.preference.SwitchPreference
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.BuildPlayerRequestResponseFingerprint
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.util.resultOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch(
    name = "Spoof stream",
    description = "Spoofs streaming data to allow video playback.",
    dependencies = [
        SettingsPatch::class,
        AddResourcesPatch::class,
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "19.16.39",
            ],
        ),
    ],
)
object SpoofStreamPatch : BytecodePatch(
    setOf(
        // Player request response.
        BuildPlayerRequestResponseFingerprint,
    ),
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/youtube/patches/spoof/SpoofStreamPatch;"

    override fun execute(context: BytecodeContext) {
        AddResourcesPatch(this::class)

        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            PreferenceScreen(
                key = "revanced_spoof_stream_screen",
                sorting = PreferenceScreen.Sorting.UNSORTED,
                preferences = setOf(
                    SwitchPreference("revanced_spoof_stream"),
                ),
            ),
        )

        // region Change streaming data by injecting player request response.

        BuildPlayerRequestResponseFingerprint.resultOrThrow().let {
            it.mutableMethod.apply {
                val moveIndex = it.scanResult.patternScanResult!!.endIndex
                val moveRegister = getInstruction<OneRegisterInstruction>(moveIndex).registerA
                val freeRegister = getInstruction<FiveRegisterInstruction>(moveIndex - 1).registerC

                addInstructions(
                    moveIndex + 1,
                    """
                        invoke-virtual { p1 }, Lorg/chromium/net/UrlResponseInfo;->getUrl()Ljava/lang/String;
                        move-result-object v$freeRegister
                        invoke-static { v$moveRegister, v$freeRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->getStreamingData([BLjava/lang/String;)[B
                        move-result-object v$moveRegister
                    """
                )
            }
        }

        // endregion
    }
}
