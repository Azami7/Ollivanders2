package net.pottercraft.Ollivanders2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;

import net.pottercraft.Ollivanders2.Effect.O2Effect;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Spell.SpellProjectile;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.pottercraft.Ollivanders2.Effect.FLYING;
import net.pottercraft.Ollivanders2.Spell.Spell;
import net.pottercraft.Ollivanders2.StationarySpell.REPELLO_MUGGLETON;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;

/**
 * Scheduler for Ollivanders2
 *
 * @author lownes
 */
class OllivandersSchedule implements Runnable
{
   Ollivanders2 p;
   int counter = 0;
   static Set<UUID> flying = new HashSet<>();
   Set<UUID> onBroom = new HashSet<>();

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
         p.stationarySpells.upkeep();
         broomSched();
      }
      catch (Exception e)
      {
         if (Ollivanders2.debug)
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
      List<SpellProjectile> projectiles2 = new ArrayList<>(projectiles);
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
         UUID pid = player.getUniqueId();

         p.players.playerEffects.upkeep(pid);
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
      Set<REPELLO_MUGGLETON> repelloMuggletons = new HashSet<>();
      for (StationarySpellObj stat : p.stationarySpells.getActiveStationarySpells())
      {
         if (stat instanceof REPELLO_MUGGLETON)
         {
            repelloMuggletons.add((REPELLO_MUGGLETON) stat);
         }
      }

      for (World world : p.getServer().getWorlds())
      {
         for (Player player : world.getPlayers())
         {
            O2Player o2p = p.getO2Player(player);

            boolean alreadyInvis = o2p.isInvisible();
            boolean alreadyInRepelloMuggleton = o2p.isInRepelloMuggleton();

            boolean hasCloak = hasCloak(player);
            if (hasCloak) {
               if (!alreadyInvis) {
                  o2p.setInvisible(true);
                  player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
               }
            }

            boolean inRepelloMuggletons = false;
            for (StationarySpellObj stat : repelloMuggletons) {
               if (stat.isInside(player.getLocation())) {
                  inRepelloMuggletons = true;
                  if (!alreadyInRepelloMuggleton) {
                     o2p.setInRepelloMuggleton(true);
                  }
                  break;
               }
            }

            if (hasCloak || inRepelloMuggletons) {
               for (Player player2 : world.getPlayers()) {
                  if (player2.isPermissionSet("Ollivanders2.BYPASS") && player2.hasPermission("Ollivanders2.BYPASS")) {
                     continue;
                  }

                  O2Player viewer = p.getO2Player(player2);

                  if (hasCloak) {
                     player2.hidePlayer(p, player);
                     System.out.println(player2.canSee(player));
                  } else if (viewer.isMuggle()) {
                     player2.hidePlayer(p, player);
                  }
               }
            } else if (!hasCloak && alreadyInvis) {
               for (Player player2 : world.getPlayers())
               {
                  O2Player viewer = p.getO2Player(player2);
                  if (!inRepelloMuggletons && viewer.isMuggle()) {
                     player2.showPlayer(p, player);
                  }
               }
               o2p.setInvisible(false);
               player.removePotionEffect(PotionEffectType.INVISIBILITY);
            } else if (!inRepelloMuggletons && alreadyInRepelloMuggleton) {
               if (!hasCloak) {
                  for (Player player2 : world.getPlayers()) {
                     player2.showPlayer(p, player);
                  }
               }
               o2p.setInRepelloMuggleton(false);
            }

            if (o2p.isInvisible()) {
               for (Entity entity : player.getWorld().getEntities()) {
                  if (entity instanceof Creature) {
                     Creature creature = (Creature) entity;
                     if (creature.getTarget() == player) {
                        creature.setTarget(null);
                     }
                  }
               }
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
         return p.common.isInvisibilityCloak(chestPlate);
      }
      return false;
   }

   /**
    * Checks all players to see if they will receive a prophecy
    */
   @Deprecated
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
            double experience = p.getO2Player(player).getSpellCount(O2SpellType.INFORMOUS);
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
   @Deprecated
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
            if (p.common.isBroom(player.getInventory().getItemInMainHand()) && p.canLive(player.getLocation(), O2SpellType.VOLATUS))
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
                  if (p.players.playerEffects.hasEffect(player.getUniqueId(), O2EffectType.FLYING))
                  {
                     continue playerIter;
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