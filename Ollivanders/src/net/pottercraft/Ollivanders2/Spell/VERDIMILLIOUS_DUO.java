package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Fancier version of VERDIMILLIOUS
 *
 * @see PyrotechniaSuper
 * @see VERDIMILLIOUS
 * @author Azami7
 */
public final class VERDIMILLIOUS_DUO extends PyrotechniaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public VERDIMILLIOUS_DUO ()
   {
      super();

      spellType = O2SpellType.VERDIMILLIOUS_DUO;
      text = "Conjures large green ball fireworks with trails.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public VERDIMILLIOUS_DUO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.VERDIMILLIOUS_DUO;
      setUsesModifier();

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.GREEN);
      fireworkColors.add(Color.LIME);

      fireworkType = Type.BALL_LARGE;
      hasTrails = true;

      setMaxFireworks(10);
   }
}
