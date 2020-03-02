package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Creates a storm of a variable duration.
 *
 * @version Ollivanders2
 * @see MetelojinxSuper
 * @author lownes
 * @author Azami7
 */
public final class METELOJINX extends MetelojinxSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public METELOJINX ()
   {
      super();

      spellType = O2SpellType.METELOJINX;
      branch = O2MagicBranch.CHARMS;

      text = "Metelojinx will turn a sunny day into a storm.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public METELOJINX (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.METELOJINX;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      storm = true;
   }
}