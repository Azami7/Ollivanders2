package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Encases target's head in a melon itemstack with amount 0
 *
 * @author lownes
 */
public final class MELOFORS extends GaleatiSuper
{
   Material [] melons = {
         Material.MELON_BLOCK,
         Material.JACK_O_LANTERN,
   };

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MELOFORS (O2SpellType type)
   {
      super(type);

      flavorText.add("Harry overheard one second-year girl assuring another that Fudge was now lying in St Mungo’s with a pumpkin for a head.\"");
      flavorText.add("The Melon-Head Spell");
      text = "Melofors places a melon on the target player's head.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param type
    * @param rightWand
    */
   public MELOFORS (Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, type, rightWand);

      materialType = melons[Math.abs(Ollivanders2.random.nextInt() % melons.length)];
   }
}