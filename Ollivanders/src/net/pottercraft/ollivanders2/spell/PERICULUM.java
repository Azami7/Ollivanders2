package net.pottercraft.ollivanders2.spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Shoots red burst fireworks in to the air.
 *
 * @see PyrotechniaSuper
 * @author lownes
 * @author Azami7
 */
public final class PERICULUM extends PyrotechniaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PERICULUM ()
   {
      super();

      spellType = O2SpellType.PERICULUM;
      text = "Conjures red burst fireworks in the air.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PERICULUM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PERICULUM;
      setUsesModifier();

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.RED);
      fireworkType = Type.BURST;

      setMaxFireworks(10);
   }
}