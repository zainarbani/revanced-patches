package app.revanced.patches.youtube.layout.thumbnails

import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.instructions
import app.revanced.patcher.fingerprint.MethodFingerprint
import app.revanced.patcher.fingerprint.MethodFingerprintResult
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.revanced.patches.all.misc.resources.addResources
import app.revanced.patches.all.misc.resources.addResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.ListPreference
import app.revanced.patches.shared.misc.settings.preference.NonInteractivePreference
import app.revanced.patches.shared.misc.settings.preference.SwitchPreference
import app.revanced.patches.shared.misc.settings.preference.TextPreference
import app.revanced.patches.youtube.layout.thumbnails.fingerprints.*
import app.revanced.patches.youtube.misc.integrations.integrationsPatch
import app.revanced.patches.youtube.misc.navigation.navigationBarHookPatch
import app.revanced.patches.youtube.misc.settings.PreferenceScreen
import app.revanced.patches.youtube.misc.settings.settingsPatch
import app.revanced.util.resultOrThrow
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod

private const val INTEGRATIONS_CLASS_DESCRIPTOR =
    "Lapp/revanced/integrations/youtube/patches/AlternativeThumbnailsPatch;"

private lateinit var loadImageUrlMethod: MutableMethod
private var loadImageUrlIndex = 0

private lateinit var loadImageSuccessCallbackMethod: MutableMethod
private var loadImageSuccessCallbackIndex = 0

private lateinit var loadImageErrorCallbackMethod: MutableMethod
private var loadImageErrorCallbackIndex = 0

@Suppress("unused")
val alternativeThumbnailsPatch = bytecodePatch(
    name = "Alternative thumbnails",
    description = "Adds options to replace video thumbnails using the DeArrow API or image captures from the video.",
) {
    dependsOn(
        integrationsPatch,
        settingsPatch,
        addResourcesPatch,
        navigationBarHookPatch,
        CronetImageUrlHook::class
    )

    compatibleWith(
        "com.google.android.youtube"(
            "18.32.39",
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
        ),
    )

    execute { context ->
        addResources("youtube", "layout.thumbnails.AlternativeThumbnailsPatch")

        val entries = "revanced_alt_thumbnail_options_entries"
        val values = "revanced_alt_thumbnail_options_entry_values"
        PreferenceScreen.ALTERNATIVE_THUMBNAILS.addPreferences(
            ListPreference(
                "revanced_alt_thumbnail_home",
                summaryKey = null,
                entriesKey = entries,
                entryValuesKey = values,
            ),
            ListPreference(
                "revanced_alt_thumbnail_subscription",
                summaryKey = null,
                entriesKey = entries,
                entryValuesKey = values,
            ),
            ListPreference(
                "revanced_alt_thumbnail_library",
                summaryKey = null,
                entriesKey = entries,
                entryValuesKey = values,
            ),
            ListPreference(
                "revanced_alt_thumbnail_player",
                summaryKey = null,
                entriesKey = entries,
                entryValuesKey = values,
            ),
            ListPreference(
                "revanced_alt_thumbnail_search",
                summaryKey = null,
                entriesKey = entries,
                entryValuesKey = values,
            ),
            NonInteractivePreference(
                "revanced_alt_thumbnail_dearrow_about",
                // Custom about preference with link to the DeArrow website.
                tag = "app.revanced.integrations.youtube.settings.preference.AlternativeThumbnailsAboutDeArrowPreference",
                selectable = true,
            ),
            SwitchPreference("revanced_alt_thumbnail_dearrow_connection_toast"),
            TextPreference("revanced_alt_thumbnail_dearrow_api_url"),
            NonInteractivePreference("revanced_alt_thumbnail_stills_about"),
            SwitchPreference("revanced_alt_thumbnail_stills_fast"),
            ListPreference("revanced_alt_thumbnail_stills_time", summaryKey = null),
        )

        addImageUrlHook(INTEGRATIONS_CLASS_DESCRIPTOR)
        addImageUrlSuccessCallbackHook(INTEGRATIONS_CLASS_DESCRIPTOR)
        addImageUrlErrorCallbackHook(INTEGRATIONS_CLASS_DESCRIPTOR)
    }
}
