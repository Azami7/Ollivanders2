package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public abstract class SpellProjectile
{

   public Player player;
   public Spells name;
   public Location location;
   public Vector vector;
   public int lifeTicks;
   public boolean kill;
   public Ollivanders2 p;
   public double rightWand;
   public int spellUses;
   public double usesModifier;
   public Effect moveEffect = Effect.STEP_SOUND;
   public Material moveEffectData = Material.GLOWSTONE;
   public Set<Block> changed = new HashSet<>();

   /**
    * Default constructor should only be used for fake instances of the spell such as when initializing the book
    * text.
    */
   public SpellProjectile () { }

   /**
    * Constructor
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public SpellProjectile (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      location = player.getEyeLocation();
      vector = location.getDirection().normalize();
      location.add(vector);
      this.name = name;
      this.player = player;
      kill = false;
      lifeTicks = 0;
      p = plugin;
      this.rightWand = rightWand;
      spellUses = p.getSpellNum(player, name);
      usesModifier = getUsesModifier();
      boolean memoryPotion = false;

      for (OEffect effect : p.getO2Player(player).getEffects())
      {
         if (effect.name == Effects.MEMORY_POTION)
         {
            memoryPotion = true;
         }
         else if (effect.name == Effects.BARUFFIOS_BRAIN_ELIXIR)
         {
            usesModifier *= 2;
         }
      }

      p.incSpellCount(player, name);
      if (memoryPotion)
      {
         p.incSpellCount(player, name);
      }

      if (!p.canCast(player, name, true))
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
      if (!p.canLive(location, name))
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
    * This kills the projectile.
    */
   public void kill ()
   {
      kill = true;
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
    * Gets entities within a distance of projectile
    *
    * @param radius - radius within which to get entities
    * @return List of entities within one block of projectile
    */
   public List<Entity> getCloseEntities (double radius)
   {
      List<Entity> entities = location.getWorld().getEntities();
      List<Entity> close = new ArrayList<Entity>();
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
                     //getLogger().info(((LivingEntity) e).getEyeLocation().distance(location));
                     close.add(e);
                  }
               }
            }
         }
         else
         {
            if (e.getLocation().distance(location) < radius)
            {
               close.add(e);
            }
         }
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
      List<Item> items = new ArrayList<Item>();
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
      List<LivingEntity> living = new ArrayList<LivingEntity>();
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
      double modifier = Math.sqrt(p.getSpellNum(player, name)) / rightWand;

      return modifier;
   }

   /**
    * Gets the blocks in a radius of a location.
    *
    * @param loc    - The Location that is the center of the block list
    * @param radius - The radius of the block list
    * @return List of blocks that are within radius of the location.
    */
   public List<Block> getBlocksInRadius (Location loc, double radius)
   {
      Block center = loc.getBlock();
      int blockRadius = (int) (radius + 1);
      List<Block> blockList = new ArrayList<>();
      for (int x = -blockRadius; x <= blockRadius; x++)
      {
         for (int y = -blockRadius; y <= blockRadius; y++)
         {
            for (int z = -blockRadius; z <= blockRadius; z++)
            {
               blockList.add(center.getRelative(x, y, z));
            }
         }
      }
      ArrayList<Block> returnList = new ArrayList<Block>();
      for (Block block : blockList)
      {
         if (block.getLocation().distance(center.getLocation()) < radius)
         {
            returnList.add(block);
         }
      }
      return returnList;
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
}