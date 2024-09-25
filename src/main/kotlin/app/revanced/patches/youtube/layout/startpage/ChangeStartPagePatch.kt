package app.revanced.patches.youtube.layout.startpage

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.all.misc.resources.AddResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.ListPreference
import app.revanced.patches.youtube.layout.startpage.fingerprints.BrowseIdFingerprint
import app.revanced.patches.youtube.layout.startpage.fingerprints.IntentActionFingerprint
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.util.getReference
import app.revanced.util.indexOfFirstInstructionOrThrow
import app.revanced.util.resultOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.StringReference

@Patch(
    name = "Change start page",
    description = "Adds an option to set which page the app opens in instead of the homepage.",
    dependencies = [
        IntegrationsPatch::class,
        SettingsPatch::class,
        AddResourcesPatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
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
                "19.25.37",
                "19.34.42",
            ]
        )
    ]
)
@Suppress("unused")
object ChangeStartPagePatch : BytecodePatch(
    setOf(BrowseIdFingerprint, IntentActionFingerprint)
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/youtube/patches/ChangeStartPagePatch;"

    override fun execute(context: BytecodeContext) {
        AddResourcesPatch(this::class)

        SettingsPatch.PreferenceScreen.GENERAL_LAYOUT.addPreferences(
            ListPreference(
                key = "revanced_change_start_page",
                summaryKey = null,
            )
        )

        // Hook broseId.
        BrowseIdFingerprint.resultOrThrow().mutableMethod.apply {
            val browseIdIndex = indexOfFirstInstructionOrThrow {
                opcode == Opcode.CONST_STRING &&
                        getReference<StringReference>()?.string == "FEwhat_to_watch"
            }
            val browseIdRegister = getInstruction<OneRegisterInstruction>(browseIdIndex).registerA

            addInstructions(
                browseIdIndex + 1, """
                        invoke-static { v$browseIdRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->overrideBrowseId(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$browseIdRegister
                    """
            )
        }

        // There is no browserId assigned to Shorts and Search.
        // Just hook the Intent action.
        IntentActionFingerprint.resultOrThrow().mutableMethod.addInstruction(
            0,
            "invoke-static { p1 }, $INTEGRATIONS_CLASS_DESCRIPTOR->overrideIntentAction(Landroid/content/Intent;)V"
        )
    }
}