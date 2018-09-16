package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Shoots yellow star fireworks in to the air.
 *
 * @see PyrotechniaSuper
 * @author Azami7
 */
public final class BOTHYNUS extends PyrotechniaSuper
{
   public O2SpellType spellType = O2SpellType.BOTHYNUS;

   protected String text = "Creates one or more yellow star fireworks.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public BOTHYNUS () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public BOTHYNUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.YELLOW);
      fireworkType = Type.STAR;

      setMaxFireworks(10);
   }
}
