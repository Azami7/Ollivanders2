package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collection;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
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
 * A cast spell.
 *
 * Used to be SpellProjectile.
 *
 * @author Azami7
 */
public abstract class O2Spell implements Teachable
{
   /**
    * The most number of words a spell name can be.
    */
   public static int max_spell_words = 3;

   /**
    * The player who cast this spell.
    */
   public Player player;

   /**
    * The type this spell is.
    */
   public O2SpellType spellType;

   /**
    * The location the spell was cast from.
    */
   public Location location;

   /**
    * How long this spell projectile has been alive in game ticks.
    */
   public int lifeTicks = 0;

   /**
    * Whether this spell projectile should be terminated.
    */
   public boolean kill = false;

   /**
    * The callback to the MC plugin
    */
   Ollivanders2 p;

   /**
    * The number of times the player has previously cast this spell.
    */
   int spellUses;

   /**
    * The modifier for this spell based on usage. This is for spells that change behavior based on the caster's experience.
    */
   public double usesModifier = 1;

   /**
    * The vector of the projectile.
    */
   public Vector vector;

   /**
    * Represents which wand the user was holding. See Ollivanders2Common.wandCheck()
    */
   public double rightWand;

   /**
    * The sound this projectile makes as it moves.
    */
   Effect moveEffect = Effect.STEP_SOUND;

   /**
    * The visual effect this projectile has when it moves.
    */
   Material moveEffectData = Material.GLOWSTONE;

   /**
    * If the spell affects blocks, the list of blocks affected that can be used to restore them later.
    */
   public Set<Block> changed = new HashSet<>();

   /**
    * The cooldown for this spell. Spells take mental and physical energy for the caster and cannot be cast in rapid
    * succession.
    */
   static final Long DEFAULT_COOLDOWN = new Long(1000);

   /**
    * The branch of magic this spell is
    */
   protected O2MagicBranch branch = O2MagicBranch.CHARMS;

   /**
    * Flavor text for this spell in spellbooks, etc.  Optional.
    */
   protected ArrayList<String> flavorText;

   /**
    * The description text for this spell in spell books.  Required or spell cannot be written in a book.
    */
   protected String text;

   /**
    * Default constructor should only be used for fake instances of the spell such as when initializing the book
    * text.
    */
   public O2Spell () { }

   /**
    * Constructor
    *
    * @param plugin a callback to the O2 plugin
    * @param player the player casting the spell
    * @param rightWand wand check for the player
    */
   public O2Spell (Ollivanders2 plugin, Player player, Double rightWand)
   {
      location = player.getEyeLocation();
      this.player = player;
      p = plugin;

      vector = location.getDirection().normalize();
      location.add(vector);
      this.rightWand = rightWand;
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
    * Sets the uses modifier that takes into account spell uses and wand type. Returns 10.0 if the uses are 100 and the right wand is held.
    */
   protected void setUsesModifier ()
   {
      spellUses = p.getSpellNum(player, spellType);
      usesModifier = Math.sqrt(spellUses) / rightWand;

      if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL))
      {
         usesModifier *= 2;
      }
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
    * Game tick update on this spell - must be overriden in child classes or the spell exits immediately.
    */
   public void checkEffect ()
   {
      kill();
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
      if (flavorText == null || flavorText.size() < 1)
      {
         return null;
      }
      else
      {
         int index = Math.abs(Ollivanders2Common.random.nextInt() % flavorText.size());
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
      return spellType.getSpellName();
   }
}