package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Silences a player for a duration depending on the spell's level. The target player can only use nonverbal spells.
 *
 * @author lownes
 * @author Azami7
 */
public final class SILENCIO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public SILENCIO ()
   {
      super();

      flavorText.add("The raven continued to open and close its sharp beak, but no sound came out.");
      flavorText.add("The Silencing Charm");
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public SILENCIO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<LivingEntity> living = getLivingEntities(1);
      for (LivingEntity live : living)
      {
         if (live instanceof Player)
         {
            Player player = (Player) live;
            O2Player o2p = p.getO2Player(player);
            int dur = (int) (usesModifier * 1200);
            o2p.addEffect(new net.pottercraft.Ollivanders2.Effect.SILENCIO(player, Effects.SILENCIO, dur));
            p.setO2Player(player, o2p);
            kill();
            return;
         }
      }
   }
}