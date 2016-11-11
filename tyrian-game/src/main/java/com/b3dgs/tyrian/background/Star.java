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
package com.b3dgs.tyrian.background;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.util.UtilRandom;

/**
 * Star implementation.
 */
final class Star implements Updatable, Localizable
{
    /** Horizontal range. */
    private final Range h;
    /** Vertical range. */
    private final Range v;
    /** Horizontal vector. */
    private final double vx;
    /** Vertical vector. */
    private final double vy;
    /** Star id. */
    private final int id;
    /** Horizontal location. */
    private double x;
    /** Vertical location. */
    private double y;

    /**
     * Create a star.
     * 
     * @param h The horizontal range.
     * @param v The vertical range.
     * @param vx The horizontal vector.
     * @param vy The vertical vector.
     * @param id The id.
     */
    Star(Range h, Range v, double vx, double vy, int id)
    {
        this.h = h;
        this.v = v;
        this.vx = vx;
        this.vy = vy;
        this.id = id;
        x = UtilRandom.getRandomInteger(h);
        y = UtilRandom.getRandomInteger(v);
    }

    /**
     * Get the id.
     * 
     * @return The id.
     */
    public int getId()
    {
        return id;
    }

    @Override
    public void update(double extrp)
    {
        x += vx * extrp;
        y -= vy * extrp;
        if (y < v.getMin())
        {
            y = v.getMax();
            x = UtilRandom.getRandomInteger(h);
        }
    }

    @Override
    public double getX()
    {
        return x;
    }

    @Override
    public double getY()
    {
        return y;
    }
}
