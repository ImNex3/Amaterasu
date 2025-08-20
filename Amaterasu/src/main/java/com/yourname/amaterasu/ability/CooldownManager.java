package com.yourname.amaterasu.ability;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<String, Long> map = new HashMap<>();
    private String key(UUID id, AbilityType t) { return id + ":" + t.name(); }

    public void set(Player p, AbilityType t, long millis) {
        map.put(key(p.getUniqueId(), t), System.currentTimeMillis() + millis);
    }
    public long remaining(Player p, AbilityType t) {
        Long end = map.get(key(p.getUniqueId(), t));
        if (end == null) return 0L; 
        return Math.max(0L, end - System.currentTimeMillis());
    }
    public boolean ready(Player p, AbilityType t) { return remaining(p, t) == 0L; }
}
