package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffect;

/**
 * Reduces any spell effects on an item or player.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class FINITE_INCANTATEM extends Charms
{
   private final int minPercent = 10;
   private int percent;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FINITE_INCANTATEM ()
   {
      super();

      spellType = O2SpellType.FINITE_INCANTATEM;

      flavorText = new ArrayList<String>() {{
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

      initSpell();
   }

   /**
    *
    */
   @Override
   protected void doCheckEffect ()
   {
      percent = (int) usesModifier / 2;
      if (percent < minPercent)
      {
         percent = minPercent;
      }
      else if (percent > 100)
      {
         percent = 100;
      }

      if (Ollivanders2.debug)
      {
         p.getLogger().info("Finite incantatem percent = " + percent);
      }

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

            if (Ollivanders2API.getPlayers().playerEffects.hasEffects(ply.getUniqueId()))
            {
               if (usesModifier < 100)
               {
                  Ollivanders2API.getPlayers().playerEffects.ageAllEffectsByPercent(ply.getUniqueId(), percent);
               }

               kill();
               return;
            }
         }

         Collection<PotionEffect> potionEffects = live.getActivePotionEffects();

         for (PotionEffect effect : potionEffects)
         {
            int curDuration = effect.getDuration();
            int newDuration = curDuration - (curDuration * (percent / 100));

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
      for (Item item : getItems(1.5))
      {
         ItemStack stack = item.getItemStack();
         ItemMeta meta = stack.getItemMeta();

         if (Ollivanders2.debug)
         {
            p.getLogger().info("finite incantatem targeting " + item.getName());
         }

         if (meta.hasLore())
         {
            List<String> lore = meta.getLore();
            ArrayList<String> newLore = new ArrayList<>();

            for (int i = 0; i < lore.size(); i++)
            {
               String[] loreParts = lore.get(i).split(" ");

               if (Ollivanders2.debug)
               {
                  p.getLogger().info("item meta line starts with " + loreParts[0]);
               }

               if (ItemCurse.itemCurseNames.contains(loreParts[0]))
               {
                  if (Ollivanders2.debug)
                  {
                     p.getLogger().info("item has " + lore.get(i) + " curse");
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
                     int newMagnitude = curMagnitude - (curMagnitude * (percent / 100));

                     if (Ollivanders2.debug)
                     {
                        p.getLogger().info("reducing magnitude from  " + curMagnitude + " to " + newMagnitude);
                     }

                     if (percent < 100)
                     {
                        newLore.add(loreParts[0] + " " + newMagnitude);
                     }
                  }

                  kill();
               }
               else // this is not a curse line, add it back to the new lore set
               {
                  newLore.add(lore.get(i));
               }
            }

            meta.setLore(newLore);
            stack.setItemMeta(meta);
            item.setItemStack(stack);

            if (isKilled())
            {
               return;
            }
         }
      }
   }
}