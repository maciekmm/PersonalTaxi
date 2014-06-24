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

package net.maciekmm.personalTaxi;

import com.google.common.collect.ImmutableList;
import lombok.Setter;
import net.maciekmm.personalTaxi.menu.DestinationMenu;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;

public class DestinationManagement {
    private final World world;
    private ImmutableList<DestinationEntry> destinations;

    public static DestinationManagement fromConfig(ConfigurationSection section) {
        World world = Bukkit.getWorld(section.getString("world"));
        if (world == null) {
            throw new IllegalArgumentException(String.format("World %s is not available.", section.getString("world")));
        }
        List<Map<?, ?>> destinations = section.getMapList("destinations");
        ImmutableList.Builder<DestinationEntry> builder = new ImmutableList.Builder<>();
        for (Map<?, ?> destination : destinations) {
            builder.add(DestinationEntry.fromConfig(world, new MemoryConfiguration().createSection("temporary", destination)));
        }
        return new DestinationManagement(world, builder.build());
    }

    public DestinationManagement(World world, ImmutableList<DestinationEntry> destinations) {
        this.world = world;
        this.destinations = destinations;
    }

    public ImmutableList<DestinationEntry> getDestinations() {
        return this.destinations;
    }

    public Inventory getMenuForPlayer(Player player) {
        DestinationMenu menu = new DestinationMenu(this, player);
        Inventory inv = Bukkit.createInventory(menu, Math.min(54, Math.max((int) Math.ceil(destinations.size() / 9),1) * 9), PersonalTaxi.getMessage("destination.inventory.name"));
        int i = 0;
        for (DestinationEntry entry : this.destinations) {
            if (entry.hasPermission(player)) {
                inv.setItem(i, entry.getIcon());
                i++;
            }
        }
        menu.setInventory(inv);
        return inv;
    }

    //TODO: TEST THIS SHIT :D
    public DestinationEntry getEntryFromIndex(Player player, int index) {
        int i = 0;
        for (DestinationEntry entry : this.destinations) {
            if (entry.hasPermission(player)) {
                if (i == index) {
                    return entry;
                }
                i++;
            }
        }
        return null;
    }

    public World getWorld() {
        return this.world;
    }
}
