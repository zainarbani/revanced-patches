package app.revanced.patches.youtube.misc.backgroundplayback

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.all.misc.resources.AddResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.SwitchPreference
import app.revanced.patches.youtube.misc.backgroundplayback.fingerprints.*
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.playertype.PlayerTypeHookPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.patches.youtube.video.information.VideoInformationPatch
import app.revanced.util.addInstructionsAtControlFlowLabel
import app.revanced.util.findOpcodeIndicesReversed
import app.revanced.util.resultOrThrow
import app.revanced.util.returnEarly
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Patch(
    name = "Remove background playback restrictions",
    description = "Removes restrictions on background playback, including playing kids videos in the background.",
    dependencies = [
        BackgroundPlaybackResourcePatch::class,
        IntegrationsPatch::class,
        PlayerTypeHookPatch::class,
        VideoInformationPatch::class,
        SettingsPatch::class,
        AddResourcesPatch::class,
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.38.44",
                "18.49.37",
                "19.16.39",
                "19.25.37",
                "19.34.42",
            ]
        )
    ]
)
@Suppress("unused")
object BackgroundPlaybackPatch : BytecodePatch(
    setOf(
        BackgroundPlaybackManagerFingerprint,
        BackgroundPlaybackManagerShortsFingerprint,
        BackgroundPlaybackSettingsFingerprint,
        ShortsBackgroundPlaybackFeatureFlagFingerprint,
        KidsBackgroundPlaybackPolicyControllerFingerprint
    )
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/youtube/patches/BackgroundPlaybackPatch;"

    override fun execute(context: BytecodeContext) {
        AddResourcesPatch(this::class)

        SettingsPatch.PreferenceScreen.SHORTS.addPreferences(
            SwitchPreference("revanced_shorts_background_playback")
        )

        arrayOf(
            BackgroundPlaybackManagerFingerprint to "isBackgroundPlaybackAllowed",
            BackgroundPlaybackManagerShortsFingerprint to "isBackgroundShortsPlaybackAllowed"
        ).forEach { (fingerprint, integrationsMethod) ->
            fingerprint.resultOrThrow().mutableMethod.addInstructions(
                0,
                """
                    invoke-static {}, $INTEGRATIONS_CLASS_DESCRIPTOR->$integrationsMethod()Z
                    move-result v0
                    return v0
            """
            )
        }

        val overrideBackgroundPlaybackSettingsInstructions = """
                    invoke-static {}, $INTEGRATIONS_CLASS_DESCRIPTOR->overrideBackgroundPlaybackAvailable()Z
                    move-result v0
                    return v0
                """
        BackgroundPlaybackManagerFingerprint.resultOrThrow().mutableMethod.apply {
            findOpcodeIndicesReversed(Opcode.RETURN).forEach{ index ->
                val register = getInstruction<OneRegisterInstruction>(index).registerA

                addInstructionsAtControlFlowLabel(
                    index,
                    """
                        invoke-static { v$register }, $INTEGRATIONS_CLASS_DESCRIPTOR->allowBackgroundPlayback(Z)Z
                        move-result v$register 
                    """
                )
            }
        }

        // Enable background playback option in YouTube settings
        BackgroundPlaybackSettingsFingerprint.resultOrThrow().mutableMethod.apply {
            val booleanCalls = implementation!!.instructions.withIndex()
                .filter { ((it.value as? ReferenceInstruction)?.reference as? MethodReference)?.returnType == "Z" }

            val settingsBooleanIndex = booleanCalls.elementAt(1).index
            val settingsBooleanMethod =
                context.toMethodWalker(this).nextMethod(settingsBooleanIndex, true).getMethod() as MutableMethod

            settingsBooleanMethod.addInstructions(0, overrideBackgroundPlaybackSettingsInstructions)
        }

        // Force allowing background play for Shorts.
        ShortsBackgroundPlaybackFeatureFlagFingerprint.resultOrThrow().mutableMethod.addInstructions(
            0,
            overrideBackgroundPlaybackSettingsInstructions
        )

        // Force allowing background play for videos labeled for kids.
        KidsBackgroundPlaybackPolicyControllerFingerprint.returnEarly()
    }
}
