package com.kinglloy.album.presenter

import com.kinglloy.album.domain.StyleWallpaperSettings
import com.kinglloy.album.domain.interactor.DefaultObserver
import com.kinglloy.album.domain.interactor.settings.GetStyleWallpaperSettings
import com.kinglloy.album.domain.interactor.settings.UpdateStyleSettings
import com.kinglloy.album.mapper.SettingsWallpaperItemMapper
import com.kinglloy.album.view.SettingsView
import javax.inject.Inject

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
class SettingsPresenter
@Inject constructor(private val getStyleWallpaperSettings: GetStyleWallpaperSettings,
                    private val updateStyleSettings: UpdateStyleSettings) : Presenter {

    private var settingsView: SettingsView? = null
    private var currentSettings: StyleWallpaperSettings? = null

    fun initialize() {
        getStyleWallpaperSettings.execute(object : DefaultObserver<StyleWallpaperSettings>() {
            override fun onNext(t: StyleWallpaperSettings) {
                currentSettings = t
                settingsView?.renderStyleSettings(
                        SettingsWallpaperItemMapper.transformStyleSettings(t))
            }
        }, null)
    }

    fun setView(settingsView: SettingsView) {
        this.settingsView = settingsView
    }

    fun enableStyleEffect(enable: Boolean) {
        if (currentSettings != null) {
            currentSettings!!.enableEffect = enable
            updateStyleSettings.execute(DefaultObserver<Boolean>(),
                    UpdateStyleSettings.Params.with(currentSettings))
        }
    }

    fun setStyleBlur(blur: Int) {
        if (currentSettings != null) {
            currentSettings!!.blur = blur
            updateStyleSettings.execute(DefaultObserver<Boolean>(),
                    UpdateStyleSettings.Params.with(currentSettings))
        }
    }

    fun setStyleDim(dim: Int) {
        if (currentSettings != null) {
            currentSettings!!.dim = dim
            updateStyleSettings.execute(DefaultObserver<Boolean>(),
                    UpdateStyleSettings.Params.with(currentSettings))
        }
    }

    fun setStylegrey(grey: Int) {
        if (currentSettings != null) {
            currentSettings!!.grey = grey
            updateStyleSettings.execute(DefaultObserver<Boolean>(),
                    UpdateStyleSettings.Params.with(currentSettings))
        }
    }


    override fun resume() {
    }

    override fun pause() {
    }

    override fun destroy() {
    }

}