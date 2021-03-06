package com.elmakers.mine.bukkit.integration;

import com.elmakers.mine.bukkit.api.magic.Mage;
import com.elmakers.mine.bukkit.api.magic.MageController;
import com.garbagemule.MobArena.events.ArenaPlayerJoinEvent;
import com.garbagemule.MobArena.events.ArenaPlayerLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class MobArenaManager implements Listener {
    private final MageController controller;

    public MobArenaManager(MageController controller) {
        this.controller = controller;

        Set<String> magicMobKeys = controller.getMobKeys();
        for (String mob : magicMobKeys) {
            // Have to obey special MobArena naming restrictions
            String mobKey = mob.toLowerCase().replaceAll("[-_\\.]", "");
            new MagicMACreature(controller, mobKey, controller.getMob(mob));
        }

        Bukkit.getPluginManager().registerEvents(this, controller.getPlugin());
    }

    @EventHandler
    public void onPlayerJoinArena(ArenaPlayerJoinEvent event) {
        Player player = event.getPlayer();
        Mage mage = controller.getRegisteredMage(player.getUniqueId().toString());
        if (mage != null) {
            mage.deactivate();
        }
    }

    @EventHandler
    public void onPlayerLeaveArena(ArenaPlayerLeaveEvent event) {
        Player player = event.getPlayer();
        Mage mage = controller.getRegisteredMage(player.getUniqueId().toString());
        if (mage != null) {
            mage.deactivate();
        }
    }

    // Hopefully can use this again one day for custom item provider
    public ItemStack getItem(String s) {
        if (!s.startsWith("magic:")) return null;
        s = s.substring(6);
        return controller.createItem(s);
    }
}
