/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.tyrian;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.tyrian.ship.ShipModel;
import com.b3dgs.tyrian.ship.ShipUpdater;

/**
 * HUD representation.
 */
public class Hud implements Updatable, Renderable
{
    /** Stalker media. */
    public static final Media HUD = Medias.create(Constant.FOLDER_SPRITE, "hud.png");
    private static final ColorRgba BROWN = new ColorRgba(90, 45, 0);

    private final Bar shield = new Bar(60, 9);
    private final Bar armor = new Bar(60, 9);
    private final Bar energy = new Bar(96, 11);
    private final Bar levelFront = new Bar(4, 12);
    private final Bar levelRear = new Bar(4, 12);
    private final Bar progress = new Bar(12, 49);
    private final Sprite surface = Drawable.loadSprite(HUD);
    private final ShipModel ship;
    private final ShipUpdater shipUpdater;
    private final Camera camera;
    private final MapTile map;

    /**
     * Create HUD.
     * 
     * @param services The services reference.
     */
    public Hud(Services services)
    {
        super();

        camera = services.get(Camera.class);
        map = services.get(MapTile.class);

        surface.load();
        surface.prepare();
        surface.setLocation(0.0, services.get(Viewer.class).getHeight() - surface.getHeight());

        shipUpdater = services.get(ShipUpdater.class);
        ship = shipUpdater.getFeature(ShipModel.class);

        shield.setLocation(4, 305);
        shield.setColor(ColorRgba.BLACK, ColorRgba.BLUE);

        armor.setLocation(4, 342);
        armor.setColor(ColorRgba.BLACK, BROWN);

        energy.setLocation(100, 303);
        energy.setColorBackground(ColorRgba.BLACK);
        energy.setColorGradient(ColorRgba.RED, ColorRgba.YELLOW);

        levelFront.setLocation(190, 319);
        levelFront.setColorBackground(ColorRgba.BLACK);
        levelFront.setColorGradient(ColorRgba.YELLOW, ColorRgba.RED);

        levelRear.setLocation(190, 335);
        levelRear.setColorBackground(ColorRgba.BLACK);
        levelRear.setColorGradient(ColorRgba.YELLOW, ColorRgba.RED);

        progress.setLocation(73, 302);
        progress.setColorGradient(ColorRgba.GREEN, ColorRgba.RED);
    }

    @Override
    public void update(double extrp)
    {
        shield.setWidthPercent(ship.getShield().getPercent());
        armor.setWidthPercent(ship.getArmor().getPercent());
        energy.setWidthPercent(ship.getEnergy().getPercent());

        levelFront.setHeightPercent(shipUpdater.getLevelPercentFront());
        levelRear.setHeightPercent(shipUpdater.getLevelPercentRear());

        progress.setHeightPercent((int) ((camera.getY() + camera.getHeight()) * 100.0 / map.getHeight()));
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

    /**
     * Get the HUD height.
     * 
     * @return The height.
     */
    public int getHeight()
    {
        return surface.getHeight();
    }
}
