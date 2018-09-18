package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Glacius Duo has the same effect as Glacius but with 2x the duration and radius.
 *
 * @see GlaciusSuper
 * @author Azami7
 */
public final class GLACIUS_DUO extends GlaciusSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public GLACIUS_DUO ()
   {
      super();

      spellType = O2SpellType.APARECIUM;

      flavorText = new ArrayList<String>() {{
         add("A Stronger Freezing Charm");
         add("\"It's about preparing ourselves ...for what's waiting for us out there.\" -Hermione Granger");
      }};

      text = "Glacius Duo will freeze blocks in a radius twice that of glacius, but for half the time.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public GLACIUS_DUO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.APARECIUM;

      // 50% duration
      durationModifier = 0.5;
      // 2x radius
      radiusModifier = 2.0;
   }
}