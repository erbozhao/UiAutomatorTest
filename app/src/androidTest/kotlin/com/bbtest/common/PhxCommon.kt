package com.bbtest.common

import androidx.test.uiautomator.UiObject2
import org.junit.After
import org.junit.Before

open class PhxCommon : BaseCommon() {
    val pkgName = "com.transsion.phoenix"
    val activity = "com.transsion.phoenix/com.proj.sun.activity.LaunchActivity"
    val topActivity = "com.transsion.phoenix/com.tencent.mtt.MoreProcessMainActivity"

    @Before
    override fun beforeTest() {
        super.beforeTest()
    }

    @After
    override fun afterTest() {
        super.afterTest()
    }

    fun isNeedSkipSplash(): Boolean {
        val splashActivity = "com.transsion.phoenix/com.tencent.mtt.BlockActivity"
        return ShellCommon.getTopActivity(device, null) == splashActivity
    }

    fun isThirdCallSuccess(thirdCallFile: String): Boolean {
        var thirdCallActivity0 = ""
        var thirdCallActivity1 = ""
        when (thirdCallFile) {
            "webpage" -> thirdCallActivity0 = "com.transsion.phoenix/com.tencent.mtt.MoreProcessMainActivity"
            "video" -> thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phx.video.H5VideoThirdCallActivity"
            "music" -> thirdCallActivity0 = "com.transsion.phoenix/com.tencent.bang.music.ui.MusicPlayerActivity"
            "doc" -> {
                thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.DocReaderActivity"
                thirdCallActivity1 = "com.transsion.phoenix/.reader.DocReaderActivity"
            }
            "ppt" -> {
                thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.PPTReaderActivity"
                thirdCallActivity1 = "com.transsion.phoenix/.reader.PPTReaderActivity"
            }
            "xls" -> {
                thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.XSLReaderActivity"
                thirdCallActivity1 = "com.transsion.phoenix/.reader.XSLReaderActivity"
            }
            "pdf" -> {
                thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.PDFReaderActivity"
                thirdCallActivity1 = "com.transsion.phoenix/.reader.PDFReaderActivity"
            }
            "epub" -> {
                thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.EPUBReaderActivity"
                thirdCallActivity1 = "com.transsion.phoenix/.reader.EPUBReaderActivity"
            }
            "image" -> {
                thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.ImageReaderActivity"
                thirdCallActivity1 = "com.transsion.phoenix/.reader.ImageReaderActivity"
            }
            "zip" -> {
                thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.ZipReaderActivity"
                thirdCallActivity1 = "com.transsion.phoenix/.reader.ZipReaderActivity"
            }
            "txt" -> {
                thirdCallActivity0 = "com.transsion.phoenix/com.transsion.phoenix.reader.TXTReaderActivity"
                thirdCallActivity1 = "com.transsion.phoenix/.reader.TXTReaderActivity"
            }
        }
        val topActivity = ShellCommon.getTopActivity(device, null)
        return topActivity == thirdCallActivity0 || (thirdCallActivity1 != "" && topActivity == thirdCallActivity1)
    }

