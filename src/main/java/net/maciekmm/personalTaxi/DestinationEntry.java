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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.List;

public class DestinationEntry {
    private ItemStack icon;
    private final Vector location;
    private String name, permission;
    private final World world;
    private List<String> description;

    public static DestinationEntry fromConfig(World world, ConfigurationSection section) {
        double pointX = section.getDouble("x");
        double pointZ = section.getDouble("z");
        Material material = Material.matchMaterial(section.getString("icon"));
        if (material == null) {
            material = Material.STONE;
        }
        ItemStack is = new ItemStack(material);
        DestinationEntry de = new DestinationEntry(new Vector(pointX, world.getHighestBlockYAt((int) pointX, (int) pointZ), pointZ), is, world);
        de.setName(ChatColor.translateAlternateColorCodes('&',section.getString("name", PersonalTaxi.getMessage("destination.name.default"))));
        de.setDescription(section.getStringList("description"));
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(de.getName());
        meta.setLore(de.getDescription());
        is.setItemMeta(meta);
        de.setPermission(section.getString("permission", "personaltaxi.access.default"));
        return de;
    }

    public DestinationEntry(Vector location, ItemStack icon, World world) {
        this.location = location;
        this.icon = icon;
        this.world = world;
        this.name = icon.getType().name();
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public Vector getLocation() {
        return this.location;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(this.permission);
    }

    public World getWorld() {
        return this.world;
    }

    public String getPermission() {
        return this.permission;
    }

    public List<String> getDescription() {
        return this.description;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }
}
