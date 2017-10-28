package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Reduces the time duration of any levicorpus effects on the target
 *
 * @author lownes
 * @author Azami7
 */
public final class LIBERACORPUS extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LIBERACORPUS ()
   {
      super();

      flavorText.add("The Levicorpus Counter-Spell");
      flavorText.add("...he jerked his wand upwards; Snape fell into a crumpled heap on the ground.");
      text = "Liberacorpus will reduce the time left on any levicorpus effects on the target by an amount determined by your experience.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public LIBERACORPUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1))
      {
         if (live instanceof Player)
         {
            Player player = (Player) live;
            O2Player o2p = p.getO2Player(player);
            for (OEffect effect : o2p.getEffects())
            {
               if (effect.name == Effects.LEVICORPUS)
               {
                  effect.age((int) (usesModifier * 2400));
               }
            }
            kill();
            return;
         }
      }
   }
}
