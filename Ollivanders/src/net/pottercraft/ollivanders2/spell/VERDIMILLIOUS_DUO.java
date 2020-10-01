package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Fancier version of VERDIMILLIOUS
 *
 * @author Azami7
 * @see Pyrotechnia
 * @see VERDIMILLIOUS
 */
public final class VERDIMILLIOUS_DUO extends Pyrotechnia
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public VERDIMILLIOUS_DUO()
   {
      super();

      spellType = O2SpellType.VERDIMILLIOUS_DUO;
      text = "Conjures large green ball fireworks with trails.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public VERDIMILLIOUS_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.VERDIMILLIOUS_DUO;
      initSpell();

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.GREEN);
      fireworkColors.add(Color.LIME);

      fireworkType = Type.BALL_LARGE;
      hasTrails = true;

      setMaxFireworks(10);
   }
}
