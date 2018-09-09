package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collection;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Effect.O2Effect;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import net.pottercraft.Ollivanders2.*;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Moving Spell Projectile
 *
 * @author lownes
 * @author Azami7
 */
public abstract class SpellProjectile implements Teachable
{
   public Player player;
   public O2SpellType spellType;
   public Location location;
   public int lifeTicks;
   public boolean kill;
   public Ollivanders2 p;
   public int spellUses;
   public double usesModifier;
   public Vector vector;
   public double rightWand;
   public Effect moveEffect = Effect.STEP_SOUND;
   public Material moveEffectData = Material.GLOWSTONE;
   public Set<Block> changed = new HashSet<>();
   public static final Long DEFAULT_COOLDOWN = new Long(1000);

   /**
    * The branch of magic this spell is
    */
   protected O2MagicBranch branch = O2MagicBranch.CHARMS;

   /**
    * Flavor text for this spell in spellbooks, etc.  Optional.
    */
   protected ArrayList<String> flavorText = new ArrayList<>();

   /**
    * The description text for this spell in spell books.  Required or spell cannot be written in a book.
    */
   protected String text = "";

   /**
    * Default constructor should only be used for fake instances of the spell such as when initializing the book
    * text.
    *
    * @param type the type of this spell
    */
   public SpellProjectile (O2SpellType type)
   {
      spellType = type;
   }

   /**
    * Constructor
    *
    * @param plugin a callback to the O2 plugin
    * @param player the player casting the spell
    * @param type the type of this spell
    * @param rightWand wand check for the player
    */
   public SpellProjectile (Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      location = player.getEyeLocation();
      this.spellType = type;
      this.player = player;
      kill = false;
      lifeTicks = 0;
      p = plugin;
      spellUses = p.getSpellNum(player, type);
      usesModifier = spellUses;
      boolean memoryPotion = false;

      vector = location.getDirection().normalize();
      location.add(vector);
      this.rightWand = rightWand;

      usesModifier = getUsesModifier();

      for (O2Effect effect : p.getO2Player(player).getEffects())
      {
         if (effect.name == O2EffectType.MEMORY_POTION)
         {
            memoryPotion = true;
         }
         else if (effect.name == O2EffectType.BARUFFIOS_BRAIN_ELIXIR)
         {
            usesModifier *= 2;
         }
      }

      p.incSpellCount(player, type);
      if (memoryPotion)
      {
         p.incSpellCount(player, type);
      }

      if (!p.canCast(player, type, true))
      {
         kill();
      }
   }

   /**
    * Moves the projectile forward, creating a particle effect
    */
   public void move ()
   {
      location.add(vector);
      if (!p.canLive(location, spellType))
      {
         kill();
      }
      location.getWorld().playEffect(location, moveEffect, moveEffectData);
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE
            && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER
            && getBlock().getType() != Material.STATIONARY_LAVA && getBlock().getType() != Material.LAVA)
      {
         kill = true;
      }
      lifeTicks++;
      if (lifeTicks > 160)
      {
         kill = true;
      }
   }

   /**
    * Gets the block the projectile is inside
    *
    * @return Block the projectile is inside
    */
   public Block getBlock ()
   {
      return location.getBlock();
   }

   /**
    * Gets entities within a distance of spell target
    *
    * @param radius - radius within which to get entities
    * @return List of entities within one block of projectile
    */
   public List<Entity> getCloseEntities (double radius)
   {
      if (radius <= 0)
         radius = 1.0;

      Ollivanders2Common o2c = new Ollivanders2Common(p);
      Collection<Entity> entities = o2c.getEntitiesInRadius(location, radius);
      List<Entity> close = new ArrayList<>();

      for (Entity e : entities)
      {
         if (e instanceof LivingEntity)
         {
            if (((LivingEntity) e).getEyeLocation().distance(location) < radius || ((e instanceof EnderDragon || e instanceof Giant) && ((LivingEntity) e).getEyeLocation().distance(location) < (radius + 5)))
            {
               if (!e.equals(player))
               {
                  close.add(e);
               }
               else
               {
                  if (lifeTicks > 1)
                  {
                     close.add(e);
                  }
               }
            }
         }
         else
         {
            close.add(e);
         }

         close.add(e);
      }
      return close;
   }

   /**
    * Gets item entities within one block of the projectile
    *
    * @param radius - radius within which to get entities
    * @return List of item entities within one block of projectile
    */
   public List<Item> getItems (double radius)
   {
      List<Entity> entities = getCloseEntities(radius);
      List<Item> items = new ArrayList<>();
      for (Entity e : entities)
      {
         if (e instanceof Item)
         {
            items.add((Item) e);
         }
      }
      return items;
   }

   /**
    * Gets all LivingEntity within one block of projectile
    *
    * @param radius - radius within which to get entities
    * @return List of LivingEntity within one block of projectile
    */
   public List<LivingEntity> getLivingEntities (double radius)
   {
      List<Entity> entities = getCloseEntities(radius);
      List<LivingEntity> living = new ArrayList<>();
      for (Entity e : entities)
      {
         if (e instanceof LivingEntity)
         {
            living.add((LivingEntity) e);
         }
      }

      if (lifeTicks == 1 && player.getEyeLocation().getPitch() > 80)
      {
         living.add(player);
      }

      return living;
   }

   /**
    * Provides the uses modifier that takes into account spell uses and wand type. Returns 10.0 if the uses are 100 and the right wand is held.
    *
    * @return Uses modifier
    */
   private double getUsesModifier ()
   {
      double modifier = Math.sqrt(p.getSpellNum(player, spellType)) / rightWand;

      return modifier;
   }

   /**
    * Reverts any changes made to blocks if the effects are temporary.
    * Changed blocks are in this.changed
    */
   public void revert ()
   {

   }

   /**
    * Get the target block for the spell.
    *
    * @return the target block
    */
   protected Block getTargetBlock ()
   {
      Block center = getBlock();

      if (center.getType() != Material.AIR)
      {
         return center;
      }
      else
      {
         return null;
      }
   }

   /**
    * This kills the spell.
    */
   public void kill ()
   {
      kill = true;
   }

   /**
    * Return the cool-down time for this spell.
    */
   public Long getCoolDown() {
      return DEFAULT_COOLDOWN;
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

   @Override
   public String getName ()
   {
      Ollivanders2Common common = new Ollivanders2Common(p);

      return common.firstLetterCapitalize(spellType.toString().toLowerCase().replace("_", " "));
   }
}