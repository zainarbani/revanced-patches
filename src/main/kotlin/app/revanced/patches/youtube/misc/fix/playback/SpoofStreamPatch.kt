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
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.*
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
        UserAgentClientSpoofPatch::class,
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.37.36",
                "18.38.44",
                "18.43.45",
                "18.44.41",
                "18.45.43",
                "18.48.39",
                "18.49.37",
                "19.01.34",
                "19.02.39",
                "19.03.36",
                "19.04.38",
                "19.05.36",
                "19.06.39",
                "19.07.40",
                "19.08.36",
                "19.09.38",
                "19.10.39",
                "19.11.43",
                "19.12.41",
                "19.13.37",
                "19.14.43",
                "19.15.36",
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
