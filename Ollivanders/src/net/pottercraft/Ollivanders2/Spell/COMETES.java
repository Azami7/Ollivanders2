package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Created by Azami7 on 6/29/17.
 *
 * Shoots mutliple orange burst fireworks in to the air.
 *
 * @see PyrotechniaSuper
 * @author Azami7
 */
public final class COMETES extends PyrotechniaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COMETES ()
   {
      super();

      spellType = O2SpellType.COMETES;

      text = "Creates one or more orange burst fireworks.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COMETES (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.COMETES;

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.ORANGE);
      fireworkType = Type.BURST;

      setMaxFireworks(10);
   }
}
