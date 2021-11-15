package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collection;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.flags.StateFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A cast spell.
 *
 * Used to be SpellProjectile.
 *
 * @author Azami7
 */
public abstract class O2Spell
{
   /**
    * Max spell lifetime in gameticks, 12000 = 10 minutes, this is used to ensure a code bug doesn't create never-ending
    * spells. Permanent and semi-permanent spells need to use stationary spells or effects.
    */
   public static final int maxSpellLifetime = 12000;

   /**
    * The most number of words a spell name can be.
    */
   public static final int max_spell_words = 3;

   /**
    * The player who cast this spell.
    */
   public Player player;

   /**
    * The type this spell is.
    */
   public O2SpellType spellType;

   /**
    * The location of the spell projectile
    */
   public Location location;

   /**
    * How long this spell projectile has been alive in game ticks.
    */
   public int lifeTicks = 0;

   /**
    * Whether this spell should be terminated.
    */
   private boolean kill = false;

   /**
    * Whether the effects of this spell should be permanent
    */
   protected boolean permanent = false;

   /**
    * Is this a wandless spell
    */
   boolean isWandless = false;

   /**
    * The callback to the MC plugin
    */
   final Ollivanders2 p;

   /**
    * Ollivanders common functions
    */
   Ollivanders2Common common;

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
    * Whether the projectile has hit a target
    */
   private boolean hitTarget = false;

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
   static final long DEFAULT_COOLDOWN = 1000;

   /**
    * The branch of magic this spell is
    */
   protected O2MagicBranch branch = O2MagicBranch.CHARMS;

   /**
    * Flavor text for this spell in spellbooks, etc.  Optional.
    */
   protected List<String> flavorText;
   final static String flavorTextConfigLabel = "_flavorText";

   /**
    * The description text for this spell in spell books.  Required or spell cannot be written in a book.
    */
   protected String text;
   final static String textConfigLabel = "_Text";

   /**
    * A list of block types that cannot be affected by this spell
    */
   List<Material> materialBlackList = new ArrayList<>();

   /**
    * A list of block types that this projectile will pass through
    */
   List<Material> projectilePassThrough = new ArrayList<>();

   /**
    * A list of the worldguard permissions needed for this spell
    */
   List<StateFlag> worldGuardFlags = new ArrayList<>();

   /**
    * Default constructor should only be used for fake instances of the spell such as when initializing the book
    * text.
    */
   public O2Spell(Ollivanders2 plugin)
   {
      p = plugin;
   }

   /**
    * Constructor
    *
    * @param plugin    a callback to the O2 plugin
    * @param player    the player casting the spell
    * @param rightWand wand check for the player
    */
   public O2Spell(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      location = player.getEyeLocation();
      this.player = player;
      p = plugin;
      common = new Ollivanders2Common(p);

      vector = location.getDirection().normalize();
      location.add(vector);
      this.rightWand = rightWand;

      // block types that cannot be affected by any spell
      materialBlackList.addAll(Ollivanders2Common.unbreakableMaterials);


      // block types that this spell's projectiles pass through
      projectilePassThrough.add(Material.AIR);
      projectilePassThrough.add(Material.CAVE_AIR);
      projectilePassThrough.add(Material.WATER);
      projectilePassThrough.add(Material.FIRE);
   }

   /**
    * Initialize the parts of the spell that are based on experience, the player, etc. and not on class
    * constants. This must be called by each spell constructor since usage is based on the specific class
    * type and cannot be determined in the super-class constructors.
    */
   void initSpell()
   {
      if (spellType == null)
      {
         common.printDebugMessage("O2Spell.initSpell() spell type is null, this probably means initSpell was called in an abstract class.", null, null, true);
         kill();
         return;
      }

      setUsesModifier();

      // do spell-specific initialization
      doInitSpell();
   }

   /**
    * The spell-specific initialization based on usage, etc.. Must be overridden by each spell class that
    * has any initializations.
    */
   void doInitSpell()
   {
   }

