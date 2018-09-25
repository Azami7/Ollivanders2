package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * All animals that you have created through transfiguration will target the targeted LivingEntity.
 *
 * @author lownes
 * @author Azami7
 */
public final class OPPUGNO extends DarkArts
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public OPPUGNO ()
   {
      super();

      spellType = O2SpellType.OPPUGNO;

      flavorText = new ArrayList<String>() {{
         add("Harry spun around to see Hermione pointing her wand at Ron, her expression wild: The little flock of birds was speeding like a hail of fat golden bullets toward Ron, who yelped and covered his face with his hands, but the birds attacked, pecking and clawing at every bit of flesh they could reach.");
         add("The Oppugno Jinx");
      }};

      text = "Oppugno will cause any entities transfigured by you to attack the targeted entity.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public OPPUGNO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.OPPUGNO;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity e : getLivingEntities(1.5))
      {
         if (e.getUniqueId() == player.getUniqueId())
            continue;

         for (O2Spell spell : p.getProjectiles())
         {
            if (spell instanceof Transfiguration)
            {
               if (spell.player.equals(player))
               {
                  for (Entity entity : player.getWorld().getEntities())
                  {
                     if (entity.getUniqueId() == ((Transfiguration) spell).getToID())
                     {
                        if (entity instanceof LivingEntity)
                        {
                           ((Creature) entity).damage(0.0, e);
                           kill();
                        }
                     }
                  }
               }
            }
         }
      }
   }
}