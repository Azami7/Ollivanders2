package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Shoots red burst fireworks in to the air.
 */
public final class PERICULUM_DUO extends Pyrotechnia
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public PERICULUM_DUO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.PERICULUM_DUO;
      branch = O2MagicBranch.CHARMS;

      text = "Conjures large red ball fireworks in the air.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PERICULUM_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PERICULUM_DUO;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.RED);
      fireworkType = Type.BALL_LARGE;

      hasTrails = true;

      setMaxFireworks(10);
   }
}
