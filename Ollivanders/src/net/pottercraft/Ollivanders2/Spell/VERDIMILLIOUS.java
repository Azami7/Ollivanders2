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

      spellType = O2SpellType.VERDIMILLIOUS;
      text = "Conjures large green ball fireworks in the air.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public VERDIMILLIOUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.VERDIMILLIOUS;
      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.GREEN);
      fireworkType = Type.BALL_LARGE;

      setMaxFireworks(10);
   }
}
