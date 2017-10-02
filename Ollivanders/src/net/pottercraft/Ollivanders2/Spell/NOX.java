package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/29/17.
 *
 * Cancels the effect of the LUMOS spell or removes the effect of a Night Vision potion.
 *
 * @author lownes
 * @author Azami7
 */
public final class NOX extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public NOX ()
   {
      super();

      flavorText.add("The Wand-Extinguishing Charm");
      flavorText.add("With difficulty he dragged it over himself, murmured, 'Nox,' extinguishing his wand light, and continued on his hands and knees, as silently as possible, all his senses straining, expecting every second to be discovered, to hear a cold clear voice, see a flash of green light.");

      text = "Cancels the effect of the Lumos spell or removes the effect of a Night Vision potion.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public NOX(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect() {
      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
      kill();
   }
}
