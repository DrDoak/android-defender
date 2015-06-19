package com.luoleizhao2018.frame;

/**
 * Created by Larry on 6/15/2015.
 */

import com.luoleizhao2018.frame.Graphics.ImageFormat;

public interface Image {
    public int getWidth();
    public int getHeight();
    public ImageFormat getFormat();
    public void dispose();
}
