package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Cast the dark mark in the sky.
 *
 * @author Azami7
 */
public final class MORSMORDRE extends PyrotechniaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MORSMORDRE ()
   {
      super();

      flavorText.add("\"Should the Dark Mark appear over any dwelling place or other  building, DO NOT ENTER, but contact the Auror office immediately.\" -Ministry of Magic");
      flavorText.add("Then he realised it was a colossal skull, comprised of what looked like emerald stars, with a serpent protruding from its mouth like a tongue. As they watched, it rose higher and higher, blazing in a haze of greenish smoke, etched against the black sky like a new constellation.");

      text = "Conjures the Dark Mark in the sky.";

      branch = O2MagicBranch.DARK_ARTS;
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public MORSMORDRE (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      branch = O2MagicBranch.DARK_ARTS;

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.GREEN);
      fireworkType = Type.CREEPER;
      maxFireworks = 20;
   }
}
