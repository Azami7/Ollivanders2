package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

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

      flavorText.add("Harry spun around to see Hermione pointing her wand at Ron, her expression wild: The little flock of birds was speeding like a hail of fat golden bullets toward Ron, who yelped and covered his face with his hands, but the birds attacked, pecking and clawing at every bit of flesh they could reach.");
      flavorText.add("The Oppugno Jinx");
      text = "Oppugno will cause any entities transfigured by you to attack the targeted entity.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public OPPUGNO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity e : getLivingEntities(2))
      {
         for (SpellProjectile spell : p.getProjectiles())
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