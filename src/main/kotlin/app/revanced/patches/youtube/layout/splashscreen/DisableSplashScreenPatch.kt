package app.revanced.patches.youtube.layout.splashscreen

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.all.misc.resources.AddResourcesPatch
import app.revanced.patches.shared.misc.settings.preference.SwitchPreference
import app.revanced.patches.youtube.misc.integrations.IntegrationsPatch
import app.revanced.patches.youtube.misc.settings.SettingsPatch
import app.revanced.util.containsWideLiteralInstructionValue
import app.revanced.util.findMutableMethodOf

@Patch(
    name = "Disable splash screen animations",
    description = "Adds an option to disable splash screen animations when starting the app.",
    dependencies = [
        IntegrationsPatch::class,
        SettingsPatch::class,
        AddResourcesPatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
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
            ]
        )
    ]
)
@Suppress("unused")
object DisableSplashScreenPatch : BytecodePatch() {
    private const val INTEGRATIONS_METHOD_DESCRIPTOR =
        "Lapp/revanced/integrations/youtube/patches/DisableSplashScreenPatch;->isSplashEnabled()Z"

    override fun execute(context: BytecodeContext) {
        AddResourcesPatch(this::class)

        SettingsPatch.PreferenceScreen.GENERAL_LAYOUT.addPreferences(
            SwitchPreference("revanced_disable_splash_screen")
        )

        var modifiedMethods = 0

        context.classes.forEach { classDef ->
            classDef.methods.forEach { method ->
                // Allow splash screen animations flags.
                arrayOf(45417620L, 45408839L).forEach { wideValue ->
                    if (method.containsWideLiteralInstructionValue(wideValue)) {
                        with(context.proxy(classDef).mutableClass) {
                            with(findMutableMethodOf(method)) {
                                addInstructions(0,
                                    """
                                        invoke-static { }, $INTEGRATIONS_METHOD_DESCRIPTOR
                                        move-result v0
                                        return v0
                                    """
                                )
                            }
                        }

                        modifiedMethods++
                    }
                }
            }
        }

        if (modifiedMethods != 2) throw PatchException("Could not find methods to modify")
    }
}