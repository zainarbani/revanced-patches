package app.revanced.patches.youtube.video.quality

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.all.misc.resources.AddResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.ListPreference
import app.revanced.patches.shared.misc.settings.preference.SwitchPreference
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.patches.youtube.video.information.VideoInformationPatch
import app.revanced.patches.youtube.video.quality.fingerprints.NewVideoQualityChangedFingerprint
import app.revanced.patches.youtube.video.quality.fingerprints.SetQualityByIndexMethodClassFieldReferenceFingerprint
import app.revanced.patches.youtube.video.quality.fingerprints.VideoQualityItemOnClickParentFingerprint
import app.revanced.patches.youtube.video.quality.fingerprints.VideoQualitySetterFingerprint
import app.revanced.util.exception
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

@Patch(
    name = "Remember video quality",
    description = "Adds an option to remember the last video quality selected.",
    dependencies = [
        IntegrationsPatch::class,
        VideoInformationPatch::class,
        SettingsPatch::class,
        AddResourcesPatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube", [
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
                "19.17.41",
                "19.18.41",
                "19.19.39",
                "19.20.35",
                "19.21.40",
                "19.22.43",
                "19.23.40",
                "19.24.45",
                "19.25.37", 
                "19.26.42",
                "19.28.42",
                "19.29.42",
                "19.30.39",
                "19.31.36",
                "19.32.36",
                "19.33.36",
                "19.34.42",
                "19.35.36",
                "19.36.37",
            ]
        )
    ]
)
@Suppress("unused")
object RememberVideoQualityPatch : BytecodePatch(
    setOf(
        VideoQualitySetterFingerprint,
        VideoQualityItemOnClickParentFingerprint,
        NewVideoQualityChangedFingerprint
    )
) {
    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "Lapp/revanced/integrations/youtube/patches/playback/quality/RememberVideoQualityPatch;"

    override fun execute(context: BytecodeContext) {
        AddResourcesPatch(this::class)

        SettingsPatch.PreferenceScreen.VIDEO.addPreferences(
            SwitchPreference("revanced_remember_video_quality_last_selected"),
            ListPreference(
                key = "revanced_video_quality_default_wifi",
                summaryKey = null,
                entriesKey = "revanced_video_quality_default_entries",
                entryValuesKey = "revanced_video_quality_default_entry_values"
            ),
            ListPreference(
                key = "revanced_video_quality_default_mobile",
                summaryKey = null,
                entriesKey = "revanced_video_quality_default_entries",
                entryValuesKey = "revanced_video_quality_default_entry_values"
            )
        )

        /*
         * The following code works by hooking the method which is called when the user selects a video quality
         * to remember the last selected video quality.
         *
         * It also hooks the method which is called when the video quality to set is determined.
         * Conveniently, at this point the video quality is overridden to the remembered playback speed.
         */
        VideoInformationPatch.onCreateHook(INTEGRATIONS_CLASS_DESCRIPTOR, "newVideoStarted")


        // Inject a call to set the remembered quality once a video loads.
        VideoQualitySetterFingerprint.result?.also {
            if (!SetQualityByIndexMethodClassFieldReferenceFingerprint.resolve(context, it.classDef))
                throw PatchException("Could not resolve fingerprint to find setQualityByIndex method")
        }?.let {
            // This instruction refers to the field with the type that contains the setQualityByIndex method.
            val instructions = SetQualityByIndexMethodClassFieldReferenceFingerprint.result!!
                .method.implementation!!.instructions

            val getOnItemClickListenerClassReference =
                (instructions.elementAt(0) as ReferenceInstruction).reference
            val getSetQualityByIndexMethodClassFieldReference =
                (instructions.elementAt(1) as ReferenceInstruction).reference

            val setQualityByIndexMethodClassFieldReference =
                getSetQualityByIndexMethodClassFieldReference as FieldReference

            val setQualityByIndexMethodClass = context.classes
                .find { classDef -> classDef.type == setQualityByIndexMethodClassFieldReference.type }!!

            // Get the name of the setQualityByIndex method.
            val setQualityByIndexMethod = setQualityByIndexMethodClass.methods
                .find { method -> method.parameterTypes.first() == "I" }
                ?: throw PatchException("Could not find setQualityByIndex method")

            it.mutableMethod.addInstructions(
                0,
                """
                    # Get the object instance to invoke the setQualityByIndex method on.
                    iget-object v0, p0, $getOnItemClickListenerClassReference
                    iget-object v0, v0, $getSetQualityByIndexMethodClassFieldReference
                    
                    # Get the method name.
                    const-string v1, "${setQualityByIndexMethod.name}"
                    
                    # Set the quality.
                    # The first parameter is the array list of video qualities.
                    # The second parameter is the index of the selected quality.
                    # The register v0 stores the object instance to invoke the setQualityByIndex method on.
                    # The register v1 stores the name of the setQualityByIndex method.
                    invoke-static {p1, p2, v0, v1}, $INTEGRATIONS_CLASS_DESCRIPTOR->setVideoQuality([Ljava/lang/Object;ILjava/lang/Object;Ljava/lang/String;)I
                    move-result p2
                """,
            )
        } ?: throw VideoQualitySetterFingerprint.exception


        // Inject a call to remember the selected quality.
        VideoQualityItemOnClickParentFingerprint.result?.let {
            val onItemClickMethod = it.mutableClass.methods.find { method -> method.name == "onItemClick" }

            onItemClickMethod?.apply {
                val listItemIndexParameter = 3

                addInstruction(
                    0,
                    "invoke-static {p$listItemIndexParameter}, $INTEGRATIONS_CLASS_DESCRIPTOR->userChangedQuality(I)V"
                )
            } ?: throw PatchException("Failed to find onItemClick method")
        } ?: throw VideoQualityItemOnClickParentFingerprint.exception


        // Remember video quality if not using old layout menu.
        NewVideoQualityChangedFingerprint.result?.apply {
            mutableMethod.apply {
                val index = scanResult.patternScanResult!!.startIndex
                val qualityRegister = getInstruction<TwoRegisterInstruction>(index).registerA

                addInstruction(
                    index + 1,
                    "invoke-static {v$qualityRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->userChangedQualityInNewFlyout(I)V"
                )
            }
        } ?: throw NewVideoQualityChangedFingerprint.exception
    }
}
