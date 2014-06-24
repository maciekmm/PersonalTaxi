/*
 * Copyright (C) 2014 maciekmm <maciekmm@maciekmm.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.maciekmm.personalTaxi.menu;

import net.maciekmm.personalTaxi.DestinationManagement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

//TODO: Pages?
public class DestinationMenu implements InventoryHolder {
    private Inventory inventory;
    private final DestinationManagement manager;
    private final Player player;

    public DestinationMenu(DestinationManagement manager, Player player) {
        this.manager = manager;
        this.player = player;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void setInventory(Inventory inv) {
        this.inventory = inv;
    }

    public DestinationManagement getManager() {
        return this.manager;
    }

    public Player getPlayer() {
        return this.player;
    }
}
