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
package com.b3dgs.tyrian.bonus.action;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.Configurer;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.identifiable.Identifiable;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.ship.ShipUpdater;
import com.b3dgs.tyrian.weapon.Weapon;
import com.b3dgs.tyrian.weapon.WeaponModel;

/**
 * Change weapon action.
 */
public class ChangeWeapon extends FeatureModel implements Action
{
    private static final String NODE_WEAPON_FRONT = com.b3dgs.lionengine.Constant.XML_PREFIX + "weaponFront";
    private static final String NODE_WEAPON_REAR = com.b3dgs.lionengine.Constant.XML_PREFIX + "weaponRear";

    private final Media media;

    @Service private Factory factory;

    /**
     * Create power up action.
     * 
     * @param configurer The configurer reference.
     */
    public ChangeWeapon(Configurer configurer)
    {
        super();

        final XmlNode root = configurer.getRoot();
        if (root.hasChild(NODE_WEAPON_FRONT))
        {
            media = Medias.create(Constant.FOLDER_WEAPON,
                                  Constant.FOLDER_FRONT,
                                  configurer.getText(NODE_WEAPON_FRONT) + Factory.FILE_DATA_DOT_EXTENSION);
        }
        else if (root.hasChild(NODE_WEAPON_REAR))
        {
            media = Medias.create(Constant.FOLDER_WEAPON,
                                  Constant.FOLDER_REAR,
                                  configurer.getText(NODE_WEAPON_REAR) + Factory.FILE_DATA_DOT_EXTENSION);
        }
        else
        {
            media = null;
        }
    }

    @Override
    public void action(ShipUpdater ship)
    {
        if (media != null)
        {
            final Weapon weapon = factory.create(media);
            ship.takeWeapon(weapon.getFeature(WeaponModel.class));
        }
        getFeature(Identifiable.class).destroy();
    }
}
