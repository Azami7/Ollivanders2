package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * The super class for all CHARMS projectile spells.
 *
 * @author Azami7
 */
public abstract class Charms extends SpellProjectile implements Spell
{
   protected O2MagicBranch branch = O2MagicBranch.CHARMS;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public Charms () { }

   /**
    * Constructor for casting a charm spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public Charms (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }
}
