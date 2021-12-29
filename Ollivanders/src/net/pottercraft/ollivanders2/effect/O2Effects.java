package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Semaphore;

/**
 * Class for managing all effects on players.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class O2Effects implements Listener
{
   final Ollivanders2 p;
   final Ollivanders2Common common;

   final static Semaphore semaphore = new Semaphore(1);

   public static final String effectLabelPrefix = "Effect_";

   public static HashMap<PotionEffectType, Ollivanders2Common.MagicLevel> potionEffectLevels = new HashMap<>() {{
      put(PotionEffectType.ABSORPTION, Ollivanders2Common.MagicLevel.OWL);
      put(PotionEffectType.BAD_OMEN, Ollivanders2Common.MagicLevel.NEWT);
      put(PotionEffectType.BLINDNESS, Ollivanders2Common.MagicLevel.OWL);
      put(PotionEffectType.CONDUIT_POWER, Ollivanders2Common.MagicLevel.NEWT);
      put(PotionEffectType.CONFUSION, Ollivanders2Common.MagicLevel.OWL);
      put(PotionEffectType.DAMAGE_RESISTANCE, Ollivanders2Common.MagicLevel.NEWT);
      put(PotionEffectType.DOLPHINS_GRACE, Ollivanders2Common.MagicLevel.NEWT);
      put(PotionEffectType.FAST_DIGGING, Ollivanders2Common.MagicLevel.BEGINNER);
      put(PotionEffectType.FIRE_RESISTANCE, Ollivanders2Common.MagicLevel.NEWT);
      put(PotionEffectType.GLOWING, Ollivanders2Common.MagicLevel.BEGINNER);
      put(PotionEffectType.HARM, Ollivanders2Common.MagicLevel.OWL);
      put(PotionEffectType.HEAL, Ollivanders2Common.MagicLevel.OWL);
      put(PotionEffectType.HEALTH_BOOST, Ollivanders2Common.MagicLevel.NEWT);
      put(PotionEffectType.HERO_OF_THE_VILLAGE, Ollivanders2Common.MagicLevel.NEWT);
      put(PotionEffectType.HUNGER, Ollivanders2Common.MagicLevel.BEGINNER);
      put(PotionEffectType.INCREASE_DAMAGE, Ollivanders2Common.MagicLevel.NEWT);
      put(PotionEffectType.INVISIBILITY, Ollivanders2Common.MagicLevel.EXPERT);
      put(PotionEffectType.JUMP, Ollivanders2Common.MagicLevel.BEGINNER);
      put(PotionEffectType.LEVITATION, Ollivanders2Common.MagicLevel.OWL);
      put(PotionEffectType.LUCK, Ollivanders2Common.MagicLevel.BEGINNER);
      put(PotionEffectType.NIGHT_VISION, Ollivanders2Common.MagicLevel.BEGINNER);
      put(PotionEffectType.POISON, Ollivanders2Common.MagicLevel.OWL);
      put(PotionEffectType.REGENERATION, Ollivanders2Common.MagicLevel.NEWT);
      put(PotionEffectType.SATURATION, Ollivanders2Common.MagicLevel.BEGINNER);
      put(PotionEffectType.SLOW, Ollivanders2Common.MagicLevel.BEGINNER);
      put(PotionEffectType.SLOW_DIGGING, Ollivanders2Common.MagicLevel.BEGINNER);
      put(PotionEffectType.SLOW_FALLING, Ollivanders2Common.MagicLevel.NEWT);
      put(PotionEffectType.SPEED, Ollivanders2Common.MagicLevel.BEGINNER);
      put(PotionEffectType.UNLUCK, Ollivanders2Common.MagicLevel.BEGINNER);
      put(PotionEffectType.WATER_BREATHING, Ollivanders2Common.MagicLevel.NEWT);
      put(PotionEffectType.WEAKNESS, Ollivanders2Common.MagicLevel.OWL);
      put(PotionEffectType.WITHER, Ollivanders2Common.MagicLevel.NEWT);
   }};

   /**
    * Thread-safe storage class for the effect data on players.
    */
   private class EffectsData
   {
      /**
       * A list of all active effects on all online players
       */
      final private Map<UUID, Map<O2EffectType, O2Effect>> activeEffects = new HashMap<>();

      /**
       * A list of all saved effects for all offline players
       */
      final private Map<UUID, Map<O2EffectType, Integer>> savedEffects = new HashMap<>();

      /**
       * Constructor
       */
      EffectsData() {}

      /**
       * Get a copy of the effects active on this player.
       *
       * @param pid the id of the player
       * @return a map of all the effects on this player
       */
      @NotNull
      synchronized Map<O2EffectType, O2Effect> getPlayerActiveEffects(@NotNull UUID pid)
      {
         Map<O2EffectType, O2Effect> effects = new HashMap<>();

         try
         {
            semaphore.acquire();

            if (activeEffects.containsKey(pid))
            {
               effects = new HashMap<>(activeEffects.get(pid));
            }
         }
         catch (Exception e)
         {
            common.printDebugMessage("O2Effects.getPlayerActiveEffects: failed to acquire mutex in getPlayerActiveEffects", e, null, true);
         }
         finally
         {
            semaphore.release();
         }

         return effects;
      }

      /**
       * Get an effect object for a player.
       *
       * @param pid        the id of the player
       * @param effectType the effect type to search for
       * @return the effect object if it was found
       */
      @Nullable
      synchronized O2Effect getActiveEffect(@NotNull UUID pid, @NotNull O2EffectType effectType)
      {
         O2Effect effect = null;

         try
         {
            semaphore.acquire();

            if (activeEffects.containsKey(pid))
            {
               Map<O2EffectType, O2Effect> effects = activeEffects.get(pid);
               if (effects.containsKey(effectType))
               {
                  effect = effects.get(effectType);
               }
            }
         }
         catch (Exception e)
         {
            common.printDebugMessage("O2Effects.getActiveEffect: failed to acquire mutex in getActiveEffect", e, null, true);
         }
         finally
         {
            semaphore.release();
         }

         return effect;
      }

      /**
       * Get a copy of the effects saved for this player.
       *
       * @param pid the id of the player
       * @return a map of all the saved effects for this player
       */
      @NotNull
      synchronized Map<O2EffectType, Integer> getPlayerSavedEffects(@NotNull UUID pid)
      {
         Map<O2EffectType, Integer> effects = new HashMap<>();

         try
         {
            semaphore.acquire();

            if (savedEffects.containsKey(pid))
            {
               effects = new HashMap<>(savedEffects.get(pid));
            }
         }
         catch (Exception e)
         {
            common.printDebugMessage("O2Effects.getPlayerSavedEffects: failed to acquire mutex in getPlayerSavedEffects", e, null, true);
         }
         finally
         {
            semaphore.release();
         }

         return effects;
      }

      /**
       * Update the map of saved effects for this player.
       *
       * @param pid     the id of the player
       * @param effects the map of effects and durations
       */
      synchronized void updatePlayerSavedEffects(@NotNull UUID pid, @NotNull Map<O2EffectType, Integer> effects)
      {
         try
         {
            semaphore.acquire();

            savedEffects.remove(pid);

            savedEffects.put(pid, effects);
         }
         catch (Exception e)
         {
            common.printDebugMessage("O2Effects.updatePlayerSavedEffects: failed to acquire mutex in updatePlayerSavedEffects", e, null, true);
         }
         finally
         {
            semaphore.release();
         }
      }

      /**
       * Update the map of saved effects for this player.
       *
       * @param pid     the id of the player
       * @param effects the map of effects and durations
       */
      synchronized void updatePlayerActiveEffects(@NotNull UUID pid, @NotNull Map<O2EffectType, O2Effect> effects)
      {
         try
         {
            semaphore.acquire();

            activeEffects.remove(pid);

            activeEffects.put(pid, effects);
         }
         catch (Exception e)
         {
            common.printDebugMessage("O2Effects.updatePlayerActiveEffects: failed to acquire mutex in updatePlayerActiveEffects", e, null, true);
         }
         finally
         {
            semaphore.release();
         }
      }

      /**
       * Clear all effects for this player.
       *
       * @param pid the id of the player
       */
      synchronized void resetEffects(@NotNull UUID pid)
      {
         try
         {
            semaphore.acquire();

            if (activeEffects.containsKey(pid))
            {
               Map<O2EffectType, O2Effect> playerEffects = activeEffects.get(pid);

               for (O2Effect effect : playerEffects.values())
               {
                  effect.kill();
               }
            }

            savedEffects.remove(pid);
         }
         catch (Exception e)
         {
            common.printDebugMessage("O2Effects.resetEffects: failed to acquire mutex in resetEffects", e, null, true);
         }
         finally
         {
            semaphore.release();
         }
      }
   }

   /**
    * The data for all active effects
    */
   EffectsData effectsData = new EffectsData();

   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public O2Effects(@NotNull Ollivanders2 plugin)
   {
      p = plugin;
      common = new Ollivanders2Common(plugin);

      p.getServer().getPluginManager().registerEvents(this, p);
   }

   public void onEnable()
   {
      //
      // lycanthropy
      //
      O2EffectType.LYCANTHROPY.setEnabled(p.getConfig().getBoolean("enableLycanthropy"));
      if (O2EffectType.LYCANTHROPY.isEnabled())
         p.getLogger().info("Enabling lycanthropy.");
      else
         p.getLogger().info("Disabling lycanthropy.");
   }

   /**
    * Take effect actions when a player is damaged
    *
    * @param event the event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onEntityDamageByEntityEvent(@NotNull EntityDamageByEntityEvent event)
   {
      Entity entity = event.getEntity();
      if (!(entity instanceof Player))
         return;

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(entity.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnEntityDamageByEntityEvent(event);
      }
   }

   /**
    * Take effect actions when a player interacts with an item
    *
    * @param event the event
    */
   @EventHandler (priority = EventPriority.HIGHEST)
   public void onPlayerInteractEvent(@NotNull PlayerInteractEvent event)
   {
      Player player = event.getPlayer();

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnPlayerInteractEvent(event);
      }
   }

   /**
    * Handles all effects related to players speaking.
    *
    * @param event the event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event)
   {
      Player player = event.getPlayer();

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnAsyncPlayerChatEvent(event);
      }
   }

   /**
    * Handles all effects related to players sleeping.
    *
    * @param event the event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void onPlayerBedEnterEvent(@NotNull PlayerBedEnterEvent event)
   {
      Player player = event.getPlayer();

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnPlayerBedEnterEvent(event);
      }
   }

   /**
    * Handles all effects related to players flying.
    *
    * @param event the event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void onPlayerToggleFlightEvent(@NotNull PlayerToggleFlightEvent event)
   {
      Player player = event.getPlayer();

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnPlayerToggleFlightEvent(event);
      }
   }

   /**
    * Handles all effects related to players sneaking.
    *
    * @param event the event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void onPlayerToggleSleepEvent(@NotNull PlayerToggleSneakEvent event)
   {
      Player player = event.getPlayer();

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnPlayerToggleSneakEvent(event);
      }
   }

   /**
    * Handles all effects related to players sprinting.
    *
    * @param event the event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void onPlayerToggleSprintEvent(@NotNull PlayerToggleSprintEvent event)
   {
      Player player = event.getPlayer();

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnPlayerToggleSprintEvent(event);
      }
   }

   /**
    * Handles all effects related to players velocity changes
    *
    * @param event the event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void onPlayerVelocityEvent(@NotNull PlayerVelocityEvent event)
   {
      Player player = event.getPlayer();

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnPlayerVelocityEvent(event);
      }
   }

   /**
    * Handles all effects related to players picking up items
    *
    * @param event the event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onEntityPickupItemEvent(@NotNull EntityPickupItemEvent event)
   {
      Entity entity = event.getEntity();
      if (!(entity instanceof Player))
         return;

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(entity.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnPlayerPickupItemEvent(event);
      }
   }

   /**
    * Handles all effects related to player holds an item
    *
    * @param event the event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void onPlayerItemHeldEvent(@NotNull PlayerItemHeldEvent event)
   {
      Player player = event.getPlayer();

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnPlayerItemHeldEvent(event);
      }
   }

   /**
    * Handles all effects related to player consumes an item
    *
    * @param event the event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void onPlayerItemConsumeEvent(@NotNull PlayerItemConsumeEvent event)
   {
      Player player = event.getPlayer();

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnPlayerItemConsumeEvent(event);
      }
   }

   /**
    * Handles all effects related to player drops an item
    *
    * @param event the event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void onPlayerDropItemEvent(@NotNull PlayerDropItemEvent event)
   {
      Player player = event.getPlayer();

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnPlayerDropItemEvent(event);
      }
   }

   /**
    * Handles all effects related to the player moving
    *
    * @param event the event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void onPlayerMoveEvent(@NotNull PlayerMoveEvent event)
   {
      Player player = event.getPlayer();

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(player.getUniqueId());
      for (O2Effect effect : activeEffects.values())
      {
         effect.doOnPlayerMoveEvent(event);
      }
   }

   /**
    * Get a list of all the Ollivanders effects this player has on them.
    *
    * @param pid the id of the player
    * @return a list of the effects active on this player
    */
   @NotNull
   public List<O2EffectType> getEffects(@NotNull UUID pid)
   {
      Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

      return new ArrayList<>(playerEffects.keySet());
   }

   /**
    * Determines if this player is affected by this effect. Only checks active effects.
    *
    * @param pid        the id of the player
    * @param effectType the effect type to check for
    * @return true if they have this effect, false otherwise
    */
   public boolean hasEffect(@NotNull UUID pid, @NotNull O2EffectType effectType)
   {
      Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

      return playerEffects.containsKey(effectType);
   }

   /**
    * Determines if this player is affected by any effect. Only checks active effects.
    *
    * @param pid the id of the player
    * @return true if they have this effect, false otherwise
    */
   public boolean hasEffects(@NotNull UUID pid)
   {
      return !effectsData.getPlayerActiveEffects(pid).isEmpty();
   }

   /**
    * On player join, add any saved effects.
    *
    * @param pid the id of the player.
    */
   public synchronized void onJoin(@NotNull UUID pid)
   {
      Map<O2EffectType, Integer> savedEffects = effectsData.getPlayerSavedEffects(pid);
      Map<O2EffectType, O2Effect> activeEffects = new HashMap<>();

      if (savedEffects.size() < 1)
         return;

      Player player = p.getServer().getPlayer(pid);
      if (player == null)
      {
         common.printDebugMessage("O2Effects.onJoin: player is null", null, null, true);
         return;
      }

      common.printDebugMessage("Applying effects for " + player.getDisplayName(), null, null, false);

      for (Entry<O2EffectType, Integer> entry : savedEffects.entrySet())
      {
         O2EffectType effectType = entry.getKey();
         int duration = entry.getValue();

         Class<?> effectClass = effectType.getClassName();

         O2Effect effect;
         try
         {
            effect = (O2Effect)effectClass.getConstructor(Ollivanders2.class, int.class, UUID.class).newInstance(p, duration, pid);
         }
         catch (Exception e)
         {
            common.printDebugMessage("O2Effects.onJoin: failed to create class for " + effectType.toString(), e, null, true);

            continue;
         }

         if (effectType.isEnabled())
         {
            activeEffects.put(effectType, effect);
            common.printDebugMessage("   added " + effectType.toString(), null, null, false);
         }
      }

      effectsData.updatePlayerActiveEffects(pid, activeEffects);
   }

   /**
    * On quit, save all active effects.
    *
    * @param pid the id of the player
    */
   public synchronized void onQuit(@NotNull UUID pid)
   {
      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
      Map<O2EffectType, Integer> savedEffects = new HashMap<>();

      for (Entry<O2EffectType, O2Effect> entry : activeEffects.entrySet())
      {
         savedEffects.put(entry.getKey(), entry.getValue().duration);
      }

      effectsData.updatePlayerSavedEffects(pid, savedEffects);
   }

   /**
    * On death, reset all the effects for a player.
    *
    * @param pid the id of the player
    */
   public synchronized void onDeath(@NotNull UUID pid)
   {
      effectsData.resetEffects(pid);
   }

   /**
    * Serialize effects data to strings.
    *
    * @param pid the id of the player to serialize
    * @return a map of the effect type and duration serialized as strings
    */
   @NotNull
   public Map<String, String> serializeEffects(@NotNull UUID pid)
   {
      Map<String, String> serialized = new HashMap<>();

      Map<O2EffectType, Integer> savedEffects = effectsData.getPlayerSavedEffects(pid);
      for (Entry<O2EffectType, Integer> entry : savedEffects.entrySet())
      {
         serialized.put(effectLabelPrefix + entry.getKey().toString(), entry.getValue().toString());
      }

      return serialized;
   }

   /**
    * Deserialize saved effect from strings.
    *
    * @param pid            the id of the player this is saved for
    * @param effectsString  the effect string
    * @param durationString the duration string
    */
   public void deserializeEffect(@NotNull UUID pid, @NotNull String effectsString, @NotNull String durationString)
   {
      Map<O2EffectType, Integer> savedEffects = effectsData.getPlayerSavedEffects(pid);

      String effectName = effectsString.replaceFirst(effectLabelPrefix, "");
      Integer duration = Ollivanders2API.common.integerFromString(durationString);

      if (duration != null)
      {
         try
         {
            O2EffectType effectType = O2EffectType.valueOf(effectName);
            savedEffects.put(effectType, duration);
         }
         catch (Exception e)
         {
            common.printDebugMessage("Failed to deserialize effect " + effectName, null, null, false);
         }
      }

      effectsData.updatePlayerSavedEffects(pid, savedEffects);
   }

   /**
    * Add an effect to this player.
    *
    * @param e the effect to add to this player
    */
   public synchronized void addEffect(@NotNull O2Effect e)
   {
      if (!e.effectType.isEnabled())
         return;

      UUID pid = e.getTargetID();

      Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

      // do not allow multiple shape-shifting effects at the same time
      if (e instanceof ShapeShiftSuper)
      {
         for (O2Effect effect : playerEffects.values())
         {
            if (effect instanceof ShapeShiftSuper)
            {
               return;
            }
         }
      }

      if (playerEffects.containsKey(e.effectType))
      {
         // increase effect duration by the amount of this effect's
         O2Effect effect = playerEffects.get(e.effectType);
         e.duration += effect.duration;
         playerEffects.replace(e.effectType, e);
      }
      else
      {
         // add this effect
         playerEffects.put(e.effectType, e);
      }

      effectsData.updatePlayerActiveEffects(pid, playerEffects);
      common.printDebugMessage("Added effect " + e.effectType.toString() + " to " + pid.toString(), null, null, false);
   }

   /**
    * Remove an effect from this player.
    *
    * @param pid        the id of the player
    * @param effectType the effect to remove from this player
    */
   public synchronized void removeEffect(@NotNull UUID pid, @NotNull O2EffectType effectType)
   {
      Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

      O2Effect effect = playerEffects.get(effectType);

      if (effect != null)
      {
         effect.kill();

         playerEffects.get(effectType).doRemove();

         playerEffects.remove(effectType);
      }
      else
      {
         common.printDebugMessage("O2Effects.removeEffect: effect to remove is null.", null, null, false);
      }

      effectsData.updatePlayerActiveEffects(pid, playerEffects);
      common.printDebugMessage("Removed effect " + effectType.toString() + " from " + pid.toString(), null, null, false);
   }

   /**
    * Get an effect for a player.
    *
    * @param pid        the id of the player
    * @param effectType the type of effect to get
    * @return the effect object if found, null otherwise
    */
   public synchronized O2Effect getEffect(@NotNull UUID pid, @NotNull O2EffectType effectType)
   {
      return effectsData.getActiveEffect(pid, effectType);
   }

   /**
    * Run game heartbeat upkeep for all effects for this player. This is not done for every player because
    * we only want to update the ones that are currently online, therefore we let the OllivandersScheduler
    * class tell us which ones to update.
    *
    * @param pid the id of the player.
    */
   public void upkeep(@NotNull UUID pid)
   {
      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);

      for (O2Effect effect : activeEffects.values())
      {
         effect.checkEffect();

         if (effect.isKilled() || !effect.effectType.isEnabled())
         {
            removeEffect(pid, effect.effectType);
         }
      }
   }

   /**
    * Age all non-permanent spell effects by a specified amount.
    *
    * @param pid    the id of the player
    * @param amount the amount to age the effects by
    */
   public void ageAllEffects(@NotNull UUID pid, int amount)
   {
      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
      Collection<O2Effect> effects = activeEffects.values();

      for (O2Effect effect : effects)
      {
         if (!effect.isPermanent())
         {
            effect.age(amount);
         }
      }

      effectsData.updatePlayerActiveEffects(pid, activeEffects);
   }

   /**
    * Age a specific effect on a player by a specified amount.
    *
    * @param pid        the id of the player
    * @param effectType the effect to age
    * @param amount     the amount to age the effect
    */
   public void ageEffect(@NotNull UUID pid, @NotNull O2EffectType effectType, int amount)
   {
      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);

      if (activeEffects.containsKey(effectType))
      {
         O2Effect effect = activeEffects.get(effectType);
         effect.duration = effect.duration - amount;
      }

      effectsData.updatePlayerActiveEffects(pid, activeEffects);
   }

   /**
    * Age a specific effect on a player by a specified percent.
    *
    * @param pid        the id of the player
    * @param effectType the effect to age
    * @param percent    the percent to age the effect
    */
   public void ageEffectByPercent(@NotNull UUID pid, @NotNull O2EffectType effectType, int percent)
   {
      if (percent > 100)
      {
         percent = 100;
      }
      else if (percent < 1)
      {
         percent = 1;
      }

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
      if (activeEffects.containsKey(effectType))
      {
         O2Effect effect = activeEffects.get(effectType);

         if (!effect.isPermanent())
         {
            effect.duration = effect.duration - (effect.duration * (percent / 100));
         }
      }

      effectsData.updatePlayerActiveEffects(pid, activeEffects);
   }

   /**
    * Age all effects on a player by a specified percent.
    *
    * @param pid     the id of the player
    * @param percent the percent to age the effect
    */
   public void ageAllEffectsByPercent(@NotNull UUID pid, int percent)
   {
      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
      Collection<O2Effect> effects = activeEffects.values();

      for (O2Effect effect : effects)
      {
         if (!effect.isPermanent())
         {
            ageEffectByPercent(pid, effect.effectType, percent);
         }
      }
   }

   /**
    * Get the information for a detectable effect, if any can be detected. This is primarily used for the spell Informous.
    *
    * @param pid the id of the player to check
    * @return text about detectable effect or null if none found.
    */
   @Nullable
   public String detectEffectWithInformous(@NotNull UUID pid)
   {
      common.printDebugMessage("O2Effects.detectEffectWithInformous: detecting effcts with Informous", null, null, false);
      String infoText = null;

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
      Collection<O2Effect> effects = activeEffects.values();

      common.printDebugMessage("O2Effects.detectEffectWithInformous: found " + activeEffects.keySet().size() + " active effects", null, null, false);

      for (O2Effect effect : effects)
      {
         common.printDebugMessage("O2Effects.detectEffectWithInformous: checking effect " + effect.effectType.toString(), null, null, false);

         if (effect.informousText != null)
         {
            infoText = effect.informousText;
            break;
         }
      }

      return infoText;
   }

   /**
    * Get the information for a mind readable effect, if any can be detected. This is primarily used for the spell Legilimens.
    *
    * @param pid the id of the player to check
    * @return text about detectable effect or null if none found.
    */
   @Nullable
   public String detectEffectWithLegilimens(@NotNull UUID pid)
   {
      String infoText = null;

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
      Collection<O2Effect> effects = activeEffects.values();

      for (O2Effect effect : effects)
      {
         if (effect.legilimensText != null)
         {
            infoText = effect.legilimensText;
            break;
         }
      }

      return infoText;
   }

   /**
    * The effects command for ops management of effects
    *
    * @param sender the player that issued the command
    * @param args   the arguments for the command, if any
    * @param p callback to the Ollivanders plugin
    * @return true unless an error occurred
    */
   public static boolean runCommand(@NotNull CommandSender sender, @NotNull String[] args, @NotNull Ollivanders2 p)
   {
      if (!(sender instanceof Player))
         return false;

      if (!sender.hasPermission("Ollivanders2.admin"))
         return false;

      if (args.length < 2 || args.length > 3)
         return commandUsage(sender);

      String effectName = args[1].toUpperCase();

      O2EffectType effectType;
      try
      {
         effectType = O2EffectType.valueOf(effectName);
      }
      catch (Exception e)
      {
         sender.sendMessage(Ollivanders2.chatColor + "No effect named " + effectName + ".\n");
         return true;
      }

      Player targetPlayer;
      if (args.length == 3)
      {
         String playerName = args[2];
         targetPlayer = p.getServer().getPlayer(playerName);
         if (targetPlayer == null)
         {
            sender.sendMessage(Ollivanders2.chatColor + "Unable to find player " + playerName + ".\n");
            return true;
         }
      }
      else
         targetPlayer = (Player) sender;

      toggleEffect(sender, targetPlayer, effectType, p);

      return true;
   }

   /**
    * Toggle an effect on a player.
    *
    * @param sender the player that issued the command
    * @param player the target player
    * @param effectType the effect type
    * @param p a callback to the plugin
    */
   private static void toggleEffect(@NotNull CommandSender sender, @NotNull Player player, @NotNull O2EffectType effectType, @NotNull Ollivanders2 p)
   {
      if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), effectType))
      {
         Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), effectType);
         sender.sendMessage(Ollivanders2.chatColor + "Removed " + effectType.toString() + " from " + player.getName() + ".\n");
      }
      else
      {
         Class<?> effectClass = effectType.getClassName();

         if (Ollivanders2.debug)
            p.getLogger().info("Trying to add effect " + effectType.toString());

         O2Effect effect;

         try
         {
            effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, UUID.class).newInstance(p, 1200, player.getUniqueId());
         }
         catch (Exception e)
         {
            sender.sendMessage(Ollivanders2.chatColor + "Failed to add effect " + effectType.toString() + " to " + player.getName() + ".\n");
            e.printStackTrace();
            return;
         }

         if (effect.effectType.isEnabled())
         {
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
            sender.sendMessage(Ollivanders2.chatColor + "Added " + effectType.toString() + " to " + player.getName() + ".\n");
         }
         else
            sender.sendMessage(Ollivanders2.chatColor + effectType.toString() + " is currently disabled in your server config.\n");
      }
   }

   /**
    * Usage message for Effect subcommands.
    *
    * @param sender the player that issued the command
    */
   public static boolean commandUsage(@NotNull CommandSender sender)
   {
      sender.sendMessage(Ollivanders2.chatColor
              + "/ollivanders2 effect effect_name - toggles the named effect on the command sender" + "\n"
              + "/ollivanders2 effect effect_name player_name - toggles the named effect on the player");

      return true;
   }
}
