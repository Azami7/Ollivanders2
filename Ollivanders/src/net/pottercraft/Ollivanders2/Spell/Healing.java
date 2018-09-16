package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * The super class for all HEALING projectile spells.
 *
 * @author Azami7
 */
public abstract class Healing extends SpellProjectile implements Spell
{
   protected O2MagicBranch branch = O2MagicBranch.HEALING;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public Healing () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public Healing (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }
}
