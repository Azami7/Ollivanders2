package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;

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
   Ollivanders2 p;

   static Semaphore semaphore = new Semaphore(1);

   public final String effectLabelPrefix = "Effect_";

   /**
    * Thread-safe storage class for the effect data on players.
    */
   private class EffectsData
   {
      /**
       * A list of all active effects on all online players
       */
      private Map<UUID, Map<O2EffectType, O2Effect>> activeEffects = new HashMap<>();

      /**
       * A list of all saved effects for all offline players
       */
      private Map<UUID, Map<O2EffectType, Integer>> savedEffects = new HashMap<>();

      /**
       * Constructor
       */
      EffectsData () { }

      /**
       * Get a copy of the effects active on this player.
       *
       * @param pid the id of the player
       * @return
       */
      synchronized Map<O2EffectType, O2Effect> getPlayerActiveEffects (UUID pid)
      {
         Map<O2EffectType, O2Effect> effects = new HashMap<>();

         if (pid != null)
         {
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
               p.getLogger().warning("Failed to acquire mutex in getPlayerActiveEffects");
               e.printStackTrace();
            }
            finally
            {
               semaphore.release();
            }
         }

         return effects;
      }

      /**
       * Get an effect object for a player.
       *
       * @param pid the id of the player
       * @param effectType the effect type to search for
       * @return the effect object if it was found
       */
      synchronized O2Effect getActiveEffect (UUID pid, O2EffectType effectType)
      {
         O2Effect effect = null;
         if (pid != null && effectType != null)
         {
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
               p.getLogger().warning("Failed to acquire mutex in getActiveEffect");
               e.printStackTrace();
            }
            finally
            {
               semaphore.release();
            }
         }

         return effect;
      }

      /**
       * Get a copy of the effects saved for this player.
       *
       * @param pid the id of the player
       * @return
       */
      synchronized Map<O2EffectType, Integer> getPlayerSavedEffects (UUID pid)
      {
         Map<O2EffectType, Integer> effects = new HashMap<>();

         if (pid != null)
         {
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
               p.getLogger().warning("Failed to acquire mutex in getPlayerSavedEffects");
               e.printStackTrace();
            }
            finally
            {
               semaphore.release();
            }
         }

         return effects;
      }

      /**
       * Update the map of saved effects for this player.
       *
       * @param pid the id of the player
       * @param effects the map of effects and durations
       */
      synchronized void updatePlayerSavedEffects (UUID pid, Map<O2EffectType, Integer> effects)
      {
         if (pid != null && effects != null)
         {
            try
            {
               semaphore.acquire();

               if (savedEffects.containsKey(pid))
               {
                  savedEffects.remove(pid);
               }

               savedEffects.put(pid, effects);
            }
            catch (Exception e)
            {
               p.getLogger().warning("Failed to acquire mutex in updatePlayerSavedEffects");
               e.printStackTrace();
            }
            finally
            {
               semaphore.release();
            }
         }
      }

      /**
       * Update the map of saved effects for this player.
       *
       * @param pid the id of the player
       * @param effects the map of effects and durations
       */
      synchronized void updatePlayerActiveEffects (UUID pid, Map<O2EffectType, O2Effect> effects)
      {
         if (pid != null && effects != null)
         {
            try
            {
               semaphore.acquire();

               if (activeEffects.containsKey(pid))
               {
                  activeEffects.remove(pid);
               }

               activeEffects.put(pid, effects);
            }
            catch (Exception e)
            {
               p.getLogger().warning("Failed to acquire mutex in updatePlayerActiveEffects");
               e.printStackTrace();
            }
            finally
            {
               semaphore.release();
            }
         }
      }

      /**
       * Clear all effects for this player.
       *
       * @param pid the id of the player
       */
      synchronized void resetEffects (UUID pid)
      {
         if (pid != null)
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
               if (savedEffects.containsKey(pid))
               {
                  savedEffects.remove(pid);
               }
            }
            catch (Exception e)
            {
               p.getLogger().warning("Failed to acquire mutex in resetEffects");
               e.printStackTrace();
            }
            finally
            {
               semaphore.release();
            }
         }
      }
   }

   EffectsData effectsData = new EffectsData();

   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public O2Effects (Ollivanders2 plugin)
   {
      p = plugin;
   }

   /**
    * Get a list of all the Ollivanders effects this player has on them.
    *
    * @param pid the id of the player
    * @return a list of the effects active on this player
    */
   public List<O2EffectType> getEffects (UUID pid)
   {
      List<O2EffectType> effectedBy = new ArrayList<>();

      if (pid != null)
      {
         Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

         if (playerEffects != null)
         {
            effectedBy.addAll(playerEffects.keySet());
         }
      }

      return effectedBy;
   }

   /**
    * Determines if this player is affected by this effect. Only checks active effects.
    *
    * @param pid the id of the player
    * @param effectType the effect type to check for
    * @return true if they have this effect, false otherwise
    */
   public boolean hasEffect (UUID pid, O2EffectType effectType)
   {
      boolean isAffected = false;

      if (pid != null)
      {
         Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

         if (playerEffects != null)
         {
            if (playerEffects.containsKey(effectType))
            {
               isAffected = true;
            }
         }
      }

      return isAffected;
   }

   /**
    * On player join, add any saved effects.
    *
    * @param pid the id of the player.
    */
   public synchronized void onJoin (UUID pid)
   {
      if (pid == null)
         return;

      Map<O2EffectType, Integer> savedEffects = effectsData.getPlayerSavedEffects(pid);
      Map<O2EffectType, O2Effect> activeEffects = new HashMap<>();

      if (savedEffects.size() < 1)
         return;

      if (Ollivanders2.debug)
         p.getLogger().info("Applying effects for " + p.getServer().getPlayer(pid).getDisplayName());

      for (Entry<O2EffectType, Integer> entry : savedEffects.entrySet())
      {
         O2EffectType effectType = entry.getKey();
         Integer duration = entry.getValue();

         Class effectClass = effectType.getClassName();

         O2Effect effect;
         try
         {
            effect = (O2Effect)effectClass.getConstructor(Ollivanders2.class, Integer.class, UUID.class).newInstance(p, duration, pid);
         }
         catch (Exception e)
         {
            if (Ollivanders2.debug)
            {
               p.getLogger().info("Failed to create class for " + effectType.toString());
               e.printStackTrace();
            }
            continue;
         }

         activeEffects.put(effectType, effect);

         if (Ollivanders2.debug)
            p.getLogger().info("   added " + effectType.toString());
      }

      effectsData.updatePlayerActiveEffects(pid, activeEffects);
   }

   /**
    * On quit, save all active effects.
    *
    * @param pid the id of the player
    */
   public synchronized void onQuit (UUID pid)
   {
      if (pid == null)
         return;

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
      Map<O2EffectType, Integer> savedEffects = new HashMap<>();

      if (activeEffects != null)
      {
         for (Entry<O2EffectType, O2Effect> entry : activeEffects.entrySet())
         {
            savedEffects.put(entry.getKey(), entry.getValue().duration);
         }
      }

      effectsData.updatePlayerSavedEffects(pid, savedEffects);
   }

   /**
    * On death, reset all the effects for a player.
    *
    * @param pid the id of the player
    */
   public synchronized void onDeath (UUID pid)
   {
      if (pid == null)
         return;

      effectsData.resetEffects(pid);
   }

   /**
    * Serialize effects data to strings.
    *
    * @param pid the id of the player to serialize
    * @return a map of the effect type and duration serialized as strings
    */
   public Map<String, String> serializeEffects (UUID pid)
   {
      Map <String, String> serialized = new HashMap<>();

      if (pid != null)
      {
         Map<O2EffectType, Integer> savedEffects = effectsData.getPlayerSavedEffects(pid);
         for (Entry<O2EffectType, Integer> entry : savedEffects.entrySet())
         {
            serialized.put(effectLabelPrefix + entry.getKey().toString(), entry.getValue().toString());
         }
      }

      return serialized;
   }

   /**
    * Deserialize saved effect from strings.
    *
    * @param pid the id of the player this is saved for
    * @param effectsString the effect string
    * @param durationString the duration string
    */
   public void deserializeEffect (UUID pid, String effectsString, String durationString)
   {
      if (pid == null || effectsString == null || durationString == null)
         return;

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
   public synchronized void addEffect (O2Effect e)
   {
      if (e == null)
         return;

      UUID pid = e.getTargetID();

      Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

      if (playerEffects == null)
      {
         playerEffects = new HashMap<>();
      }

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

      if (Ollivanders2.debug)
         p.getLogger().info("Added effect " + e.effectType.toString() + " to " + p.getServer().getPlayer(pid).getDisplayName());
   }

   /**
    * Remove an effect from this player.
    *
    * @param pid the id of the player
    * @param effectType the effect to remove from this player
    */
   public synchronized void removeEffect (UUID pid, O2EffectType effectType)
   {
      if (pid == null || effectType == null)
         return;

      Map<O2EffectType, O2Effect> playerEffects = effectsData.getPlayerActiveEffects(pid);

      if(playerEffects == null)
         return;

      O2Effect effect = playerEffects.get(effectType);

      if (effect != null)
      {
         effect.kill();
         playerEffects.remove(effectType);
      }
      else
      {
         p.getLogger().warning("Effect to remove is null.");
      }

      effectsData.updatePlayerActiveEffects(pid, playerEffects);

      if (Ollivanders2.debug)
      {
         p.getLogger().info("Removed effect " + effectType.toString() + " from " + p.getServer().getPlayer(pid).getDisplayName());
      }
   }

   /**
    * Get an effect for a player.
    *
    * @param pid the id of the player
    * @param effectType the type of effect to get
    * @return the effect object if found, null otherwise
    */
   public synchronized O2Effect getEffect (UUID pid, O2EffectType effectType)
   {
      if (pid == null || effectType == null)
         return null;

      return effectsData.getActiveEffect(pid, effectType);
   }

   /**
    * Run game heartbeat upkeep for all effects for this player. This is not done for every player because
    * we only want to update the ones that are currently online, therefore we let the OllivandersScheduler
    * class tell us which ones to update.
    *
    * @param pid the id of the player.
    */
   public void upkeep (UUID pid)
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
    * @param pid the id of the player
    * @param amount the amount to age the effects by
    */
   public void ageAllEffects (UUID pid, int amount)
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
    * @param pid the id of the player
    * @param effectType the effect to age
    * @param amount the amount to age the effect
    */
   public void ageEffect (UUID pid, O2EffectType effectType, int amount)
   {
      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);

      if (activeEffects.containsKey(effectType))
      {
         O2Effect effect = activeEffects.get(effectType);
         effect.duration -= amount;
      }

      effectsData.updatePlayerActiveEffects(pid, activeEffects);
   }

   /**
    * Get the information for a detectable effect, if any can be detected. This is primarily used for the spell Informous.
    *
    * @param pid the id of the player to check
    * @return text about detectable effect or null if none found.
    */
   public String detectEffectWithInformous (UUID pid)
   {
      p.getLogger().info("detecting effcts with Informous");
      String infoText = null;

      Map<O2EffectType, O2Effect> activeEffects = effectsData.getPlayerActiveEffects(pid);
      Collection<O2Effect> effects = activeEffects.values();

      p.getLogger().info("found " + activeEffects.keySet().size() + " active effects");

      for (O2Effect effect : effects)
      {
         p.getLogger().info("checking effect " + effect.effectType.toString());
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
   public String detectEffectWithLegilimens (UUID pid)
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
