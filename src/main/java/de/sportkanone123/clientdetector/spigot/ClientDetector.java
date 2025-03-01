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

package de.sportkanone123.clientdetector.spigot;

import de.sportkanone123.clientdetector.bungeecord.utils.CustomPayload;
import de.sportkanone123.clientdetector.spigot.client.Client;
import de.sportkanone123.clientdetector.spigot.clientcontrol.ClientControl;
import de.sportkanone123.clientdetector.spigot.command.Command;
import de.sportkanone123.clientdetector.spigot.forgemod.ModList;
import de.sportkanone123.clientdetector.spigot.hackdetector.HackDetector;
import de.sportkanone123.clientdetector.spigot.hackdetector.impl.AntiFastMath;
import de.sportkanone123.clientdetector.spigot.listener.NetworkListener;
import de.sportkanone123.clientdetector.spigot.listener.PlayerListener;
import de.sportkanone123.clientdetector.spigot.listener.PluginMessageListener;
import de.sportkanone123.clientdetector.spigot.manager.*;
import de.sportkanone123.clientdetector.spigot.mod.Mod;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.settings.PacketEventsSettings;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class ClientDetector extends JavaPlugin {
    public static Plugin plugin;
    public static ArrayList<Client> CLIENTS = new ArrayList<Client>();
    public static ArrayList<Mod> MODS = new ArrayList<Mod>();

    public static HashMap<Player, String> mcVersion = new HashMap<Player, String>();
    public static HashMap<Player, ModList> forgeMods = new HashMap<Player, ModList>();
    public static HashMap<Player, String> playerClient = new HashMap<Player, String> ();
    public static HashMap<Player, String> clientVersion = new HashMap<Player, String> ();
    public static HashMap<Player, ArrayList<String>> playerMods = new HashMap<Player, ArrayList<String>> ();
    public static HashMap<Player, ArrayList<String>> playerLabymodMods = new HashMap<Player, ArrayList<String>> ();
    public static HashMap<UUID, ArrayList<CustomPayload>> bungeePayload = new HashMap<UUID, ArrayList<CustomPayload>> ();

    private PacketEvents instance;

    @Override
    public void onLoad() {
        instance = PacketEvents.create(this);
        PacketEventsSettings settings = instance.getSettings();
        settings.checkForUpdates(false)
                .fallbackServerVersion(ServerVersion.v_1_16_5)
                .compatInjector(false)
                .bStats(false);
        instance.load();
    }

    @Override
    public void onEnable() {
        plugin = this;

        new MetricsManager(this, 10745);

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3ClientDetector&7] (&aVersion&7) &aDetected Version &c" + PacketEvents.get().getServerUtils().getVersion().name()));
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3ClientDetector&7] (&aVersion&7) &aLoading settings for Version &c" + PacketEvents.get().getServerUtils().getVersion().name()));

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3ClientDetector&7] (&aProtocol&7) &aLoading protocols..."));
        instance.registerListener(new NetworkListener());
        instance.init();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "clientdetector:sync");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "lunarclient:pm");

        Bukkit.getMessenger().registerIncomingPluginChannel(this, "clientdetector:sync", new PluginMessageListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "clientdetector:fix", new PluginMessageListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "lunarclient:pm", new PluginMessageListener());

        if(PacketEvents.get().getServerUtils().getVersion().isOlderThanOrEquals(ServerVersion.v_1_12_2))
            Bukkit.getMessenger().registerIncomingPluginChannel((Plugin)this, "CB-Client", new PluginMessageListener());

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3ClientDetector&7] (&aConfig&7) &aLoading config(s)..."));
        saveDefaultConfig();

        getCommand("clientdetector").setExecutor(new Command());
        getCommand("client").setExecutor(new Command());
        getCommand("forge").setExecutor(new Command());
        getCommand("mods").setExecutor(new Command());

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ClientControl(), this);
        Bukkit.getPluginManager().registerEvents(new HackDetector(), this);

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3ClientDetector&7] (&aDetection&7) &aLoading client detections..."));
        ClientManager.load();
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3ClientDetector&7] (&aDetection&7) &aLoading mod detections..."));
        ModManager.load();

        AlertsManager.load();

        DiscordManager.load();

        AntiFastMath.load();

        if(Bukkit.getServer().getPluginManager().isPluginEnabled("ViaVersion")){
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3ClientDetector&7] (&aViaVersion&7) &aDetected ViaVersion " + Bukkit.getPluginManager().getPlugin("ViaVersion").getDescription().getVersion()));
        }

        if(Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib")){
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3ClientDetector&7] (&aProtocolLib&7) &aDetected ProtocolLib " + Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion()));
        }

        if(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3ClientDetector&7] (&aPlaceholderAPI&7) &aDetected PlaceholderAPI " + Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion()));
        }

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && getConfig().getBoolean("placeholder.enablePlaceholder"))
            new PlaceholderManager().register();

        try {
            ConfigManager.loadConfig("message");
            ConfigManager.loadConfig("clientcontrol");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&3ClientDetector&7] &aStarted!"));

    }

    @Override
    public void onDisable() {
        instance.terminate();

        Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin);

        ClientManager.unLoad();
        ModManager.unLoad();
    }
}
