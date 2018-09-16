package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Target sheep or colored block turns yellow.
 *
 * @author Azami7
 */
public final class COLORO_FLAVO extends ColoroSuper
{
   public O2SpellType spellType = O2SpellType.COLORO_FLAVO;
   protected String text = "Turns target colorable entity or block yellow.";

   DyeColor color = DyeColor.YELLOW;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLORO_FLAVO () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COLORO_FLAVO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }
}
