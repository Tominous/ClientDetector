/*
 * This file is part of ClientDetector - https://github.com/Sportkanone123/ClientDetector
 * Copyright (C) 2021 Sportkanone123
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.sportkanone123.clientdetector.spigot.manager;

import de.sportkanone123.clientdetector.spigot.ClientDetector;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderManager extends PlaceholderExpansion {
    static Plugin plugin = ClientDetector.plugin;

    public boolean persist() {
        return true;
    }

    public boolean canRegister() {
        return true;
    }

    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    public String getIdentifier() {
        return "clientdetector";
    }

    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        if(identifier.equalsIgnoreCase("client_name")){
            if(ClientDetector.playerClient.get(player) != null)
                return ClientDetector.playerClient.get(player);
            return "Vanilla Minecraft / Undetectable Client";
        }
        if(identifier.equalsIgnoreCase("client_version")){
            if(ClientDetector.clientVersion.get(player) != null)
                return ClientDetector.clientVersion.get(player);
            return null;
        }
        if(identifier.equalsIgnoreCase("forge_user")){
            if(ClientDetector.forgeMods.get(player).getMods() == null || ClientDetector.forgeMods.get(player).getMods().isEmpty())
                return "false";
            return "true";
        }
        if(identifier.equalsIgnoreCase("bedrock_player")){
            return String.valueOf(GeyserManager.isBedrockPlayer(player));
        }
        return null;
    }
}
