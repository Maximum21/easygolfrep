package com.minhhop.easygolf.listeners;

import com.minhhop.easygolf.framework.models.HolderGreen;
import com.minhhop.easygolf.framework.models.Tee;

public interface SegmentOnListener {
    void onChooseGreen(HolderGreen holderGreen);
    void onChooseTee(Tee tee);
    void onClose();
    void onOpen();
}
