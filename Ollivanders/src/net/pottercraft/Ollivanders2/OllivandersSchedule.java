package net.pottercraft.Ollivanders2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;

import net.pottercraft.Ollivanders2.Spell.SpellProjectile;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import net.pottercraft.Ollivanders2.Effect.Effect;
import net.pottercraft.Ollivanders2.Effect.VENTO_FOLIO;
import net.pottercraft.Ollivanders2.Spell.Spell;
import net.pottercraft.Ollivanders2.StationarySpell.REPELLO_MUGGLETON;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpell;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Scheduler for Ollivanders2
 *
 * @author lownes
 */
class OllivandersSchedule implements Runnable
{
   Ollivanders2 p;
   int counter = 0;
   static Set<UUID> flying = new HashSet<UUID>();
   Set<UUID> onBroom = new HashSet<UUID>();

   public OllivandersSchedule (Ollivanders2 plugin)
   {
      p = plugin;
   }

   public void run ()
   {
      try
      {
         projectileSched();
         oeffectSched();
         stationarySched();
         broomSched();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      if (counter % 20 == 0)
      {
         itemCurseSched();
      }
      if (counter % 20 == 1)
      {
         invisPlayer();
      }
      if (counter % 20 == 2 && p.getConfig().getBoolean("divination"))
      {
         scry();
         effectProphecy();
      }
      counter = (counter + 1) % 20;
   }

   /**
    * Scheduling method that calls checkEffect() on all SpellProjectile objects
    * and removes those that have kill set to true.
    */
   private void projectileSched ()
   {
      List<SpellProjectile> projectiles = p.getProjectiles();
      List<SpellProjectile> projectiles2 = new ArrayList<SpellProjectile>(projectiles);
      if (projectiles2.size() > 0)
      {
         for (SpellProjectile proj : projectiles2)
         {
            ((Spell) proj).checkEffect();
            if (proj.kill)
            {
               p.remProjectile(proj);
            }
         }
      }
   }

   /**
    * Scheduling method that calls checkEffect on all OEffect objects associated with every online player
    * and removes those that have kill set to true.
    */
   private void oeffectSched ()
   {
      List<Player> onlinePlayers = new ArrayList<>();

      for (World world : p.getServer().getWorlds())
      {
         onlinePlayers.addAll(world.getPlayers());
      }

      for (Player player : onlinePlayers)
      {
         O2Player o2p = p.getO2Player(player);
         UUID pid = player.getUniqueId();

         List<OEffect> playerEffects = o2p.getEffects();
         if (playerEffects == null)
         {
            continue;
         }

         for (OEffect effect : playerEffects)
         {
            ((Effect) effect).checkEffect(p, Bukkit.getPlayer(pid));
            if (effect.kill)
            {
               o2p.removeEffect(effect);
            }
         }

         p.setO2Player(player, o2p);
      }
   }

   /**
    * Scheduling method that calls checkEffect on all StationarySpellObj objects associated with every player
    * and removes those that have kill set to true.
    */
   private void stationarySched ()
   {
      List<StationarySpellObj> stationary = p.getStationary();
      List<StationarySpellObj> stationary2 = new ArrayList<>(stationary);
      if (stationary2.size() > 0)
      {
         for (StationarySpellObj stat : stationary2)
         {
            if (stat.active)
            {
               ((StationarySpell) stat).checkEffect(p);
            }
            if (stat.kill)
            {
               p.remStationary(stat);
            }
         }
      }
   }

   /**
    * Scheduling method that checks for any geminio or
    * flagrante curses on items in player inventories and
    * performs their effect.
    */
   private void itemCurseSched ()
   {
      for (World world : p.getServer().getWorlds())
      {
         for (Player player : world.getPlayers())
         {
            List<ItemStack> geminioIS = new ArrayList<>();
            ListIterator<ItemStack> invIt = player.getInventory().iterator();
            while (invIt.hasNext())
            {
               ItemStack item = invIt.next();
               if (item != null)
               {
                  ItemMeta meta = item.getItemMeta();
                  if (meta.hasLore())
                  {
                     List<String> lored = meta.getLore();
                     for (String lore : lored)
                     {
                        if (lore.contains("Geminio "))
                        {
                           geminioIS.add(geminio(item.clone()));
                           invIt.set(null);
                        }
                        if (lore.contains("Flagrante "))
                        {
                           flagrante(player, item);
                        }
                     }
                  }
               }
            }
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(geminioIS.toArray(new ItemStack[geminioIS.size()]));
            for (ItemStack item : leftover.values())
            {
               player.getWorld().dropItem(player.getLocation(), item);
            }
         }
      }
   }

   /**
    * Enacts the geminio duplicating effect on an itemstack
    *
    * @param item - item with geminio curse on it
    * @return Duplicated itemstacks
    */
   private ItemStack geminio (ItemStack item)
   {
      int stackSize = item.getAmount();
      ItemMeta meta = item.getItemMeta();
      List<String> lore = meta.getLore();
      ArrayList<String> newLore = new ArrayList<>();
      for (int i = 0; i < lore.size(); i++)
      {
         if (lore.get(i).contains("Geminio "))
         {
            String[] loreParts = lore.get(i).split(" ");
            int magnitude = Integer.parseInt(loreParts[1]);
            if (magnitude > 1)
            {
               magnitude--;
               newLore.add("Geminio " + magnitude);
            }
            stackSize = stackSize * 2;
         }
         else
         {
            newLore.add(lore.get(i));
         }
      }
      meta.setLore(newLore);
      item.setItemMeta(meta);
      item.setAmount(stackSize);
      return item;
   }

   /**
    * Enacts the flagrante burning effect on the player
    */
   private void flagrante (Player player, ItemStack item)
   {
      ItemMeta meta = item.getItemMeta();
      List<String> lore = meta.getLore();
      int magnitude = 0;
      for (int i = 0; i < lore.size(); i++)
      {
         if (lore.get(i).contains("Flagrante "))
         {
            String[] loreParts = lore.get(i).split(" ");
            magnitude = Integer.parseInt(loreParts[1]);
         }
      }
      player.damage(magnitude * 0.05 * item.getAmount());
      player.setFireTicks(160);
   }

   /**
    * Hides a player with the Cloak of Invisibility from other players.
    * Also hides players in Repello Muggleton from players not in that same spell.
    * Also sets any Creature targeting this player to have null target.
    */
   private void invisPlayer ()
   {
      for (World world : p.getServer().getWorlds())
      {
         for (Player player : world.getPlayers())
         {
            O2Player o2p = p.getO2Player(player);

            Set<REPELLO_MUGGLETON> muggletons = new HashSet<>();
            for (StationarySpellObj stat : p.getStationary())
            {
               if (stat instanceof REPELLO_MUGGLETON)
               {
                  if (stat.isInside(player.getLocation()) && stat.active)
                  {
                     muggletons.add((REPELLO_MUGGLETON) stat);
                  }
               }
            }

            boolean hasCloak = hasCloak(player);
            if (hasCloak || muggletons.size() > 0)
            {
               o2p.setMuggleton(true);

               for (Player viewer : world.getPlayers())
               {
                  if (viewer.isPermissionSet("Ollivanders2.BYPASS"))
                  {
                     if (viewer.hasPermission("Ollivanders2.BYPASS"))
                     {
                        continue;
                     }
                  }
                  if (muggletons.size() == 0)
                  {
                     viewer.hidePlayer(p, player);

                  }
                  else
                  {
                     for (REPELLO_MUGGLETON muggleton : muggletons)
                     {
                        if (hasCloak || (!muggleton.isInside(viewer.getLocation()) && muggleton.active))
                        {
                           viewer.hidePlayer(p, player);
                           break;
                        }
                        else
                        {
                           viewer.showPlayer(p, player);
                        }
                     }
                  }
               }

               if (!o2p.isInvisible())
               {
                  for (Entity entity : player.getWorld().getEntities())
                  {
                     if (entity instanceof Creature)
                     {
                        Creature creature = (Creature) entity;
                        if (creature.getTarget() == player)
                        {
                           if (muggletons.size() == 0)
                           {
                              creature.setTarget(null);
                           }
                           else
                           {
                              for (REPELLO_MUGGLETON muggleton : muggletons)
                              {
                                 if (hasCloak || (!muggleton.isInside(creature.getLocation()) && muggleton.active))
                                 {
                                    creature.setTarget(null);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
               o2p.setInvisible(hasCloak);
            }
            else if (o2p.isInvisible() || o2p.isMuggleton())
            {
               for (Player viewer : world.getPlayers())
               {
                  viewer.showPlayer(p, player);
               }
               o2p.setInvisible(false);
               o2p.setMuggleton(false);
            }

            p.setO2Player(player, o2p);
         }
      }
   }

   /**
    * Does the player have the Cloak of Invisibility
    *
    * @param player - Player to be checked
    * @return - True if yes, false if no
    */
   private boolean hasCloak (Player player)
   {
      ItemStack chestPlate = player.getInventory().getChestplate();
      if (chestPlate != null)
      {
         if (chestPlate.getType() == Material.CHAINMAIL_CHESTPLATE)
         {
            if (chestPlate.getItemMeta().hasLore())
            {
               List<String> lore = chestPlate.getItemMeta().getLore();
               if (lore.get(0).equals("Silvery Transparent Cloak"))
               {
                  return true;
               }
            }
         }
      }
      return false;
   }

   /**
    * Checks all players to see if they will receive a prophecy
    */
   private void scry ()
   {
      Material ball = Material.getMaterial("divinationBlock");

      for (World world : p.getServer().getWorlds())
      {
         for (Player player : world.getPlayers())
         {
            if (player.getTargetBlock((java.util.Set) null, 100).getType() != ball || !player.isSneaking())
            {
               return;
            }
            double experience = p.getO2Player(player).getSpellCount(Spells.INFORMOUS);
            if (Math.random() < experience / 1000.0)
            {
               //The scrying is successful
               Prophecy prophecy = new Prophecy(player);
               p.getProphecy().add(prophecy);
               String message = "";
               List<String> lore = prophecy.toLore();
               for (String str : lore)
               {
                  message = message.concat(str + " ");
               }
               player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + message);
               ItemStack hand = player.getInventory().getItemInMainHand();
               if (hand.getType() == ball)
               {
                  ItemStack record = new ItemStack(ball, 1);
                  ItemMeta recordM = record.getItemMeta();
                  recordM.setDisplayName("Prophecy Record");
                  recordM.setLore(lore);
                  record.setItemMeta(recordM);
                  if (hand.getAmount() == 1)
                  {
                     player.getInventory().setItemInMainHand(null);
                  }
                  else
                  {
                     hand.setAmount(hand.getAmount() - 1);
                     player.getInventory().setItemInMainHand(hand);
                  }
                  for (ItemStack drop : player.getInventory().addItem(record).values())
                  {
                     player.getWorld().dropItem(player.getLocation(), drop);
                  }
               }
            }
         }
      }
   }

   /**
    * Goes through all prophecies and enacts them if they are due
    * and deletes them if they are past
    */
   private void effectProphecy ()
   {
      Iterator<Prophecy> iter = p.getProphecy().iterator();
      while (iter.hasNext())
      {
         Prophecy prop = iter.next();
         if (prop.isActive())
         {
            Player player = p.getServer().getPlayer(prop.getPlayerUUID());
            if (player != null)
            {
               player.addPotionEffect(prop.toPotionEffect(), true);
            }
         }
         else if (prop.isFinished())
         {
            iter.remove();
         }
      }
   }

   /**
    * Goes through all players and sets any holding a broom to flying
    */
   private void broomSched ()
   {
      playerIter:
      for (World world : p.getServer().getWorlds())
      {
         for (Player player : world.getPlayers())
         {
            if (p.isBroom(player.getInventory().getItemInMainHand()) && p.canLive(player.getLocation(), Spells.VOLATUS))
            {
               player.setAllowFlight(true);
               player.setFlying(true);
               this.onBroom.add(player.getUniqueId());
               if (flying.contains(player.getUniqueId()))
               {
                  Vector broomVec = player.getLocation().getDirection().clone();
                  broomVec.multiply(Math.sqrt(player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.PROTECTION_FALL)) / 40.0);
                  player.setVelocity(player.getVelocity().add(broomVec));
               }
            }
            else
            {
               if (player.getGameMode() == GameMode.SURVIVAL && (this.onBroom.contains(player.getUniqueId())))
               {
                  for (OEffect effect : p.getO2Player(player).getEffects())
                  {
                     if (effect instanceof VENTO_FOLIO)
                     {
                        continue playerIter;
                     }
                  }
                  //player.setAllowFlight(false);
                  player.setFlying(false);
                  this.onBroom.remove(player.getUniqueId());
               }
            }
         }
      }
   }

   public static Set<UUID> getFlying()
   {
      return flying;
   }
}