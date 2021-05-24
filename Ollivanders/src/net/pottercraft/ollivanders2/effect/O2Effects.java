package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.entity.Player;
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
public class O2Effects
{
   final Ollivanders2 p;
   final Ollivanders2Common common;

   final static Semaphore semaphore = new Semaphore(1);

   public static final String effectLabelPrefix = "Effect_";

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
      EffectsData () { }

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

         activeEffects.put(effectType, effect);
         common.printDebugMessage("   added " + effectType.toString(), null, null, false);
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
            p.getLogger().info("Failed to deserialize effect " + effectName);
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

         if (effect.isKilled())
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
            p.getLogger().info(effect.informousText);
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
}
