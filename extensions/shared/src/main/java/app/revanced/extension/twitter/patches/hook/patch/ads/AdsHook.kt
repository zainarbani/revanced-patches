package app.revanced.extension.twitter.patches.hook.patch.ads

import app.revanced.extension.twitter.patches.hook.json.BaseJsonHook
import app.revanced.extension.twitter.patches.hook.twifucker.TwiFucker
import org.json.JSONObject

object AdsHook : BaseJsonHook() {
    /**
     * Strips JSONObject from promoted ads.
     *
     * @param json The JSONObject.
     */
    override fun apply(json: JSONObject) = TwiFucker.hidePromotedAds(json)
}
