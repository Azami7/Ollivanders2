package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Encases target's head in a melon itemstack with amount 0
 *
 * @author lownes
 */
public final class MELOFORS extends GaleatiSuper
{
   public O2SpellType spellType = O2SpellType.MELOFORS;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("Harry overheard one second-year girl assuring another that Fudge was now lying in St Mungo’s with a pumpkin for a head.\"");
      add("The Melon-Head Spell");
   }};

   protected String text = "Melofors places a melon on the target player's head.";

   Material [] melons = {
         Material.MELON_BLOCK,
         Material.JACK_O_LANTERN,
   };

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MELOFORS () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MELOFORS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      materialType = melons[Math.abs(Ollivanders2.random.nextInt() % melons.length)];
   }
}