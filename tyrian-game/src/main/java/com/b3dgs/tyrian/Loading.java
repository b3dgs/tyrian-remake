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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.Sequence;

/**
 * Loading screen.
 */
public final class Loading extends Sequence
{
    private static final String TEXT_LOADING = "Loading... ";
    private static final int LOADING_OFFSET_Y = 50;

    private final Image background = Drawable.loadImage(Medias.create(Constant.FOLDER_PIC, "loading.png"));
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Constant.FOLDER_FONT, "font.png"),
                                                            Medias.create(Constant.FOLDER_FONT, "font.xml"),
                                                            8,
                                                            9);

    private boolean loaded;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Loading(Context context)
    {
        super(context, Constant.NATIVE);

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        background.load();
        background.prepare();

        font.load();
        font.prepare();

        font.setText(TEXT_LOADING);
        font.setAlign(Align.CENTER);
        font.setLocation(getWidth() / 2, getHeight() / 2 + LOADING_OFFSET_Y);
    }

    @Override
    public void update(double extrp)
    {
        if (loaded)
        {
            end(Scene.class);
        }
        loaded = true;
    }

    @Override
    public void render(Graphic g)
    {
        background.render(g);
        font.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        background.dispose();
        font.dispose();
    }
}
