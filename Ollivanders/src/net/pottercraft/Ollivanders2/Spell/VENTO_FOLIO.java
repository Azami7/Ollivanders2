package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Give a player the ability to fly.
 *
 * @author lownes
 * @author Azami7
 */
public final class VENTO_FOLIO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public VENTO_FOLIO ()
   {
      super();

      text = "Vento Folio gives a player the ability to fly unassisted for an amount of time.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public VENTO_FOLIO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
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
            Player ply = (Player) live;
            OPlayer oply = p.getOPlayer(ply);
            int dur = (int) (usesModifier * 1200);
            oply.addEffect(new net.pottercraft.Ollivanders2.Effect.VENTO_FOLIO(player, Effects.VENTO_FOLIO, dur));
            p.setOPlayer(ply, oply);
            kill();
            return;
         }
      }
   }
}
