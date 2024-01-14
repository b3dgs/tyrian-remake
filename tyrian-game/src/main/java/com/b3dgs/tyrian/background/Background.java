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
package com.b3dgs.tyrian.background;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.tyrian.Constant;

/**
 * Background implementation with scrolling stars.
 */
public final class Background implements Updatable, Renderable
{
    /** Color background. */
    private static final ColorRgba COLOR = new ColorRgba(0, 0, 0);
    /** Default star density. */
    private static final int DENSITY = 128;
    /** Vertical speed range. */
    private static final Range VY_SPEED = new Range(20, 50);
    /** Vertical speed divisor. */
    private static final double VY_DIVISOR = 15.0;

    private final SpriteTiled surface;
    private final Star[] stars = new Star[DENSITY];
    private final Viewer viewer;
    private final ImageBuffer[] layers;
    private final double[] speed;
    private final double[] y;

    /**
     * Create a background.
     * 
     * @param viewer The viewer reference.
     */
    public Background(Viewer viewer)
    {
        super();

        this.viewer = viewer;

        final int h = 3;
        final int v = 6;
        surface = Drawable.loadSpriteTiled(Medias.create(Constant.FOLDER_BACKGROUND, "stars.png"), h, v);
        surface.load();

        final int minX = -surface.getTileWidth() - (int) Constant.MARGIN_H;
        final int maxX = viewer.getWidth() + surface.getTileWidth() * 2 + (int) Constant.MARGIN_H;
        final int layersNumber = surface.getTilesHorizontal() * surface.getTilesVertical();

        for (int i = 0; i < stars.length; i++)
        {
            stars[i] = new Star(new Range(minX, maxX),
                                new Range(surface.getTileHeight(), viewer.getHeight() + surface.getTileHeight()),
                                0.0,
                                UtilRandom.getRandomInteger(VY_SPEED) / VY_DIVISOR,
                                UtilRandom.getRandomInteger(0, layersNumber));
        }
        layers = new ImageBuffer[layersNumber];
        speed = new double[layersNumber];
        y = new double[layersNumber];
        for (final Star star : stars)
        {
            star.update(1.0);
        }
        final double factor = 0.5;
        for (int i = 0; i < layersNumber; i++)
        {
            final ImageBuffer buffer = Graphics.createImageBuffer(maxX - minX,
                                                                  viewer.getHeight(),
                                                                  ColorRgba.TRANSPARENT);
            layers[i] = buffer;
            speed[i] = layersNumber - i * factor;
            final Graphic g = buffer.createGraphic();
            for (final Star star : stars)
            {
                if (star.getId() == i)
                {
                    surface.setLocation(star.getX(), star.getY());
                    surface.setTile(i);
                    surface.render(g);
                }
            }
            g.dispose();
        }
    }

    @Override
    public void update(double extrp)
    {
        for (int i = 0; i < layers.length; i++)
        {
            y[i] = UtilMath.wrapDouble(y[i] + speed[i] * extrp, 0, viewer.getHeight());
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(COLOR);
        g.drawRect(0, 0, viewer.getWidth(), viewer.getHeight(), true);

        for (int i = 0; i < layers.length; i++)
        {
            g.drawImage(layers[i], (int) viewer.getViewpointX(0.0), (int) y[i]);
            g.drawImage(layers[i], (int) viewer.getViewpointX(0.0), (int) y[i] - viewer.getHeight());
        }
    }
}
