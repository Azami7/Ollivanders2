package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.Collection;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.O2Effects;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

/**
 * Reduces any spell effects on an item or player.
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class FINITE_INCANTATEM extends O2Spell
{
   /**
    * The number of spells that can be targeted by this spell.
    */
   private int maxTargets = 1;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public FINITE_INCANTATEM(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.FINITE_INCANTATEM;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("\"He pointed his wand at the rampart, cried, \"Finite!\" and it steadied.\"");
         add("\"Try Finite Incantatem, that should stop the rain if it’s a hex or curse.\"  -Hermione Granger");
         add("\"Stop! Stop!\" screamed Lockhart, but Snape took charge. \"Finite Incantatum!\" he shouted; Harry's feet stopped dancing, Malfoy stopped laughing, and they were able to look up.");
         add("The General Counter-Spell");
      }};

      text = "Reduces spell effects on an item or player. Is not effective against more powerful jinxes and enchantments.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FINITE_INCANTATEM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.FINITE_INCANTATEM;
      branch = O2MagicBranch.CHARMS;

      initSpell();
   }

   @Override
   void doInitSpell()
   {
      maxTargets = (int) usesModifier / 20;
      if (maxTargets < 1)
      {
         maxTargets = 1;
      }
   }

   /**
    * Check players and items in the projectiles path for enchantments and effects
    */
   @Override
   protected void doCheckEffect()
   {
      // look for entities first
      finiteIncantatemEntities();

      // look for items next
      if (!isKilled())
      {
         finiteIncantatemItems();
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
      {
         kill();
      }
   }

   /**
    * Finite Incantatem on entities.
    */
   private void finiteIncantatemEntities()
   {
      int effectsTargeted = 0;

      for (LivingEntity live : getLivingEntities(1.5))
      {
         if (live.getUniqueId() == player.getUniqueId())
         {
            continue;
         }

         common.printDebugMessage("finite incantatem targeting " + live.getName(), null, null, false);

         // if they are a player, look for O2Effects
         if (live instanceof Player)
         {
            Player target = (Player)live;

            // look for any effects on the player
            if (Ollivanders2API.getPlayers().playerEffects.hasEffects(target.getUniqueId()))
            {
               for (O2EffectType effectType : Ollivanders2API.getPlayers().playerEffects.getEffects(target.getUniqueId()))
               {
                  if (effectType.getLevel().ordinal() <= spellType.getLevel().ordinal())
                     Ollivanders2API.getPlayers().playerEffects.removeEffect(target.getUniqueId(), effectType);

                  effectsTargeted = effectsTargeted + 1;
                  if (effectsTargeted >= maxTargets)
                     break;
               }
            }
         }

         if (effectsTargeted < maxTargets)
         {
            // look for any minecraft PotionEffects
            Collection<PotionEffect> potionEffects = live.getActivePotionEffects();

            for (PotionEffect effect : potionEffects)
            {
               Ollivanders2Common.MagicLevel level;
               if (O2Effects.potionEffectLevels.containsKey(effect.getType()))
                  level = O2Effects.potionEffectLevels.get(effect.getType());
               else
                  level = Ollivanders2Common.MagicLevel.OWL;

               if (level.ordinal() <= spellType.getLevel().ordinal())
                  player.removePotionEffect(effect.getType());

               effectsTargeted = effectsTargeted + 1;
               if (effectsTargeted >= maxTargets)
                  break;
            }
         }

         kill();
         return;
      }
   }

   /**
    * Finite Incantatem on items.
    */
   private void finiteIncantatemItems()
   {
      for (Item item : getItems(1.5))
      {
         if (Ollivanders2API.getItems().enchantedItems.isEnchanted(item))
         {
            ItemEnchantmentType enchantmentType = Ollivanders2API.getItems().enchantedItems.getEnchantmentType(item.getItemStack());

            if (enchantmentType.getLevel().ordinal() <= spellType.getLevel().ordinal())
               Ollivanders2API.getItems().enchantedItems.removeEnchantment(item);
         }

         kill();
         return;
      }
   }
}