   /**
    * Game tick update on this spell - must be overridden in child classes or the spell exits immediately.
    */
   public void checkEffect()
   {
      // check whether this spell can exist up until it hits a target
      if (!hitTarget && !isSpellAllowed())
      {
         kill();
         return;
      }

      lifeTicks++;

      if (lifeTicks > maxSpellLifetime)
      {
         kill();
      }

      // do nothing if spell is already marked as killed
      if (!kill)
      {
         // only move the projectile if a target has not been hit
         if (!hitTarget)
         {
            move();
         }

         doCheckEffect();
      }
   }

   /**
    * Moves the projectile forward, creating a particle effect
    */
   public void move()
   {
      // if we've already targeted a block, do not move further
      // if this is somehow called when the spell is set to killed, do nothing
      if (kill)
      {
         return;
      }

      location.add(vector);
      World world = location.getWorld();
      if (world == null)
      {
         common.printDebugMessage("O2Spell.move: world null", null, null, true);
         kill();
         return;
      }

      world.playEffect(location, moveEffect, moveEffectData);

      // if current block type is not a pass-through type, we have hit a target
      Material targetBlockType = location.getBlock().getType();
      if (!projectilePassThrough.contains(targetBlockType))
      {
         targetBlock();
      }

      // if the max duration of the projectile is reached, kill the spell
      if (lifeTicks > 160)
      {
         kill();
      }
   }

   /**
    * Target the current block with this spell.
    *
    * If the block is a pass-through, nothing happens and the projectile will continue
    * If the block is on the blacklist or the spell cannot be cast in this location, kills the spell
    * Otherwise, targets the block
    */
   private void targetBlock()
   {
      Block target = location.getBlock();

      // if we are on a pass-through block, keep going
      if (projectilePassThrough.contains(target.getType()))
      {
         return;
      }

      // determine if this spell is allowed in this location per Ollivanders2 config and WorldGuard
      if (!isSpellAllowed())
      {
         kill();
      }

      // check blockBlackList
      if (materialBlackList.contains(target.getType()))
      {
         kill();
      }

      if (!kill)
      {
         hitTarget = true;
      }
   }

   /**
    * Check to see if this spell is allowed per Ollivanders2 config and WorldGuard.
    *
    * @return true if the spell can exist here, false otherwise
    */
   boolean isSpellAllowed()
   {
      boolean isAllowed = true;

      // determine if this spell is allowed in this location per Ollivanders2 config
      if (!Ollivanders2API.getSpells().isSpellTypeAllowed(location, spellType))
      {
         kill();
         isAllowed = false;
      }
      // determine if spell is allowed in this location per WorldGuard
      else if (!checkWorldGuard())
      {
         kill();
         isAllowed = false;
      }

      if (!isAllowed)
      {
         p.spellFailedMessage(player);
      }

      return isAllowed;
   }

   /**
    * Checks world guard, if enabled, to determine if this spell can be cast here.
    *
    * @return true if the spell can be cast, false otherwise
    */
   private boolean checkWorldGuard()
   {
      if (!Ollivanders2.worldGuardEnabled)
      {
         return true;
      }

      // check every flag needed for this spell
      for (StateFlag flag : worldGuardFlags)
      {
         common.printDebugMessage("checking WG flag " + flag.toString(), null, null, false);

         if (!Ollivanders2.worldGuardO2.checkWGFlag(player, location, flag))
         {
            common.printDebugMessage(spellType.toString() + " cannot be cast because of WorldGuard flag " + flag.toString(), null, null, false);

            return false;
         }
      }

      return true;
   }

   /**
    * Gets entities within a distance of spell target
    *
    * @param radius - radius within which to get entities
    * @return List of entities within one block of projectile
    */
   @NotNull
   List<Entity> getCloseEntities(double radius)
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
            if (((LivingEntity) e).getEyeLocation().distance(location) < radius || ((e instanceof EnderDragon
                    || e instanceof Giant) && ((LivingEntity) e).getEyeLocation().distance(location) < (radius + 5)))
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
   @NotNull
   public List<Item> getItems(double radius)
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
   @NotNull
   public List<LivingEntity> getLivingEntities(double radius)
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
   protected void setUsesModifier()
   {
      // set up spell use modifier
      // the number of times the spell has been cast and then halved if the player is not using their
      // destined wand, doubled if they are using the elder wand
      if (Ollivanders2.maxSpellLevel)
      {
         usesModifier = 200;
      }
      else
      {
         spellUses = p.getSpellCount(player, spellType);

         if (isWandless)
            usesModifier = spellUses;
         else
            usesModifier = spellUses / rightWand;

         // if the caster is affected by HIGHER_SKILL, double their usesModifier
         if (Ollivanders2API.getPlayers(p).playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL))
         {
            usesModifier *= 2;
         }
      }

