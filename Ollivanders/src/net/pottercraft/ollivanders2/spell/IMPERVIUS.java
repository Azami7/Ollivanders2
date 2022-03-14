package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Protective charm for curses like flagrante
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Impervius_Charm
 */
public class IMPERVIUS extends O2Spell
{
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public IMPERVIUS(Ollivanders2 plugin)
    {
        super(plugin);
    }

    @Override
    protected void doCheckEffect()
    {
    }
}
