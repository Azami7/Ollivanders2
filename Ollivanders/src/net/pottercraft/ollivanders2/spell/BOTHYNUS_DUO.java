package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * A fancier version of Bothynus.
 *
 * @see BOTHYNUS
 * @see PyrotechniaSuper
 * @author Azami7
 */
public final class BOTHYNUS_DUO extends PyrotechniaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public BOTHYNUS_DUO ()
   {
      super();

      spellType = O2SpellType.BOTHYNUS_DUO;

      text = "Creates one or more yellow and orange star fireworks with trails.";
   }

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

      spellType = O2SpellType.BOTHYNUS_DUO;
      setUsesModifier();

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.YELLOW);
      fireworkColors.add(Color.ORANGE);
      fireworkType = Type.STAR;
      hasTrails = true;

      setMaxFireworks(10);
   }
}
