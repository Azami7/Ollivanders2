package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Super class for all shield spells
 */
public abstract class ShieldSpell extends O2StationarySpell
{
    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public ShieldSpell(@NotNull Ollivanders2 plugin)
    {
        super(plugin);
    }
}
