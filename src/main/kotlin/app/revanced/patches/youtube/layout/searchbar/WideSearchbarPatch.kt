package app.revanced.patches.youtube.layout.searchbar

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.MethodFingerprintResult
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.all.misc.resources.addResources
import app.revanced.patches.all.misc.resources.addResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.SwitchPreference
import app.revanced.patches.youtube.layout.searchbar.fingerprints.createSearchSuggestionsFingerprint
import app.revanced.patches.youtube.layout.searchbar.fingerprints.setWordmarkHeaderFingerprint
import app.revanced.patches.youtube.misc.integrations.integrationsPatch
import app.revanced.patches.youtube.misc.settings.PreferenceScreen
import app.revanced.patches.youtube.misc.settings.settingsPatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

private const val INTEGRATIONS_CLASS_DESCRIPTOR =
    "Lapp/revanced/integrations/youtube/patches/WideSearchbarPatch;"

@Suppress("unused")
val wideSearchbarPatch = bytecodePatch(
    name = "Wide searchbar",
    description = "Adds an option to replace the search icon with a wide search bar. This will hide the YouTube logo when active.",
) {
    dependsOn(
        integrationsPatch,
        settingsPatch,
        addResourcesPatch,
    )

    compatibleWith(
        "com.google.android.youtube"(
            "18.32.39",
            "18.37.36",
            "18.38.44",
            "18.43.45",
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
        ),
    )

    val setWordmarkHeaderResult by setWordmarkHeaderFingerprint
    val createSearchSuggestionsResult by createSearchSuggestionsFingerprint

    execute { context ->
        addResources("youtube", "layout.searchbar.WideSearchbarPatch")

        PreferenceScreen.FEED.addPreferences(
            SwitchPreference("revanced_wide_searchbar"),
        )

        /**
         * Navigate a fingerprints method at a given index mutably.
         *
         * @param index The index to navigate to.
         * @param fromResult The fingerprint result to navigate the method on.
         * @return The [MutableMethod] which was navigated on.
         */
        fun BytecodePatchContext.walkMutable(index: Int, fromResult: MethodFingerprintResult) =
            navigate(fromResult.method).at(index).mutable()

        /**
         * Injects instructions required for certain methods.
         */
        fun MutableMethod.injectSearchBarHook() {
            val insertIndex = implementation!!.instructions.size - 1
            val insertRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

            addInstructions(
                insertIndex,
                """
                invoke-static {v$insertRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->enableWideSearchbar(Z)Z
                move-result v$insertRegister
            """,
            )
        }

        mapOf(
            setWordmarkHeaderResult to 1,
            createSearchSuggestionsResult to createSearchSuggestionsResult.scanResult.patternScanResult!!.startIndex,
        ).forEach { (fingerprint, callIndex) ->
            context.walkMutable(callIndex, fingerprint).injectSearchBarHook()
        }
    }
}
