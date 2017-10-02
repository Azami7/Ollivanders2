package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * A fancier version of COMETES.
 *
 * @see COMETES
 * @see PyrotechniaSuper
 * @author Azami7
 */
public final class COMETES_DUO extends PyrotechniaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COMETES_DUO ()
   {
      super();

      text = "Creates one or more orange burst fireworks with trails, flicker, and fades to white and yellow.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public COMETES_DUO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.ORANGE);
      fireworkType = Type.BURST;

      hasTrails = true;
      hasFlicker = true;
      hasFade = true;

      fadeColors = new ArrayList<>();
      fadeColors.add(Color.YELLOW);
      fadeColors.add(Color.WHITE);

      setMaxFireworks(10);
   }
}
