package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Shoots purple fireworks in to the air.
 *
 * @see PyrotechniaSuper
 * @author Azami7
 */
public final class PORFYRO_ASTERI extends PyrotechniaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PORFYRO_ASTERI ()
   {
      super();

      text = "Conjures purple star fireworks in the sky.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public PORFYRO_ASTERI (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.PURPLE);
      fireworkType = Type.STAR;

      setMaxFireworks(10);
   }
}
