package app.revanced.patches.youtube.misc.fix.playback

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstructions
import app.revanced.patcher.extensions.or
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.all.misc.resources.AddResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.PreferenceScreen
import app.revanced.patches.shared.misc.settings.preference.PreferenceScreen.Sorting
import app.revanced.patches.shared.misc.settings.preference.SwitchPreference
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.BuildTestFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.BuildTestTwoFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.BuildInitPlaybackRequestFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.BuildPlayerRequestURIFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.BuildPlayerRequestBuilderFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.BuildFormatStreamModelFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.BuildVideoStreamingDataFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.BuildShortsCreationSelectedTrackFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.BuildShortRecompositionFragmentPeerFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.CreatePlayerRequestBodyFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.CreatePlayerRequestBodyWithModelFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.PlayerGestureConfigSyntheticFingerprint
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.SetPlayerRequestClientTypeFingerprint
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.patches.youtube.video.playerresponse.PlayerResponseMethodHookPatch
import app.revanced.patches.youtube.video.videoid.VideoIdPatch
import app.revanced.util.getReference
import app.revanced.util.indexOfFirstInstructionOrThrow
import app.revanced.util.resultOrThrow
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction35c
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ThreeRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter

@Patch(
    name = "Spoof client",
    description = "Spoofs the client to allow video playback.",
    dependencies = [
        SettingsPatch::class,
        AddResourcesPatch::class,
        UserAgentClientSpoofPatch::class,
        PlayerResponseMethodHookPatch::class,
        //VideoIdPatch::class,
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
object SpoofClientPatch : BytecodePatch(
    setOf(
        // Client type spoof.
        //BuildInitPlaybackRequestFingerprint,
        //BuildPlayerRequestURIFingerprint,
        //BuildPlayerRequestBuilderFingerprint,
        BuildFormatStreamModelFingerprint,
        //BuildVideoStreamingDataFingerprint,
        BuildTestTwoFingerprint,
        //BuildShortRecompositionFragmentPeerFingerprint,
        //SetPlayerRequestClientTypeFingerprint,
        //CreatePlayerRequestBodyFingerprint,
        //CreatePlayerRequestBodyWithModelFingerprint,

        // Player gesture config.
        //PlayerGestureConfigSyntheticFingerprint,
        
        //BuildTestFingerprint,
    ),
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/youtube/patches/spoof/SpoofClientPatch;"
    private const val CLIENT_INFO_CLASS_DESCRIPTOR =
        "Lcom/google/protos/youtube/api/innertube/InnertubeContext\$ClientInfo;"
    private const val FORMAT_STREAM_MODEL_CLASS_DESCRIPTOR =
        "Lcom/google/android/libraries/youtube/innertube/model/media/FormatStreamModel;"

    override fun execute(context: BytecodeContext) {
        AddResourcesPatch(this::class)

        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            PreferenceScreen(
                key = "revanced_spoof_client_screen",
                sorting = Sorting.UNSORTED,
                preferences = setOf(
                    SwitchPreference("revanced_spoof_client"),
                    SwitchPreference("revanced_spoof_client_use_ios"),
                ),
            ),
        )

        PlayerResponseMethodHookPatch += PlayerResponseMethodHookPatch.Hook.ProtoBufferParameter(
            "$INTEGRATIONS_CLASS_DESCRIPTOR->getPlayerRequestUri(" +
                "Ljava/lang/String;Ljava/lang/String;Z)Ljava/lang/String;",
        )
        
        BuildFormatStreamModelFingerprint.resultOrThrow().let {
            val testIndex = it.scanResult.patternScanResult!!.startIndex
            
            it.mutableMethod.apply {
                val targetRegister = getInstruction<TwoRegisterInstruction>(testIndex)

                addInstructionsWithLabels(
                    testIndex + 1,
                    """
                        if-eqz v1, :skip
                        if-eqz p2, :skip
                        invoke-static { v1, p2 }, $INTEGRATIONS_CLASS_DESCRIPTOR->getStreamingDataUrl(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v1
                    """, ExternalLabel("skip", getInstruction(testIndex + 1))
                )
            }
        }
        

        BuildTestTwoFingerprint.resultOrThrow().let {
            val initMethod = it.mutableClass
                .methods.first { method ->
                    method.name == "<init>" &&
                    method.parameters.count() == 10
                }
            
            initMethod?.apply {
                    addInstructions(
                        0,
                        """
                            invoke-static { p5 }, $INTEGRATIONS_CLASS_DESCRIPTOR->testPrintMap(Ljava/util/Map;)V
                            invoke-static { p8 }, $INTEGRATIONS_CLASS_DESCRIPTOR->testPrint(Ljava/lang/String;)V
                        """,
                    )
            } ?: throw PatchException("Could not find the init method.")
        }
        
        // endregion

    }
}
