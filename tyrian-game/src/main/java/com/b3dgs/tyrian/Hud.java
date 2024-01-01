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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.tyrian.entity.ShipModel;

/**
 * HUD representation.
 */
public class Hud implements Updatable, Renderable
{
    private static final Media HUD = Medias.create(Constant.FOLDER_SPRITE, "hud.png");
    private static final ColorRgba BROWN = new ColorRgba(90, 45, 0);

    private static Bar createBar(int width, int height, int x, int y, ColorRgba color)
    {
        final Bar bar = new Bar(width, height);
        bar.setLocation(x, y);
        bar.setColor(ColorRgba.BLACK, color);
        return bar;
    }

    private static Bar createBar(int width, int height, int x, int y, ColorRgba gradientA, ColorRgba gradientB)
    {
        final Bar bar = createBar(width, height, x, y, ColorRgba.BLACK);
        bar.setColorGradient(gradientA, gradientB);
        return bar;
    }

    private final Bar shield = createBar(60, 9, 12, 334, ColorRgba.BLUE);
    private final Bar armor = createBar(60, 9, 12, 371, BROWN);
    private final Bar energy = createBar(96, 11, 108, 332, ColorRgba.RED, ColorRgba.YELLOW);
    private final Bar levelFront = createBar(4, 12, 198, 348, ColorRgba.YELLOW, ColorRgba.RED);
    private final Bar levelRear = createBar(4, 12, 198, 364, ColorRgba.YELLOW, ColorRgba.RED);
    private final Bar progress = createBar(12, 49, 81, 331, ColorRgba.GREEN, ColorRgba.RED);
    private final Sprite surface = Drawable.loadSprite(HUD);
    private final ShipModel ship;
    private final Camera camera;
    private final MapTile map;

    /**
     * Create hud.
     * 
     * @param services The services reference.
     */
    public Hud(Services services)
    {
        super();

        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
        ship = services.get(ShipModel.class);

        surface.load();
        surface.prepare();
        surface.setLocation(0.0, camera.getHeight() - surface.getHeight());
    }

    /**
     * Get the height.
     * 
     * @return The height.
     */
    public int getHeight()
    {
        return surface.getHeight();
    }

    /**
     * Get the front weapon level.
     * 
     * @return The front weapon level.
     */
    private int getLevelPercentFront()
    {
        return (int) Math.max(1, Math.floor(ship.getFront().getLevel() * 100.0 / Constant.WEAPON_LEVEL_MAX));
    }

    /**
     * Get the rear weapon level.
     * 
     * @return The rear weapon level.
     */
    private int getLevelPercentRear()
    {
        return (int) Math.max(1, Math.floor(ship.getRear().getLevel() * 100.0 / Constant.WEAPON_LEVEL_MAX));
    }

    @Override
    public void update(double extrp)
    {
        shield.setWidthPercent(ship.getShield().getPercent());
        armor.setWidthPercent(ship.getArmor().getPercent());
        energy.setWidthPercent(ship.getEnergy().getPercent());

        levelFront.setHeightPercent(getLevelPercentFront());
        levelRear.setHeightPercent(getLevelPercentRear());

        progress.setHeightPercent((int) Math.floor((camera.getY() + camera.getHeight()) * 100.0 / map.getHeight()));
    }

    @Override
    public void render(Graphic g)
    {
        shield.render(g);
        armor.render(g);
        energy.render(g);

        surface.render(g);

        levelFront.render(g);
        levelRear.render(g);

        progress.render(g);
    }
}