      common.printDebugMessage("usesModifier = " + usesModifier, null, null, false);
   }

   /**
    * Get the target block for the spell.
    *
    * @return the target block
    */
   @Nullable
   public Block getTargetBlock()
   {
      if (hitTarget)
      {
         return location.getBlock();
      }
      else
         return null;
   }

   /**
    * The spell-specific actions taken for each check effect. This must be overridden by each spell or the spell
    * will do nothing.
    */
   abstract protected void doCheckEffect();

   /**
    * This kills the spell.
    */
   public void kill()
   {
      stopProjectile();

      if (!permanent)
         revert();

      kill = true;
   }

   /**
    * Reverts any changes made to blocks if the effects are temporary. This must be overridden by each spell that has
    * a revert action.
    */
   protected void revert()
   {
   }

   /**
    * Whether this spell has been killed
    *
    * @return true if the spell has been terminated, false otherwise
    */
   public boolean isKilled()
   {
      return kill;
   }

   /**
    * Stops the spell projectile from moving further
    */
   void stopProjectile()
   {
      hitTarget = true;
   }

   /**
    * Whether this spell has hit a target
    *
    * @return true if the spell has hit a target, false otherwise
    */
   public boolean hasHitTarget()
   {
      return hitTarget;
   }

   /**
    * @return the cool-down time for this spell in ticks
    */
   public long getCoolDown()
   {
      return DEFAULT_COOLDOWN;
   }

   /**
    * @return the book text for this spell
    */
   @NotNull
   public String getText()
   {
      // first check to see if it has been set with config
      if (p.getConfig().isSet(spellType.toString() + textConfigLabel))
      {
         String t = p.getConfig().getString(spellType.toString() + textConfigLabel);
         if (t != null && t.length() > 0)
            return t;
      }

      return text;
   }

   /**
    * @return the flavor text for this spell
    */
   @Nullable
   public String getFlavorText()
   {
      // first check to see if it has been set with config
      if (p.getConfig().isSet(spellType.toString() + flavorTextConfigLabel))
      {
         String f = p.getConfig().getString(spellType.toString() + flavorTextConfigLabel);
         if (f != null && f.length() > 0)
            return f;
      }

      if (flavorText == null || flavorText.size() < 1)
         return null;
      else
      {
         int index = Math.abs(Ollivanders2Common.random.nextInt() % flavorText.size());
         return flavorText.get(index);
      }
   }

   /**
    * @return the branch of magic for this spell
    */
   @NotNull
   public O2MagicBranch getMagicBranch()
   {
      return branch;
   }

   /**
    * @return the name of the spell
    */
   @NotNull
   public String getName()
   {
      return spellType.getSpellName();
   }

   /**
    * Get the level of this spell. This is primarily for use with counter-spells
    *
    * @return the level of the spell
    */
   @NotNull
   public Ollivanders2Common.MagicLevel getLevel()
   {
      return spellType.getLevel();
   }

   /**
    * Determine if this entity can be targeted for a potentially harmful spell
    *
    * @param entity the entity to check
    * @return true if it can be targeted, false otherwise
    */
   boolean entityHarmWGCheck(Entity entity)
   {
      // players
      if (entity instanceof Player && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.PVP))
         return false;

      // friendly mobs
      if (entity instanceof Animals && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.DAMAGE_ANIMALS))
         return false;

      // items
      if (entity instanceof Item && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.ITEM_PICKUP))
         return false;

      // vehicles
      if (entity instanceof Vehicle && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.DESTROY_VEHICLE))
         return false;

      // item frames
      if (entity instanceof ItemFrame && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.ENTITY_ITEM_FRAME_DESTROY))
         return false;

      // paintings
      if (entity instanceof Painting && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.ENTITY_PAINTING_DESTROY))
         return false;

      return true;
   }
}