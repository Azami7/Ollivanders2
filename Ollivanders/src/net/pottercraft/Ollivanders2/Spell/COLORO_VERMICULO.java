package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Target sheep or colored block turns red.
 *
 * @author Azami7
 */
public final class COLORO_VERMICULO extends ColoroSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLORO_VERMICULO ()
   {
      spellType = O2SpellType.COLORO_VERMICULO;
      text = "Turns target colorable entity or block red.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COLORO_VERMICULO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.COLORO_VERMICULO;
      color = DyeColor.RED;
   }
}
