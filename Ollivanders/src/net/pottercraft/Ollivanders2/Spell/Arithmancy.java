package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * The super class for all ARITHMANCY projectile spells.
 *
 * @author Azami7
 */
public abstract class Arithmancy extends SpellProjectile implements Spell
{
   protected O2MagicBranch branch = O2MagicBranch.ARITHMANCY;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public Arithmancy () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public Arithmancy (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }
}

