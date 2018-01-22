package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;

import net.pottercraft.Ollivanders2.Ollivanders2WorldGuard;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * The super class for transfiguration of all entities.
 *
 * @since 2.2.6
 * @author Azami7
 */
public abstract class EntityTransfigurationSuper extends SpellProjectile implements O2Spell
{
   //
   // these should generally not be changed
   //
   /**
    * If the transfiguration has taken place or not.
    */
   protected boolean isTransfigured = false;

   //
   // these should be set by each spell as needed
   //
   /**
    * The branch of magic this spell is - most likely Charms or Transfiguration
    */
   protected O2MagicBranch branch = O2MagicBranch.TRANSFIGURATION;

   /**
    * The type of entity this will transfigure.
    */
   EntityType targetType = EntityType.SHEEP;

   /**
    * Whether this transfiguration permanent or not.  Usually for Charms it is false and for Transfiguration it is true.
    */
   protected boolean permanent = false;

   /**
    * How many blocks out from the target are affects.  Usually for permanent spells this is 1.
    */
   protected int radius = 1;

   /**
    * Allows spell variants to change the radius of the spell.
    */
   protected double radiusModifier = 1.0;

   /**
    * If this is not permanent, how long it should last.
    */
   protected int spellDuration = 120;

   /**
    * Allows spell variants to change the duration of this spell.
    */
   protected double durationModifier = 1.0;

   /**
    * The current duration of this spell.
    */
   protected int lifeTicks = 0;

   /**
    * The percent chance this spell will succeed each casting.
    */
   protected int successRate = 100;

   /**
    * Flavor text for this spell in spellbooks, etc.  Optional.
    */
   protected ArrayList<String> flavorText = new ArrayList<>();

   /**
    * The description text for this spell in spell books.  Required or spell cannot be written in a book.
    */
   protected String text = "";

   /**
    * A blacklist of Entity types that will not be affected by this spell.  Only used if the whitelist is empty.
    */
   protected List<EntityType> entityBlacklist = new ArrayList<>();

   /**
    * A whitelist of Entity types that will be affected by this spell.
    */
   protected List<EntityType> entityWhitelist = new ArrayList<>();

   /**
    * A class for checking WorldGuard permissions
    */
   protected Ollivanders2WorldGuard worldGuard;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EntityTransfigurationSuper () {}

   /**
    * Constructor
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public EntityTransfigurationSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      entityBlacklist.add(EntityType.AREA_EFFECT_CLOUD);
      entityBlacklist.add(EntityType.COMPLEX_PART);
      entityBlacklist.add(EntityType.EXPERIENCE_ORB);
      entityBlacklist.add(EntityType.FALLING_BLOCK);
      entityBlacklist.add(EntityType.EXPERIENCE_ORB);
      entityBlacklist.add(EntityType.UNKNOWN);
   }

   @Override
   public void checkEffect()
   {
      // if the entity has not transfigured, transfigure it
      if (!isTransfigured)
      {
         // move the projectile
         move();

         // kill the spell if it is not allowed to be cast here
         if (Ollivanders2.worldGuardEnabled && !Ollivanders2.worldGuardO2.checkWGBuild(player, player.getLocation()))
         {
            kill();
            p.spellCannotBeCastMessage(player);
            return;
         }

         for (Entity e : getCloseEntities(1))
         {
            // kill the spell if it is not allowed to be cast here
            if (Ollivanders2.worldGuardEnabled && !Ollivanders2.worldGuardO2.checkWGBuild(player, e.getLocation()))
            {
               kill();
               p.spellCannotBeCastMessage(player);
               return;
            }

            transfigure(e);

            if (!permanent)
            {
               spellDuration = (int) (spellDuration * durationModifier);

               // do not let the spell last more than 24 hours
               if (spellDuration > 86400)
                  spellDuration = 86400;

               kill = false;
            }
            else
            {
               spellDuration = 0;
               kill();
            }
         }
      }
      else
      {
         // check time to live on the spell
         if (lifeTicks >= spellDuration)
         {
            // spell duration is up, transfigure back to original shape
            if (!permanent)
            {
               revert();
            }

            kill();
            return;
         }

         lifeTicks++;
      }
   }

   /**
    * Transfigure the target living entity. Will not change the entity if it is on the entityBlacklist list or
    * if the target entity is already the transfigure type.
    *
    * @param entity
    */
   protected void transfigure (Entity entity)
   {
      if (isTransfigured || !canTransfigure(entity))
      {
         return;
      }

      // check success
      int rand = Math.abs(Ollivanders2.random.nextInt() % 100);
      if (rand < successRate)
      {
         transfigureEntity(entity);
      }
      else
      {
         if (Ollivanders2.debug)
            p.getLogger().info("Failed success check.");
      }
   }

   /**
    * Determine if this entity can be transfigured by this spell. This must be overridden by child classes or
    * it will fail all transfigurations.
    *
    * @param e
    * @return true if the entity can be transfigured, false otherwise.
    */
   protected boolean canTransfigure (Entity e)
   {
      return false;
   }

   /**
    * Transfigures entity into new EntityType.
    *
    * @param entity
    */
   protected void transfigureEntity (Entity entity)
   {
   }

   /**
    * Determines if this entity can be changed by this Transfiguration spell.
    *
    * @param e
    * @return true if the entity can be changed, false otherwise.
    */
   protected boolean targetTypeCheck (Entity e)
   {
      // get entity type
      EntityType eType = e.getType();

      boolean check = true;

      if (eType == targetType) // do not change if this entity is already the target type
      {
         if (Ollivanders2.debug)
            p.getLogger().info("Target entity is same type as spell type.");

         check = false;
      }
      else if (entityBlacklist.contains(eType)) // do not change if this entity is in the blacklist
      {
         if (Ollivanders2.debug)
            p.getLogger().info("EntityType is on the blacklist.");

         check = false;
      }
      else if (!entityWhitelist.isEmpty()) // do not change if the whitelist exists and this entity is not in it
      {
         if (!entityWhitelist.contains(eType))
         {
            if (Ollivanders2.debug)
               p.getLogger().info("EntityType is not on the whitelist.");

            check = false;
         }
      }

      return check;
   }

   @Override
   public String getText ()
   {
      return text;
   }

   @Override
   public String getFlavorText()
   {
      if (flavorText.size() < 1)
      {
         return null;
      }
      else
      {
         int index = Math.abs(Ollivanders2.random.nextInt() % flavorText.size());
         return flavorText.get(index);
      }
   }

   @Override
   public O2MagicBranch getMagicBranch ()
   {
      return branch;
   }
}