    fun skipSplash() {
        var agreeStart = waitUiObject2ByText("Agree and Start", TIMEOUT_MEDIUM)
        if (agreeStart == null) {
            agreeStart = waitUiObject2ByText("أوافق، إبدأ الإستخدام", TIMEOUT_VERY_SHORT)
        }
        if (agreeStart != null) {
            agreeStart.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val skip = waitUiObject2ByText("Skip", TIMEOUT_MEDIUM)
            if (skip != null) {
                skip.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            } else {
                val startNow = waitUiObject2ByText("Start Now", TIMEOUT_VERY_SHORT)
                if (startNow != null) {
                    startNow.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            }
        }
    }

    fun skipFilesGuide() {
        var files = waitUiObject2ByText("Files", TIMEOUT_MEDIUM)
        if (files == null) {
            files = waitUiObject2ByText("الملفّات", TIMEOUT_VERY_SHORT)
        }
        files?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        val continueFile = waitUiObject2ByRes("com.transsion.phoenix:id/continue_button", TIMEOUT_MEDIUM.toLong())
        if (continueFile != null) {
            continueFile.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val switchBtn = waitUiObject2ByRes("android:id/switch_widget", TIMEOUT_MEDIUM.toLong())
            if (switchBtn != null && !switchBtn.isChecked) {
                switchBtn.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                backToHome()
            }
        }
        var home = waitUiObject2ByText("Home", TIMEOUT_MEDIUM)
        if (home == null) {
            home = waitUiObject2ByText("الرئيسية", TIMEOUT_VERY_SHORT)
        }
        home?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
    }

    fun skipFeedsGuide() {
        swip(0.5, 0.8, 0.5, 0.2)
        sleep(TIMEOUT_VERY_SHORT.toLong())
        var home = waitUiObject2ByText("Home", TIMEOUT_MEDIUM)
        if (home == null) {
            home = waitUiObject2ByText("الرئيسية", TIMEOUT_VERY_SHORT)
        }
        home?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
    }

    open fun skipAppDialog() {
        val defaultBrowserDialog = waitUiObject2ByText("Continue", TIMEOUT_VERY_SHORT)
        if (defaultBrowserDialog != null) {
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        val rateUs = waitUiObject2ByText("Rate us 5 stars", TIMEOUT_VERY_SHORT)
        if (rateUs != null) {
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        val offline = waitUiObject2ByText("The network is not connected, check the offline content right now.", TIMEOUT_VERY_SHORT)
        if (offline != null) {
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
    }

    open fun skipOtherDialog() {
        val agree = waitUiObject2ByText("AGREE", TIMEOUT_VERY_SHORT)
        if (agree != null) {
            agree.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        val accept = waitUiObject2ByText("Accept", TIMEOUT_VERY_SHORT)
        if (accept != null) {
            accept.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        val allow = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT)
        if (allow != null) {
            allow.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        val cancel = waitUiObject2ByText("取消", TIMEOUT_VERY_SHORT)
        if (cancel != null) {
            cancel.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
        val ok = waitUiObject2ByText("OK", TIMEOUT_VERY_SHORT)
        if (ok != null) {
            ok.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
    }

    fun switchLanguage(country: String, language: String) {
        try {
            var me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM)
            if (me == null) me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT)
            if (me != null) me.click() else waitUiObject2ByDesc("toolbar menu", TIMEOUT_VERY_SHORT.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var settings = waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)
            if (settings == null) settings = waitUiObject2ByText("الإعدادات", TIMEOUT_VERY_SHORT)
            settings?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            skipAppDialog()
            if (waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM) == null) {
                waitUiObject2ByText("محرك البحث", TIMEOUT_VERY_SHORT)
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.5, 0.8, 0.5, 0.2)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var aboutPhoenix = waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM)
            if (aboutPhoenix == null) aboutPhoenix = waitUiObject2ByText("حول Phoenix", TIMEOUT_VERY_SHORT)
            aboutPhoenix?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var verDesc = waitUiObject2ByTextContains("Version", TIMEOUT_MEDIUM)
            if (verDesc == null) verDesc = waitUiObject2ByTextContains("إصدار", TIMEOUT_VERY_SHORT)
            repeat(5) { verDesc?.click() }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var locale = waitUiObject2ByText("Locale Setting", TIMEOUT_MEDIUM)
            if (locale == null) locale = waitUiObject2ByText("Locale Test", TIMEOUT_VERY_SHORT)
            if (locale == null) {
                val phxIcon = getUiObject2s("android.widget.ImageView", true, 0.2, 0.5, 0.05, 0.5, 0.2, 0.8, 0.05, 0.5)?.firstOrNull()
                repeat(5) { phxIcon?.click() }
                sleep(TIMEOUT_VERY_SHORT.toLong())
                locale = waitUiObject2ByText("Locale Setting", TIMEOUT_MEDIUM)
                if (locale == null) locale = waitUiObject2ByText("Locale Test", TIMEOUT_VERY_SHORT)
            }
            locale?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            val countryLanguage = waitUiObject2ByText("$country-$language", TIMEOUT_MEDIUM)
            if (countryLanguage != null) {
                countryLanguage.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
            } else {
                if (waitUiObject2ByText("Type to search country", TIMEOUT_MEDIUM) == null) {
                    swip(0.5, 0.8, 0.5, 0.2)
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    waitUiObject2ByText("Custom Setting", TIMEOUT_MEDIUM)?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
                waitUiObject2ByText("Type to search country", TIMEOUT_MEDIUM)?.setText(country)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                waitUiObject2ByTextContains("$country |", TIMEOUT_MEDIUM)?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                repeat(10) {
                    if (waitUiObject2ByText("About Phoenix", TIMEOUT_SHORT) == null && waitUiObject2ByText("حول Phoenix", TIMEOUT_VERY_SHORT) == null) {
                        back()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                    } else {
                        return@repeat
                    }
                }
                sleep(TIMEOUT_VERY_SHORT.toLong())
                swip(0.5, 0.2, 0.5, 0.8)
                sleep(TIMEOUT_VERY_SHORT.toLong())
                var languageSet = waitUiObject2ByText("Language", TIMEOUT_MEDIUM)
                if (languageSet == null) languageSet = waitUiObject2ByText("اللغة", TIMEOUT_VERY_SHORT)
                languageSet?.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                when (language) {
                    "en" -> waitScrollableUiObjectByTextContains("androidx.recyclerview.widget.RecyclerView", "English", false)?.click()
                    "fr" -> waitScrollableUiObjectByTextContains("androidx.recyclerview.widget.RecyclerView", "français", false)?.click()
                    "ar" -> waitScrollableUiObjectByTextContains("androidx.recyclerview.widget.RecyclerView", "العربية", false)?.click()
                }
                sleep(TIMEOUT_VERY_SHORT.toLong())
                var restart = waitUiObject2ByText("Restart", TIMEOUT_MEDIUM)
                if (restart == null) restart = waitUiObject2ByText("إعادة التشغيل", TIMEOUT_VERY_SHORT)
                if (restart != null) {
                    restart.click()
                    sleep(TIMEOUT_LONG.toLong())
                } else {
                    backToHome()
                }
            }
            ShellCommon.forceStopApp(device, pkgName, null)
            sleep(TIMEOUT_SHORT.toLong())
            startApp(pkgName)
            sleep(TIMEOUT_LONG.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun switchGrayEnv(isGrayEnv: Boolean) {
        try {
            var me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM)
            if (me == null) me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT)
            if (me != null) me.click() else waitUiObject2ByDesc("toolbar menu", TIMEOUT_VERY_SHORT.toLong())?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var settings = waitUiObject2ByText("Settings", TIMEOUT_MEDIUM)
            if (settings == null) settings = waitUiObject2ByText("الإعدادات", TIMEOUT_VERY_SHORT)
            settings?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            skipAppDialog()
            if (waitUiObject2ByText("Search engine", TIMEOUT_MEDIUM) == null) {
                waitUiObject2ByText("محرك البحث", TIMEOUT_VERY_SHORT)
            }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            swip(0.5, 0.8, 0.5, 0.2)
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var aboutPhoenix = waitUiObject2ByTextContains("About Phoenix", TIMEOUT_MEDIUM)
            if (aboutPhoenix == null) aboutPhoenix = waitUiObject2ByText("حول Phoenix", TIMEOUT_VERY_SHORT)
            aboutPhoenix?.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var verDesc = waitUiObject2ByTextContains("Version", TIMEOUT_MEDIUM)
            if (verDesc == null) verDesc = waitUiObject2ByTextContains("إصدار", TIMEOUT_VERY_SHORT)
            repeat(5) { verDesc?.click() }
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var locale = waitUiObject2ByText("Locale Setting", TIMEOUT_MEDIUM)
            if (locale == null) locale = waitUiObject2ByText("Locale Test", TIMEOUT_VERY_SHORT)
            if (locale == null) {
                val phxIcon = getUiObject2s("android.widget.ImageView", true, 0.2, 0.5, 0.05, 0.5, 0.2, 0.8, 0.05, 0.5)?.firstOrNull()
                repeat(5) { phxIcon?.click() }
                sleep(TIMEOUT_VERY_SHORT.toLong())
            }
            val tupEnv = waitUiObject2ByText("TUP Environment", TIMEOUT_MEDIUM)
            if (tupEnv != null) {
                tupEnv.click()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                if (isGrayEnv) {
                    val preEnv = waitUiObject2ByText("Pre-production Environment", TIMEOUT_MEDIUM)
                    preEnv?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                } else {
                    val proEnv = waitUiObject2ByText("Production Environment", TIMEOUT_MEDIUM)
                    proEnv?.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            }
            backToHome()
            backExitBrowser()
            ShellCommon.forceStopApp(device, pkgName, null)
            sleep(TIMEOUT_SHORT.toLong())
            startApp(pkgName)
            sleep(TIMEOUT_LONG.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun skipSniffVideosGuide() {
        val guide = waitUiObject2ByTextContains("Download videos from", TIMEOUT_MEDIUM)
        if (guide != null) {
            guide.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        } else {
            var facebook = waitUiObject2ByText("Facebook", TIMEOUT_MEDIUM)
            if (facebook == null) facebook = waitUiObject2ByText("فيسبوك", TIMEOUT_VERY_SHORT)
            if (facebook != null) {
                facebook.click()
                sleep(TIMEOUT_SHORT.toLong())
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                val guide2 = waitUiObject2ByTextContains("Download videos from", TIMEOUT_MEDIUM)
                if (guide2 != null) {
                    guide2.click()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            }
        }
    }

    fun backToApp() {
        val anrDialog = waitUiObject2ByText("Close app", TIMEOUT_MEDIUM)
        if (anrDialog != null) {
            anrDialog.click()
            sleep(TIMEOUT_SHORT.toLong())
        }
        if (ShellCommon.isAppBackstage(device, pkgName)) {
            ShellCommon.amStartApp(device, activity, null)
            sleep(TIMEOUT_MEDIUM.toLong())
        }
    }

    open fun backToHome() {
        for (i in 0 until 30) {
            if (waitUiObject2ByRes("com.transsion.phoenix:id/homepage_qrcode_button", TIMEOUT_SHORT.toLong()) != null) {
                back()
                sleep(TIMEOUT_VERY_SHORT.toLong())
                var sureExit0 = waitUiObject2ByText("Exit Phoenix?", TIMEOUT_VERY_SHORT)
                if (sureExit0 == null) sureExit0 = waitUiObject2ByText("تأكيد الخروج الآن ؟", TIMEOUT_VERY_SHORT)
                if (sureExit0 != null) {
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    break
                }
                var sureExit1 = waitUiObject2ByText("Do you have any difficulties While using Phoenix Browser？", TIMEOUT_VERY_SHORT)
                if (sureExit1 == null) sureExit1 = waitUiObject2ByText("هل واجهتط صعوبات أثناء رحلتك مع فينيكس", TIMEOUT_VERY_SHORT)
                if (sureExit1 != null) {
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    break
                }
                val clearExit0 = waitUiObject2ByText("Clear history on exit?", TIMEOUT_VERY_SHORT)
                if (clearExit0 != null) {
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    break
                }
                val clearExit1 = waitUiObject2ByText("Clear History When Exit ?", TIMEOUT_VERY_SHORT)
                if (clearExit1 != null) {
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                    break
                }
            } else {
                if (i > 8) {
                    var dialog = waitUiObject2ByText("OK", TIMEOUT_SHORT)
                    if (dialog == null) dialog = waitUiObject2ByText("موافق", TIMEOUT_VERY_SHORT)
                    if (dialog != null) {
                        dialog.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var yesDialog = waitUiObject2ByText("Yes", TIMEOUT_VERY_SHORT)
                    if (yesDialog == null) yesDialog = waitUiObject2ByText("نعم", TIMEOUT_VERY_SHORT)
                    if (yesDialog != null) {
                        yesDialog.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var continueDialog = waitUiObject2ByText("Continue", TIMEOUT_VERY_SHORT)
                    if (continueDialog == null) continueDialog = waitUiObject2ByText("متابعة", TIMEOUT_VERY_SHORT)
                    if (continueDialog != null) {
                        continueDialog.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var allowDialog = waitUiObject2ByText("Allow", TIMEOUT_VERY_SHORT)
                    if (allowDialog == null) allowDialog = waitUiObject2ByText("السماح", TIMEOUT_VERY_SHORT)
                    if (allowDialog != null) {
                        allowDialog.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var customDialog = waitUiObject2ByText("You can customize your news feeds in just two steps", TIMEOUT_VERY_SHORT)
                    if (customDialog == null) customDialog = waitUiObject2ByText("يمكنك تخصيص موجز الأخبار الخاص بك فى خطوتين", TIMEOUT_VERY_SHORT)
                    if (customDialog != null) {
                        getUiObject2s("android.widget.ImageView", true, 0.0, 0.2, 0.0, 0.2, 0.0, 1.0, 0.1, 0.5)?.firstOrNull()?.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    var skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/closeButton", TIMEOUT_VERY_SHORT.toLong())
                    if (skipButton == null) skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/close", TIMEOUT_VERY_SHORT.toLong())
                    if (skipButton == null) skipButton = waitUiObject2ByRes("com.transsion.phoenix:id/trybuttom", TIMEOUT_VERY_SHORT.toLong())
                    if (skipButton == null) skipButton = waitUiObject2ByText("Try", TIMEOUT_VERY_SHORT)
                    if (skipButton == null) skipButton = waitUiObject2ByText("جَرّبه الآن", TIMEOUT_VERY_SHORT)
                    if (skipButton != null) {
                        skipButton.click()
                        sleep(TIMEOUT_VERY_SHORT.toLong())
                        continue
                    }
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                } else {
                    back()
                    sleep(TIMEOUT_VERY_SHORT.toLong())
                }
            }
        }
    }

    fun menuExitBrowser() {
        var me = waitUiObject2ByText("Me", TIMEOUT_MEDIUM)
        if (me == null) me = waitUiObject2ByText("أنا", TIMEOUT_VERY_SHORT)
        if (me != null) me.click() else waitUiObject2ByDesc("toolbar menu", TIMEOUT_MEDIUM.toLong())?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
        var exit = waitUiObject2ByText("Exit", TIMEOUT_MEDIUM)
        if (exit == null) exit = waitUiObject2ByText("خروج", TIMEOUT_VERY_SHORT)
        exit?.click()
        sleep(TIMEOUT_SHORT.toLong())
    }

    fun backExitBrowser(): Boolean {
        var isBackExitSuccess = false
        val topActivity = ShellCommon.getTopActivity(device, null)
        for (i in 0 until 10) {
            back()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            var exit = waitUiObject2ByText("Exit", TIMEOUT_SHORT)
            if (exit == null) exit = waitUiObject2ByText("خروج", TIMEOUT_VERY_SHORT)
            if (exit != null) {
                exit.click()
            } else {
                back()
            }
            sleep(TIMEOUT_SHORT.toLong())
            val curActivity = ShellCommon.getTopActivity(device, null)
            if (curActivity != topActivity) {
                isBackExitSuccess = true
                break
            }
        }
        return isBackExitSuccess
    }

    fun clickSearchBox(isOpenedPage: Boolean) {
        var searchBoxs: List<UiObject2>? =
            if (isOpenedPage) {
                getUiObject2sByChildClazz("android.widget.LinearLayout", true, "android.widget.TextView", 0.5, 1.0, 0.04, 0.5, 0.0, 1.0, 0.3, 0.7)
            } else {
                getUiObject2s("android.widget.TextSwitcher", false, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.3, 0.7)
            }
        if (!isOpenedPage && (searchBoxs == null || searchBoxs.isEmpty())) {
            searchBoxs = getUiObject2s("android.widget.TextView", false, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.01, 0.5)
        }
        searchBoxs?.firstOrNull()?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
    }

    fun setTextAndGo(text: String) {
        val searchBoxs = getUiObject2s("android.widget.EditText", true, 0.5, 1.0, 0.01, 0.5, 0.0, 1.0, 0.02, 0.4)
        searchBoxs?.firstOrNull()?.setText(text)
        sleep(TIMEOUT_VERY_SHORT.toLong())
        val goSearch = getUiObject2s("android.widget.ImageView", true, 0.01, 0.3, 0.01, 0.3, 0.5, 1.0, 0.0, 0.3)
        goSearch?.getOrNull(1)?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
    }

    fun switchFeedsTab(tabName: String) {
        getUiObject2s("android.widget.FrameLayout", true, 0.0, 0.2, 0.0, 0.2, 0.8, 1.0, 0.1, 0.6)?.firstOrNull()?.click()
        sleep(TIMEOUT_SHORT.toLong())
        getUiObject2ByChildText("android.widget.FrameLayout", true, tabName, "android.widget.TextView")?.click()
        val done = waitUiObject2ByText("Done", TIMEOUT_SHORT)
        if (done != null) {
            done.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
            getUiObject2ByChildText("android.widget.FrameLayout", true, tabName, "android.widget.TextView")?.click()
        }
        sleep(TIMEOUT_MEDIUM.toLong())
    }

    fun clickFeedsNews() {
        var news = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.1, 0.3, 0.0, 1.0, 0.02, 0.8)
        if (news == null || news.isEmpty()) {
            repeat(3) {
                swip(0.5, 0.7, 0.5, 0.3)
                sleep(TIMEOUT_MEDIUM.toLong())
                news = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.1, 0.3, 0.0, 1.0, 0.02, 0.8)
                if (news != null) return@repeat
            }
        }
        news?.firstOrNull()?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
    }

    fun clickFeedsVideo() {
        val firstVideo = getUiObject2s("android.widget.LinearLayout", true, 0.9, 1.0, 0.2, 0.8, 0.0, 1.0, 0.02, 0.9)?.firstOrNull() ?: return
        val firstVideoBottom = getChildUiObject2(firstVideo, false, "android.widget.LinearLayout", 0.8, 1.0, 0.0, 0.1, 0.0, 1.0, 0.0, 1.0, false)
        firstVideoBottom?.click()
        sleep(TIMEOUT_VERY_SHORT.toLong())
    }

    fun clickFeedsMiniVideo() {
        getUiObject2s("android.widget.FrameLayout", true, 0.4, 0.6, 0.2, 0.8, 0.0, 1.0, 0.02, 0.9)?.firstOrNull()?.click()
        sleep(TIMEOUT_SHORT.toLong())
        val swipeToast = waitUiObject2ByText("Swipe up for more", TIMEOUT_SHORT)
        if (swipeToast != null) {
            swipeToast.click()
            sleep(TIMEOUT_VERY_SHORT.toLong())
        }
    }

    fun getScrollableClazz(): String {
        return when {
            getScrollableUiObject2("androidx.recyclerview.widget.RecyclerView") != null -> "androidx.recyclerview.widget.RecyclerView"
            getScrollableUiObject2("android.support.v7.widget.RecyclerView") != null -> "android.support.v7.widget.RecyclerView"
            getScrollableUiObject2("android.webkit.WebView") != null -> "android.webkit.WebView"
            getScrollableUiObject2("android.widget.ListView") != null -> "android.widget.ListView"
            getScrollableUiObject2("android.view.ViewGroup") != null -> "android.view.ViewGroup"
            else -> {
                var scrollableClazz = ""
                val uiObject2s = getAllUiObject2s(getRootObject())
                for (uiObject2 in uiObject2s) {
                    if (uiObject2.isScrollable) {
                        scrollableClazz = uiObject2.className
                        break
                    }
                }
                scrollableClazz
            }
        }
    }

    fun closeAdDialog() {
        var closeBtn = waitUiObject2ByRes("close-button-container", TIMEOUT_MEDIUM.toLong())
        if (closeBtn == null) closeBtn = waitUiObject2ByText("CLOSE", TIMEOUT_VERY_SHORT)
        if (closeBtn != null) {
            try {
                closeBtn.click()
            } catch (e: Exception) {
                ShellCommon.pressHome(device, null)
                sleep(TIMEOUT_SHORT.toLong())
                ShellCommon.amStartApp(device, activity, null)
            }
            sleep(TIMEOUT_SHORT.toLong())
        } else {
            var closeBtns = getUiObject2s("android.widget.Button", true, 0.05, 0.3, 0.05, 0.3, 0.0, 1.0, 0.0, 0.3)
            if (closeBtns == null || closeBtns.isEmpty()) {
                closeBtns = getUiObject2s("android.widget.ImageButton", true, 0.05, 0.3, 0.05, 0.3, 0.0, 1.0, 0.0, 0.3)
            }
            if (closeBtns != null && closeBtns.isNotEmpty()) {
                try {
                    closeBtns[0].click()
                } catch (e: Exception) {
                    ShellCommon.pressHome(device, null)
                    sleep(TIMEOUT_SHORT.toLong())
                    ShellCommon.amStartApp(device, activity, null)
                }
                sleep(TIMEOUT_SHORT.toLong())
            }
        }
    }
}
