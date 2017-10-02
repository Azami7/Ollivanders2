package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Causes purple firecrackers to shoot out from the tip of one's wand.
 *
 * Seen/Mentioned: On 31 October 1991, Albus Dumbledore used this spell to get the attention of panicking diners in the
 * Great Hall when a troll was loose in the castle.
 *
 * @see PyrotechniaSuper
 * @author Azami7
 */
public final class MOV_FOTIA extends PyrotechniaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MOV_FOTIA ()
   {
      super();

      flavorText.add("It took several purple firecrackers exploding from the end of Professor Dumbledore's wand to bring silence.");
      flavorText.add("Purple Firecrackers");

      text = "Causes purple firecrackers to shoot out from the tip of one's wand.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public MOV_FOTIA (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.PURPLE);
      fireworkType = Type.BALL;
      fireworkPower = 0;

      setMaxFireworks(1);
   }
}
