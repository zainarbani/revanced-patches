package app.revanced.patches.youtube.misc.fix.playback

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstructions
import app.revanced.patcher.extensions.or
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.revanced.patches.all.misc.resources.AddResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.PreferenceScreen
import app.revanced.patches.shared.misc.settings.preference.SwitchPreference
import app.revanced.patches.youtube.misc.fix.playback.fingerprints.*
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.util.getReference
import app.revanced.util.indexOfFirstInstructionOrThrow
import app.revanced.util.resultOrThrow
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
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
        BuildInitPlaybackRequestFingerprint,
        BuildPlayerRequestURIFingerprint,
        SetPlayerRequestClientTypeFingerprint,
        BuildGetDefaultUserAgentFingerprint,
        CreatePlayerRequestBodyFingerprint,
        CreatePlayerRequestBodyWithModelFingerprint,

        // Player gesture config.
        PlayerGestureConfigSyntheticFingerprint,

        // Player speed menu item.
        CreatePlaybackSpeedMenuItemFingerprint,
        BuildTestFingerprint,
        BuildTestTwoFingerprint,
    ),
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/youtube/patches/spoof/SpoofClientPatch;"
    private const val CLIENT_INFO_CLASS_DESCRIPTOR =
        "Lcom/google/protos/youtube/api/innertube/InnertubeContext\$ClientInfo;"

    override fun execute(context: BytecodeContext) {
        AddResourcesPatch(this::class)

        SettingsPatch.PreferenceScreen.MISC.addPreferences(
            PreferenceScreen(
                key = "revanced_spoof_client_screen",
                sorting = PreferenceScreen.Sorting.UNSORTED,
                preferences = setOf(
                    SwitchPreference("revanced_spoof_client"),
                    SwitchPreference("revanced_spoof_client_use_ios"),
                ),
            ),
        )

        BuildTestTwoFingerprint.resultOrThrow().let {
            it.mutableMethod.instructions.forEachIndexed { index, instruction ->
                if (instruction.opcode == Opcode.INVOKE_VIRTUAL &&
                    instruction.getReference<MethodReference>()?.name == "getPackageName"
                ) {
                    it.mutableMethod.apply {
                        val targetRegister = getInstruction<OneRegisterInstruction>(index + 1).registerA

                        replaceInstruction(
                            index + 1, "const-string v$targetRegister, \"com.google.android.youtube\""
                        )
                    }
                }
            }
        }

        BuildTestFingerprint.resultOrThrow().let {
            val scanResult = it.scanResult.patternScanResult!!.startIndex

            it.mutableMethod.apply {
                val targetRegister = getInstruction<OneRegisterInstruction>(scanResult + 1).registerA

                replaceInstruction(
                    scanResult + 1, "const-string v$targetRegister, \"cce45e8ae69c58b10babdc57290cfe99ba939c70436101f2c2bf8e70912f19e5\""
                )
            }

            val bMethod = it.mutableClass
                .methods.find { method ->
                    method.name == "b"
                }

            bMethod?.apply {
                replaceInstruction(
                    35, "const-string v4, \"com.google.android.youtube\""
                )
            } ?: throw PatchException("Could not find the init method.")
            
            val cMethod = it.mutableClass
                .methods.find { method ->
                    method.name == "c"
                }

            cMethod?.apply {
                addInstructions(
                    13,
                     """
                         invoke-static { v0 }, $INTEGRATIONS_CLASS_DESCRIPTOR->testPrint(Ljava/lang/String;)V
                     """
                )
            } ?: throw PatchException("Could not find the init method.")

        }
        
    }
}
