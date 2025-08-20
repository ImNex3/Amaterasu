package com.yourname.amaterasu.listeners;

import com.yourname.amaterasu.ability.AbilityType;
import com.yourname.amaterasu.data.PlayerData;
import com.yourname.amaterasu.data.PlayerDataStore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JoinListener implements Listener {
    private final com.yourname.amaterasu.AmaterasuPlugin plugin;
    private final PlayerDataStore store;

    public JoinListener(com.yourname.amaterasu.AmaterasuPlugin plugin, PlayerDataStore store) {
        this.plugin = plugin;
        this.store = store;
    }

    @EventHandler public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PlayerData d = store.get(p);
        if (!d.hasRolled()) {
            List<AbilityType> all = Arrays.asList(AbilityType.values());
            Collections.shuffle(all);
            AbilityType selected = all.get(0);
            d.selected(selected);
            d.hasRolled(true);
            store.save(d);

            p.sendMessage(ChatColor.GOLD + "Your abilities have been rolled in an incomplete state:");
            p.sendMessage(ChatColor.YELLOW + "- " + selected.display() + ": " + selected.desc());
            p.sendMessage(ChatColor.RED + "These abilities may be cool, but they are in incomplete states!");
        }
    }
}
