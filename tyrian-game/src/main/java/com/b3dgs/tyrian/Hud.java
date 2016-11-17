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
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.tyrian.ship.ShipModel;

/**
 * HUD representation.
 */
public class Hud implements Updatable, Renderable
{
    /** Stalker media. */
    public static final Media HUD = Medias.create(Constant.FOLDER_SPRITE, "hud.png");

    private final Bar shield = new Bar(60, 9);
    private final Bar armor = new Bar(60, 9);
    private final Bar energy = new Bar(96, 11);
    private final Sprite surface;
    private ShipModel ship;

    /**
     * Create HUD.
     * 
     * @param services The services reference.
     */
    public Hud(Services services)
    {
        super();

        surface = Drawable.loadSprite(HUD);
        surface.load();
        surface.prepare();
        surface.setLocation(0.0, services.get(Viewer.class).getHeight() - surface.getHeight());

        shield.setLocation(4, 350);
        shield.setColorBackground(ColorRgba.BLACK);
        shield.setColorForeground(ColorRgba.BLUE);

        armor.setLocation(4, 387);
        armor.setColorBackground(ColorRgba.BLACK);
        armor.setColorForeground(new ColorRgba(90, 45, 0));

        energy.setLocation(140, 348);
        energy.setColorBackground(ColorRgba.BLACK);
        energy.setColorGradient(140, 348, ColorRgba.RED, 237, 359, ColorRgba.YELLOW);
    }

    /**
     * Set the ship data to display.
     * 
     * @param ship The ship data to display.
     */
    public void setShip(ShipModel ship)
    {
        this.ship = ship;
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

    @Override
    public void update(double extrp)
    {
        shield.setWidthPercent(ship.getShield().getPercent());
        armor.setWidthPercent(ship.getArmor().getPercent());
        energy.setWidthPercent(ship.getEnergy().getPercent());
    }

    @Override
    public void render(Graphic g)
    {
        shield.render(g);
        armor.render(g);
        energy.render(g);

        surface.render(g);
    }
}
