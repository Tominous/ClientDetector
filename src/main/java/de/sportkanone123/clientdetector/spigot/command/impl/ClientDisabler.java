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

package de.sportkanone123.clientdetector.spigot.command.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ClientDisabler {
    public static boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 3 && Bukkit.getPlayer(args[1]) != null){
            if(args[2].equalsIgnoreCase("aristois")){
                de.sportkanone123.clientdetector.spigot.clientdisabler.ClientDisabler.checkAristois(sender, Bukkit.getPlayer(args[1]));
            }
        }else if(args.length != 3){

        }

        return false;
    }
}
