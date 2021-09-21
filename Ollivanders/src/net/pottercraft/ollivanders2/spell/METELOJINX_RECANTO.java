package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Ends a storm for a variable duration
 *
 * @version Ollivanders2
 * @see MetelojinxSuper
 * @author lownes
 * @author Azami7
 */
public final class METELOJINX_RECANTO extends MetelojinxSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public METELOJINX_RECANTO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.METELOJINX_RECANTO;
      branch = O2MagicBranch.CHARMS;

      text = "Metelojinx Recanto will turn a storm into a sunny day.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public METELOJINX_RECANTO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.METELOJINX_RECANTO;
      branch = O2MagicBranch.CHARMS;

      initSpell();
      storm = false;
   }
}