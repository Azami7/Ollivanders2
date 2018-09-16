package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Glacius Tria has the same effect as Glacius but with 4x the duration and radius.
 *
 * @see GlaciusSuper
 * @version Ollivanders2
 * @author Azami7
 */
public final class GLACIUS_TRIA extends GlaciusSuper
{
   public O2SpellType spellType = O2SpellType.GLACIUS_TRIA;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("The Strongest Freezing Charm");
   }};

   protected String text = "Glacius Tria will freeze blocks in a radius four times that of glacius, but for one quarter the time.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public GLACIUS_TRIA () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public GLACIUS_TRIA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      //25% duration
      durationModifier = 0.25;
      //4x radius radius
      radiusModifier = 4.0;
   }
}