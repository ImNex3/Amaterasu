package com.yourname.amaterasu.ability;

public enum AbilityType {
    DEVILS_FOOTPRINT,
    PLASMA_BLESSING,
    DRAGON_WRATH,
    HELLFLAME,
    FLARE_REBIRTH,
    PIERCINGSPIKE /* typo-protected? kept command name piercingspike */,
    PIERCING_SPIKE;

    public String display() {
        return switch (this) {
            case DEVILS_FOOTPRINT -> "Devil's Footprint";
            case PLASMA_BLESSING -> "Plasma Blessing";
            case DRAGON_WRATH -> "Dragon's Wrath";
            case HELLFLAME -> "Hellflame";
            case FLARE_REBIRTH -> "Flare Rebirth";
            case PIERCINGSPIKE, PIERCING_SPIKE -> "Piercing Spike";
        };
    }

    public String desc() {
        return switch (this) {
            case DEVILS_FOOTPRINT -> "Dash forward leaving flames.";
            case PLASMA_BLESSING -> "Store hits and release plasma AoE.";
            case DRAGON_WRATH -> "Spiral leap; shockwave on landing.";
            case HELLFLAME -> "Rune web that scorches and pulls.";
            case FLARE_REBIRTH -> "Phoenix cloak; invulnerable briefly.";
            case PIERCINGSPIKE, PIERCING_SPIKE -> "Erupt spikes in waves.";
        };
    }
}
