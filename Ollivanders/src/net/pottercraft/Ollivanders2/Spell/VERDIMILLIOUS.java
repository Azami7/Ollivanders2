package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Shoots green fireworks in to the air.
 *
 * @see PyrotechniaSuper
 * @author Azami7
 */
public final class VERDIMILLIOUS extends PyrotechniaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public VERDIMILLIOUS ()
   {
      super();

      text = "Conjures large green ball fireworks in the air.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public VERDIMILLIOUS (Ollivanders2 plugin, Player player, O2SpellType name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.GREEN);
      fireworkType = Type.BALL_LARGE;

      setMaxFireworks(10);
   }
}
