package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Shoots purple fireworks in to the air.
 *
 * @see PyrotechniaSuper
 * @author Azami7
 */
public final class PORFYRO_ASTERI extends PyrotechniaSuper
{
   public O2SpellType spellType = O2SpellType.PORFYRO_ASTERI;

   protected String text = "Conjures purple star fireworks in the sky.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PORFYRO_ASTERI () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PORFYRO_ASTERI (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.PURPLE);
      fireworkType = Type.STAR;

      setMaxFireworks(10);
   }
}
