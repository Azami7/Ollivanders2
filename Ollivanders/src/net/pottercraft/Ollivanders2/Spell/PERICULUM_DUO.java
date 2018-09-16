package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Shoots red burst fireworks in to the air.
 */
public final class PERICULUM_DUO extends PyrotechniaSuper
{
   public O2SpellType spellType = O2SpellType.PERICULUM_DUO;

   protected String text = "Conjures large red ball fireworks in the air.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PERICULUM_DUO () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PERICULUM_DUO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.RED);
      fireworkType = Type.BALL_LARGE;

      hasTrails = true;

      setMaxFireworks(10);
   }
}
