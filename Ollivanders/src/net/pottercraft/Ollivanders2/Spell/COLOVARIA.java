package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.O2Color;
import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Changes color of sheep and colorable blocks to a random variant
 *
 * @author lownes
 * @author Azami7
 */
public final class COLOVARIA extends ColoroSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLOVARIA ()
   {
      super();

      spellType = O2SpellType.COLOVARIA;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>() {{
         add("The Colour Change Charm");
         add("[...] he wished he had not mixed up the incantations for Colour Change and Growth Charms, so that the rat he was supposed to be turning orange swelled shockingly and was the size of a badger before Harry could rectify his mistake.");
      }};

      text = "Changes color of sheep and colorable blocks to another color.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COLOVARIA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.COLOVARIA;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      color = O2Color.getRandomDyeableColor();
   }
}