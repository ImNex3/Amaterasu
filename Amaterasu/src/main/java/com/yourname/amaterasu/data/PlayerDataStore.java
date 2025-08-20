package com.yourname.amaterasu.data;

import com.yourname.amaterasu.ability.AbilityType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataStore {
    private final Plugin plugin;
    private final Map<UUID, PlayerData> cache = new HashMap<>();

    public PlayerDataStore(Plugin plugin) { this.plugin = plugin; }

    public PlayerData get(Player p) { return get(p.getUniqueId()); }
    public PlayerData get(UUID id) {
        return cache.computeIfAbsent(id, uuid -> {
            FileConfiguration cfg = plugin.getConfig();
            String base = "players."+uuid+".";
            String sel = cfg.getString(base+"selected", AbilityType.DRAGON_WRATH.name());
            boolean rolled = cfg.getBoolean(base+"hasRolled", false);
            PlayerData d = new PlayerData(uuid);
            d.selected(AbilityType.valueOf(sel));
            d.hasRolled(rolled);
            return d;
        });
    }

    public void save(PlayerData d) {
        FileConfiguration cfg = plugin.getConfig();
        String base = "players."+d.id()+".";
        cfg.set(base+"selected", d.selected().name());
        cfg.set(base+"hasRolled", d.hasRolled());
        plugin.saveConfig();
    }
    public void saveAll() { cache.values().forEach(this::save); }
}
