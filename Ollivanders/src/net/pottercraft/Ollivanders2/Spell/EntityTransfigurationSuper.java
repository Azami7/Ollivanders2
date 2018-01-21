package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The super class for transfiguration of mobs and players.
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

   /**
    * A map of the transfigured entities and their original types for use with revert()
    */
   protected HashMap<Entity, EntityType> originalEntities = new HashMap<>();

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
   protected boolean permanent = true;

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
   protected int spellDuration = 1200;

   /**
    * Allows spell variants to change the duration of this spell.
    */
   protected double durationModifier = 1.0;

   /**
    * The current duration of this spell.
    */
   protected int lifeTicks = 0;

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
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EntityTransfigurationSuper () {}

   public EntityTransfigurationSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      entityBlacklist.add(EntityType.AREA_EFFECT_CLOUD);
      entityBlacklist.add(EntityType.COMPLEX_PART);
      entityBlacklist.add(EntityType.EXPERIENCE_ORB);
      entityBlacklist.add(EntityType.PLAYER);
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
      if (isTransfigured)
      {
         return;
      }

      if (!canTransfigure(entity))
      {
         return;
      }

      if (permanent)
      {
         transfigureEntity(entity);
      }
      else
      {
         disguiseEntity(entity);
      }
   }

   /**
    * Transfigures entity into new EntityType. This spawns an entirely new entity in the location of the target
    * entity with as many of the same attributes as possible.
    */
   public void transfigureEntity (Entity entity)
   {

   }

   public void disguiseEntity (Entity entity)
   {

   }

   /**
    * Determines if this entity can be changed by this Transfiguration spell.
    *
    * @param e
    * @return true if the entity can be changed, false otherwise.
    */
   protected boolean canTransfigure (Entity e)
   {
      // get entity type
      EntityType eType = e.getType();

      boolean canChange = true;

      if (eType == targetType) // do not change if this entity is already the target type
      {
         canChange = false;
      }
      else if (entityBlacklist.contains(e)) // do not change if this entity is in the blacklist
      {
            canChange = false;
      }
      else if (!entityWhitelist.isEmpty()) // do not change if the whitelist exists and this entity is not in it
      {
         if (!entityWhitelist.contains(e))
         {
            canChange = false;
         }
      }
      else if (!e.getType().isAlive() && targetType.isAlive()) // do not change a non-living thing in to a living thing
      {
         canChange = false;
      }

      return canChange;
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
