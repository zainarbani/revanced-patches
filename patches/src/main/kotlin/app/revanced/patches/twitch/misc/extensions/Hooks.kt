package app.revanced.patches.twitch.misc.extensions

import app.revanced.patches.shared.misc.extensions.extensionsHook

internal val initHook = extensionsHook {
    custom { method, classDef ->
        classDef.endsWith("/TwitchApplication;") &&
            method.name == "onCreate"
    }
}
