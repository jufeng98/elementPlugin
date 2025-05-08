package org.javamaster.elementui.nls

import com.intellij.BundleBase.messageOrDefault
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

internal object NlsBundle {
    @NonNls
    private const val BUNDLE = "messages.ElementPluginBundle"
    private const val ZH = "zh"

    val language: String by lazy {
        if (Locale.getDefault().language == ZH) {
            return@lazy ZH
        } else {
            return@lazy "en"
        }
    }

    val region: String by lazy {
        if (Locale.getDefault().language == ZH) {
            return@lazy "zh-CN"
        } else {
            return@lazy "en-US"
        }
    }

    fun nls(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any): @Nls String {
        return messageOrDefault(ResourceBundle.getBundle(BUNDLE), key, "", *params)
    }

}