/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.tyrian;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.adplug.AdPlugFormat;
import com.b3dgs.lionengine.audio.wav.WavFormat;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.graphic.engine.Loader;

/**
 * Program starts here.
 */
public final class AppTyrian
{
    /** Default display. */
    private static final Resolution DEFAULT_RESOLUTION = new Resolution(540, 960, 60);

    /**
     * Main function.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args) // CHECKSTYLE IGNORE LINE: TrailingComment|UncommentedMain
    {
        EngineAwt.start(Constant.NAME, Constant.VERSION, AppTyrian.class);
        AudioFactory.addFormat(new WavFormat());
        AudioFactory.addFormat(AdPlugFormat.getFailsafe());
        Sfx.setEnabled(true);
        Loader.start(Config.windowed(DEFAULT_RESOLUTION), Loading.class);
    }

    /**
     * Private constructor.
     */
    private AppTyrian()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
