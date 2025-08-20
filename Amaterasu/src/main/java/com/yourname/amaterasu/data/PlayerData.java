package com.yourname.amaterasu.data;

import com.yourname.amaterasu.ability.AbilityType;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private AbilityType selected = AbilityType.DRAGON_WRATH;
    private final Map<AbilityType, Boolean> rolled = new EnumMap<>(AbilityType.class);
    private boolean hasRolled = false;

    public PlayerData(UUID id) {
        this.uuid = id;
        for (AbilityType t : AbilityType.values()) rolled.put(t, Boolean.FALSE);
    }
    public UUID id() { return uuid; }
    public AbilityType selected() { return selected; }
    public void selected(AbilityType t) { selected = t; }
    public boolean hasRolled() { return hasRolled; }
    public void hasRolled(boolean v) { hasRolled = v; }
}
