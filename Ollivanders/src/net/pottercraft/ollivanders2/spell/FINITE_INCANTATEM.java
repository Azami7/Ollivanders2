package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffect;

/**
 * Reduces any spell effects on an item or player.
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class FINITE_INCANTATEM extends O2Spell
{
   private final double minPercent = 0.1;
   private double percent;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FINITE_INCANTATEM()
   {
      super();

      spellType = O2SpellType.FINITE_INCANTATEM;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("\"He pointed his wand at the rampart, cried, \"Finite!\" and it steadied.\"");
         add("\"Try Finite Incantatem, that should stop the rain if it’s a hex or curse.\"  -Hermione Granger");
         add("\"Stop! Stop!\" screamed Lockhart, but Snape took charge. \"Finite Incantatum!\" he shouted; Harry's feet stopped dancing, Malfoy stopped laughing, and they were able to look up.");
         add("The General Counter-Spell");
      }};

      text = "Reduces all spell effects on an item or player.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FINITE_INCANTATEM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.FINITE_INCANTATEM;
      branch = O2MagicBranch.CHARMS;

      initSpell();
   }

   @Override
   void doInitSpell()
   {
      percent = (usesModifier / 2) / 100;
      if (percent < minPercent)
      {
         percent = minPercent;
      } else if (percent > 1)
      {
         percent = 1;
      }
   }

   /**
    *
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
   private void finiteIncantatemEntities ()
   {
      for (LivingEntity live : getLivingEntities(1.5))
      {
         if (live.getUniqueId() == player.getUniqueId())
         {
            continue;
         }

         if (Ollivanders2.debug)
         {
            p.getLogger().info("finite incantatem targeting " + live.getName());
         }

         if (live instanceof Player)
         {
            Player ply = (Player) live;

            // look for any effects on the player
            if (Ollivanders2API.getPlayers(p).playerEffects.hasEffects(ply.getUniqueId()))
            {
               if (usesModifier < 100)
               {
                  Ollivanders2API.getPlayers(p).playerEffects.ageAllEffectsByPercent(ply.getUniqueId(), (int) (percent * 100));
               }

               kill();
               return;
            }
         }

         Collection<PotionEffect> potionEffects = live.getActivePotionEffects();

         for (PotionEffect effect : potionEffects)
         {
            int curDuration = effect.getDuration();
            double difference = curDuration * percent;
            int newDuration = curDuration - (int) difference;

            PotionEffect newEffect = new PotionEffect(effect.getType(), newDuration, effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon());

            live.removePotionEffect(effect.getType());

            if (percent < 100)
            {
               live.addPotionEffect(newEffect);
            }

            kill();
         }

         return;
      }
   }

   /**
    * Finite Incantatem on items.
    */
   private void finiteIncantatemItems ()
   {
      if (hasHitTarget())
      {
         kill();
         return;
      }

      for (Item item : getItems(1.5))
      {
         ItemStack stack = item.getItemStack();
         ItemMeta meta = stack.getItemMeta();

         if (meta == null)
            continue;

         if (Ollivanders2.debug)
         {
            p.getLogger().info("finite incantatem targeting " + item.getName());
         }

         if (meta.hasLore())
         {
            List<String> lore = meta.getLore();
            if (lore == null)
               continue;

            List<String> newLore = updateLore(lore);
            if (newLore == null)
               continue;

            meta.setLore(newLore);
            stack.setItemMeta(meta);
            item.setItemStack(stack);

            stopProjectile();
            return;
         }
      }
   }

   private List<String> updateLore(List<String> lore)
   {
      if (lore == null)
         return null;

      ArrayList<String> newLore = new ArrayList<>();

      for (String loreLine : lore)
      {
         String[] loreParts = loreLine.split(" ");
         String curseName = loreParts[0];

         if (Ollivanders2.debug)
         {
            p.getLogger().info("item meta line starts with " + curseName);
         }

         if (curseName.equals(GEMINIO.geminio))
         {
            if (Ollivanders2.debug)
            {
               p.getLogger().info("item has " + curseName + " curse");
            }

            int curMagnitude = 0;

            try
            {
               curMagnitude = Integer.parseInt(loreParts[1]);
            }
            catch (Exception e)
            {
               continue;
            }

            if (curMagnitude > 0)
            {
               double difference = curMagnitude * percent;
               int newMagnitude = curMagnitude - (int) difference;

               if (Ollivanders2.debug)
               {
                  p.getLogger().info("reducing magnitude by " + percent + "%, " + difference + ", from " + curMagnitude + " to " + newMagnitude);
               }

               if (percent < 100)
               {
                  newLore.add(loreParts[0] + " " + newMagnitude);
               }
            }
         } else if (curseName.equals(FLAGRANTE.flagrante))
         {
            // special case for flagrante, do nothing which will just remove the effect entirely
         } else
         {
            newLore.add(loreLine);
         }
      }

      return newLore;
   }
}