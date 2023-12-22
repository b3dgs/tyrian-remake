/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.tyrian.bonus.action;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.entity.ShipModel;
import com.b3dgs.tyrian.entity.WeaponModel;

/**
 * Change weapon action.
 */
@FeatureInterface
public final class ChangeWeapon extends FeatureModel implements Action
{
    private static final String NODE_WEAPON_FRONT = "weaponFront";
    private static final String NODE_WEAPON_REAR = "weaponRear";

    private final Factory factory = services.get(Factory.class);

    private final Identifiable identifiable;

    private final Media media;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param identifiable The identifiable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public ChangeWeapon(Services services, Setup setup, Identifiable identifiable)
    {
        super(services, setup);

        this.identifiable = identifiable;

        if (setup.hasNode(NODE_WEAPON_FRONT))
        {
            media = Medias.create(Constant.FOLDER_WEAPON,
                                  Constant.FOLDER_FRONT,
                                  setup.getText(NODE_WEAPON_FRONT) + Factory.FILE_DATA_DOT_EXTENSION);
        }
        else if (setup.hasNode(NODE_WEAPON_REAR))
        {
            media = Medias.create(Constant.FOLDER_WEAPON,
                                  Constant.FOLDER_REAR,
                                  setup.getText(NODE_WEAPON_REAR) + Factory.FILE_DATA_DOT_EXTENSION);
        }
        else
        {
            media = null;
        }
    }

    @Override
    public void action(ShipModel ship)
    {
        if (media != null)
        {
            final Featurable weapon = factory.create(media);
            ship.takeWeapon(weapon.getFeature(WeaponModel.class));
        }
        identifiable.destroy();
    }
}
