package com.nhatton.ggtalkvn.ui.main;

import com.nhatton.ggtalkvn.data.Sound;

public interface SoundCallback {
    void onSoundSelected(Sound sound);

    void onFullScreen(Sound sound);

    void onExport(Sound sound);
}
