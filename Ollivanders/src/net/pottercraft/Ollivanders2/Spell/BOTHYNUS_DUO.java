package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * A fancier version of Bothynus.
 *
 * @see BOTHYNUS
 * @see PyrotechniaSuper
 * @author Azami7
 */
public final class BOTHYNUS_DUO extends PyrotechniaSuper
{
   public O2SpellType spellType = O2SpellType.BOTHYNUS_DUO;

   protected String text = "Creates one or more yellow and orange star fireworks with trails.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public BOTHYNUS_DUO () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public BOTHYNUS_DUO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.YELLOW);
      fireworkColors.add(Color.ORANGE);
      fireworkType = Type.STAR;
      hasTrails = true;

      setMaxFireworks(10);
   }
}
