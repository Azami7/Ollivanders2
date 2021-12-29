package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2Color;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Target sheep or colored block turns yellow.
 *
 * @author Azami7
 */
public final class COLOVARIA_FLAVO extends ColoroSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public COLOVARIA_FLAVO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.COLOVARIA_FLAVO;
      branch = O2MagicBranch.CHARMS;

      text = "Turns target colorable entity or block yellow.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COLOVARIA_FLAVO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.COLOVARIA_FLAVO;
      branch = O2MagicBranch.CHARMS;

      color = O2Color.YELLOW;

      initSpell();
   }
}