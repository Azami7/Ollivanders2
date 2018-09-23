package net.pottercraft.Ollivanders2;

import net.pottercraft.Ollivanders2.Book.O2Books;
import net.pottercraft.Ollivanders2.Effect.O2Effect;
import net.pottercraft.Ollivanders2.Effect.MUTED_SPEECH;
import net.pottercraft.Ollivanders2.Effect.BABBLING;
import net.pottercraft.Ollivanders2.Effect.LYCANTHROPY;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Spell.O2Spell;
import net.pottercraft.Ollivanders2.Spell.Transfiguration;
import net.pottercraft.Ollivanders2.Spell.AMATO_ANIMO_ANIMATO_ANIMAGUS;
import net.pottercraft.Ollivanders2.Spell.MORTUOS_SUSCITATE;
import net.pottercraft.Ollivanders2.Spell.PORTUS;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Potion.O2Potion;
import net.pottercraft.Ollivanders2.Potion.O2SplashPotion;
import net.pottercraft.Ollivanders2.Potion.O2Potions;
import net.pottercraft.Ollivanders2.StationarySpell.ALIQUAM_FLOO;
import net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS;
import net.pottercraft.Ollivanders2.StationarySpell.NULLUM_APPAREBIT;
import net.pottercraft.Ollivanders2.StationarySpell.NULLUM_EVANESCUNT;
import net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_TOTALUM;
import net.pottercraft.Ollivanders2.StationarySpell.REPELLO_MUGGLETON;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;
import net.pottercraft.Ollivanders2.StationarySpell.MOLLIARE;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.Effect;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Wolf;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.potion.PotionEffect;

import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Listener for events from the plugin
 *
 * @author lownes
 * @author Azami7
 */
public class OllivandersListener implements Listener
{

   private Ollivanders2 p;

   OllivandersListener (Ollivanders2 plugin)
   {
      p = plugin;
   }

   /**
    * Fires on player move
    *
    * @param event the player move event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onPlayerMove (PlayerMoveEvent event)
   {
      Player player = event.getPlayer();

      if (p.players.playerEffects.hasEffect(player.getUniqueId(), O2EffectType.SLEEPING))
      {
         // do not allow the player to move if they are asleep
         Location fromLoc = event.getFrom();
         event.setTo(fromLoc);

         player.sendMessage(Ollivanders2.chatColor + "You are in a deep sleep and cannot move.");
      }
      else
      {
         protegoTotalum(event);
      }
   }

   /**
    * Doesn't let players cross a protego totalum
    *
    * @param event the player move event
    */
   private void protegoTotalum (PlayerMoveEvent event)
   {
      if (event.getPlayer().isPermissionSet("Ollivanders2.BYPASS"))
      {
         if (event.getPlayer().hasPermission("Ollivanders2.BYPASS"))
         {
            return;
         }
      }
      Location toLoc = event.getTo();
      Location fromLoc = event.getFrom();
      for (StationarySpellObj spell : p.stationarySpells.getActiveStationarySpells())
      {
         if (spell instanceof PROTEGO_TOTALUM &&
               toLoc.getWorld().getUID().equals(spell.location.getWorld().getUID()) &&
               fromLoc.getWorld().getUID().equals(spell.location.getWorld().getUID()))
         {
            int radius = spell.radius;
            Location spellLoc = spell.location;
            if (((fromLoc.distance(spellLoc) < radius - 0.5 && toLoc.distance(spellLoc) > radius - 0.5)
                  || (toLoc.distance(spellLoc) < radius + 0.5 && fromLoc.distance(spellLoc) > radius + 0.5)))
            {
               event.setCancelled(true);
               spell.flair(10);
            }
         }
      }
   }

   /**
    * Checks if a player is inside an active floo fireplace and is saying a destination
    *
    * @param event the player chat event
    */
   @EventHandler(priority = EventPriority.LOW)
   public void onFlooChat (AsyncPlayerChatEvent event)
   {
      Player player = event.getPlayer();
      String chat = event.getMessage();
      for (StationarySpellObj stat : p.stationarySpells.getActiveStationarySpells())
      {
         if (stat instanceof ALIQUAM_FLOO)
         {
            ALIQUAM_FLOO aliquam = (ALIQUAM_FLOO) stat;
            if (player.getLocation().getBlock().equals(aliquam.getBlock()) && aliquam.isWorking())
            {
               //Floo network
               if (player.isPermissionSet("Ollivanders2.Floo"))
               {
                  if (!player.hasPermission("Ollivanders2.Floo"))
                  {
                     player.sendMessage(Ollivanders2.chatColor + "You do not have permission to use the Floo Network.");
                     return;
                  }
               }
               aliquam.stopWorking();
               List<ALIQUAM_FLOO> alis = new ArrayList<>();
               Location destination;
               for (StationarySpellObj ali : p.stationarySpells.getActiveStationarySpells())
               {
                  if (ali instanceof ALIQUAM_FLOO)
                  {
                     ALIQUAM_FLOO dest = (ALIQUAM_FLOO) ali;
                     alis.add(dest);
                     if (dest.getFlooName().equals(chat.trim().toLowerCase()))
                     {
                        destination = dest.location;
                        destination.setPitch(player.getLocation().getPitch());
                        destination.setYaw(player.getLocation().getYaw());
                        player.teleport(destination);
                        return;
                     }
                  }
               }
               int randomIndex = (int) (alis.size() * Math.random());
               destination = alis.get(randomIndex).location;
               destination.setPitch(player.getLocation().getPitch());
               destination.setYaw(player.getLocation().getYaw());
               player.teleport(destination);
               return;
            }
         }
      }
   }

   /**
    * Handles all actions related to players speaking.
    *
    * @param event the player chat event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onPlayerChat (AsyncPlayerChatEvent event)
   {
      Player sender = event.getPlayer();
      String message = event.getMessage();

      if (Ollivanders2.debug)
      {
         p.getLogger().info("onPlayerChat: message = " + message);
      }

      /**
       * Handle player spells that effect the chat.  Need to do this first since they may affect the chat
       * message itself, which would change later chat effects.
       */
      if (Ollivanders2.debug)
      {
         p.getLogger().info("onPlayerChat: Handling player effects");
      }

      O2Effect effect = null;
      // muted speech has highest precedence
      if (p.players.playerEffects.hasEffect(sender.getUniqueId(), O2EffectType.MUTED_SPEECH))
      {
         effect = p.players.playerEffects.getEffect(sender.getUniqueId(), O2EffectType.MUTED_SPEECH);

         if (effect != null)
            ((MUTED_SPEECH)effect).doSilencio(event);
      }
      else // speech replacement effects
      {
         if (p.players.playerEffects.hasEffect(sender.getUniqueId(), O2EffectType.SLEEP_SPEECH))
         {
            effect = p.players.playerEffects.getEffect(sender.getUniqueId(), O2EffectType.SLEEP_SPEECH);
         }
         else if (p.players.playerEffects.hasEffect(sender.getUniqueId(), O2EffectType.LYCANTHROPY_SPEECH))
         {
            effect = p.players.playerEffects.getEffect(sender.getUniqueId(), O2EffectType.LYCANTHROPY_SPEECH);
         }
         else if (p.players.playerEffects.hasEffect(sender.getUniqueId(), O2EffectType.BABBLING))
         {
            effect = p.players.playerEffects.getEffect(sender.getUniqueId(), O2EffectType.BABBLING);
         }

         if (effect != null)
            ((BABBLING)effect).doBabblingEffect(event);
      }

      //
      // Parse to see if they were casting a spell
      //
      O2SpellType spellType = p.spells.getSpellTypeByName(message);
      if (Ollivanders2.debug)
      {
         if (spellType != null)
         {
            p.getLogger().info("Spell is " + spellType);
         }
         else
         {
            p.getLogger().info("No spell found");
         }
      }

      //
      // Handle stationary spells that affect chat
      //
      Set<Player> recipients = event.getRecipients();
      List<StationarySpellObj> stationaries = p.stationarySpells.getStationarySpellsAtLocation(sender.getLocation());
      Set<StationarySpellObj> muffliatos = new HashSet<>();
      for (StationarySpellObj stationary : stationaries)
      {
         if (Ollivanders2.debug)
         {
            p.getLogger().info("onPlayerChat: handling stationary spells");
         }

         if (stationary.name.equals(StationarySpells.MUFFLIATO) && stationary.active)
         {
            muffliatos.add(stationary);
         }
      }

      //
      // Handle removing recipients from chat
      //
      Set<Player> remRecipients = new HashSet<>();

      // If player cast a spell, only show that chat to players within range
      if (spellType != null)
      {
         for (Player recipient : recipients)
         {
            Location location = sender.getLocation();
            if (!p.common.isInside(location, recipient.getLocation(), 15))
            {
               remRecipients.add(recipient);
            }
         }
      }

      // If sender is in a MUFFLIATO, remove recepients not also in the MUFFLIATO radius
      if (muffliatos.size() > 0)
      {
         if (Ollivanders2.debug)
         {
            p.getLogger().info("onPlayerChat: MUFFLIATO detected");
         }

         for (Player recipient : recipients)
         {
            for (StationarySpellObj muffliato : muffliatos)
            {
               Location recLoc = recipient.getLocation();
               if (!muffliato.isInside(recLoc) && !remRecipients.contains(recipient))
               {
                  remRecipients.add(recipient);
               }
            }
         }
      }

      for (Player remRec : remRecipients)
      {
         if (Ollivanders2.debug)
         {
            p.getLogger().info("onPlayerChat: update recipients");
         }

         try
         {
            if (remRec.isPermissionSet("Ollivanders2.BYPASS"))
            {
               if (!remRec.hasPermission("Ollivanders2.BYPASS"))
               {
                  recipients.remove(remRec);
               }
            }
            else
            {
               recipients.remove(remRec);
            }
         }
         catch (UnsupportedOperationException e)
         {
            p.getLogger().warning("Chat was unable to be removed due "
                  + "to a unmodifiable set.");
         }
      }

      //
      // Handle spell casting
      //
      // If the spell is valid AND player is allowed to cast spells per server permissions
      if (spellType != null && p.canCast(sender, spellType, true))
      {
         if (p.canCast(sender, spellType, true))
         {
            if (Ollivanders2.useBookLearning && p.getO2Player(sender).getSpellCount(spellType) < 1)
            {
               // if bookLearning is set to true then spell count must be > 0 to cast this spell
               if (Ollivanders2.debug)
               {
                  p.getLogger().info("onPlayerChat: bookLearning enforced");
               }
               sender.sendMessage(Ollivanders2.chatColor + "You do not know that spell yet. To learn a spell, you'll need to read a book about that spell.");

               return;
            }

            boolean castSuccess = true;

            if (!p.playerCommon.holdsWand(sender))
            {
               // if they are not holding their destined wand, casting success is reduced
               if (Ollivanders2.debug)
               {
                  p.getLogger().info("onPlayerChat: player not holding destined wand");
               }

               int uses = p.getO2Player(sender).getSpellCount(spellType);
               castSuccess = Math.random() < (1.0 - (100.0 / (uses + 101.0)));
            }

            // wandless spells
            if (spellType == O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS)
            {
               if (Ollivanders2.debug)
               {
                  p.getLogger().info("onPlayerChat: allow wandless casting of " + spellType);
               }
               castSuccess = true;
            }

            if (castSuccess)
            {
               if (Ollivanders2.debug)
               {
                  p.getLogger().info("onPlayerChat: begin casting " + spellType);
               }

               String[] words = message.split(" ");

               if (spellType == O2SpellType.APPARATE)
               {
                  apparate(sender, words);
                  event.setMessage("apparate");
               }
               else if (spellType == O2SpellType.PORTUS)
               {
                  p.addProjectile(new PORTUS(p, sender, 1.0, words));
               }
               else if (spellType == O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS)
               {
                  p.addProjectile(new AMATO_ANIMO_ANIMATO_ANIMAGUS(p, sender, 1.0));
               }
               else
               {
                  O2Player o2p = p.getO2Player(sender);
                  o2p.setWandSpell(spellType);
                  p.setO2Player(sender, o2p);
               }

               boolean fastLearning = false;
               if (p.players.playerEffects.hasEffect(sender.getUniqueId(), O2EffectType.FAST_LEARNING))
               {
                  fastLearning = true;
               }
               p.incSpellCount(sender, spellType);
               if (fastLearning)
               {
                  p.incSpellCount(sender, spellType);
               }
            }
         }
         else
         {
            if (Ollivanders2.debug)
            {
               p.getLogger().info("Either no spell cast attempted or not allowed to cast");
            }
         }
      }

      if (Ollivanders2.debug)
      {
         p.getLogger().info("onPlayerChat: return");
      }
   }

   /**
    * Apparates sender to either specified location or to eye target location. Respects anti-apparition and anti-disapparition spells.
    *
    * @param sender Player apparating
    * @param words Typed in words
    */
   private void apparate (Player sender, String[] words)
   {
      boolean canApparateOut = true;
      for (StationarySpellObj stat : p.stationarySpells.getActiveStationarySpells())
      {
         if (stat instanceof NULLUM_EVANESCUNT && stat.isInside(sender.getLocation()))
         {
            stat.flair(10);
            canApparateOut = false;
         }
      }
      if (sender.isPermissionSet("Ollivanders2.BYPASS"))
      {
         if (sender.hasPermission("Ollivanders2.BYPASS"))
         {
            canApparateOut = true;
         }
      }
      if (canApparateOut)
      {
         int uses = p.incSpellCount(sender, O2SpellType.APPARATE);
         Location from = sender.getLocation().clone();
         Location to;
         if (words.length == 4)
         {
            try
            {
               to = new Location(sender.getWorld(),
                     Double.parseDouble(words[1]),
                     Double.parseDouble(words[2]),
                     Double.parseDouble(words[3]));
            }
            catch (NumberFormatException e)
            {
               to = sender.getLocation().clone();
            }
         }
         else
         {
            Location eyeLocation = sender.getEyeLocation();
            Material inMat = eyeLocation.getBlock().getType();
            int distance = 0;
            while ((inMat == Material.AIR || inMat == Material.FIRE || inMat == Material.WATER || inMat == Material.STATIONARY_WATER || inMat == Material.LAVA || inMat == Material.STATIONARY_LAVA) && distance < 160)
            {
               eyeLocation = eyeLocation.add(eyeLocation.getDirection());
               distance++;
               inMat = eyeLocation.getBlock().getType();
            }
            to = eyeLocation.subtract(eyeLocation.getDirection()).clone();
         }
         to.setPitch(from.getPitch());
         to.setYaw(from.getYaw());
         Double distance = from.distance(to);
         Double radius;
         if (p.playerCommon.holdsWand(sender))
         {
            radius = 1 / Math.sqrt(uses) * distance * 0.1 * p.playerCommon.wandCheck(sender);
         }
         else
         {
            radius = 1 / Math.sqrt(uses) * distance * 0.01;
         }
         Double newX = to.getX() - (radius / 2) + (radius * Math.random());
         Double newZ = to.getZ() - (radius / 2) + (radius * Math.random());
         to.setX(newX);
         to.setZ(newZ);
         boolean canApparateIn = true;
         for (StationarySpellObj stat : p.stationarySpells.getActiveStationarySpells())
         {
            if (stat instanceof NULLUM_APPAREBIT && stat.isInside(to))
            {
               stat.flair(10);
               canApparateIn = false;
            }
         }
         if (sender.isPermissionSet("Ollivanders2.BYPASS"))
         {
            if (sender.hasPermission("Ollivanders2.BYPASS"))
            {
               canApparateIn = true;
            }
         }
         if (canApparateIn)
         {
            sender.getWorld().createExplosion(sender.getLocation(), 0);
            sender.teleport(to);
            sender.getWorld().createExplosion(sender.getLocation(), 0);
            for (Entity e : sender.getWorld().getEntities())
            {
               if (from.distance(e.getLocation()) <= 2)
               {
                  e.teleport(to);
               }
            }
         }
      }
   }

   /**
    * Monitors chat events for the owl-post keywords and enacts the owl-post system
    *
    * @param event Chat event of type AsyncPlayerChatEvent
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void owlPost (AsyncPlayerChatEvent event)
   {
      Player sender = event.getPlayer();
      Server server = sender.getServer();
      World world = sender.getWorld();
      String message = event.getMessage();
      String[] splited = message.split("\\s+", 3);
      if (splited.length == 3)
      {
         if (splited[0].equalsIgnoreCase("deliver") && splited[1].equalsIgnoreCase("to"))
         {
            for (Entity entity : world.getEntities())
            {
               if (entity.getLocation().distance(sender.getLocation()) <= 10)
               {
                  Creature owl;
                  if (Ollivanders2.mcVersionCheck() && entity instanceof Parrot)
                  {
                     owl = (Parrot) entity;
                  }
                  else if (entity instanceof Ocelot)
                  {
                     owl = (Ocelot) entity;
                     Ocelot o = (Ocelot)owl;
                     if (!o.isTamed())
                     {
                        continue;
                     }
                  }
                  else
                  {
                     continue;
                  }

                  for (Entity item : world.getEntities())
                  {
                     if (item instanceof Item && item.getLocation().distance(owl.getLocation()) <= 2)
                     {
                        Player recipient = server.getPlayer(splited[2]);
                        if (recipient != null)
                        {
                           if (recipient.isOnline())
                           {
                              if (recipient.getWorld().getUID().equals(world.getUID()))
                              {
                                 if (Ollivanders2.mcVersionCheck())
                                 {
                                    world.playSound(owl.getLocation(), Sound.ENTITY_PARROT_AMBIENT, 1, 0);
                                 }
                                 else
                                 {
                                    world.playSound(owl.getLocation(), Sound.ENTITY_CAT_PURREOW, 1, 0);
                                 }
                                 owl.teleport(recipient.getLocation());
                                 item.teleport(recipient.getLocation());
                                 if (Ollivanders2.mcVersionCheck())
                                 {
                                    world.playSound(owl.getLocation(), Sound.ENTITY_PARROT_AMBIENT, 1, 0);
                                 }
                                 else
                                 {
                                    world.playSound(owl.getLocation(), Sound.ENTITY_CAT_PURREOW, 1, 0);
                                 }
                              }
                              else
                              {
                                 if (Ollivanders2.mcVersionCheck())
                                 {
                                    world.playSound(owl.getLocation(), Sound.ENTITY_PARROT_HURT, 1, 0);
                                 }
                                 else
                                 {
                                    world.playSound(owl.getLocation(), Sound.ENTITY_CAT_HISS, 1, 0);
                                 }
                                 sender.sendMessage(Ollivanders2.chatColor + splited[2] + " is not in this world.");
                              }
                           }
                           else
                           {
                              if (Ollivanders2.mcVersionCheck())
                              {
                                 world.playSound(owl.getLocation(), Sound.ENTITY_PARROT_HURT, 1, 0);
                              }
                              else
                              {
                                 world.playSound(owl.getLocation(), Sound.ENTITY_CAT_HISS, 1, 0);
                              }
                              sender.sendMessage(Ollivanders2.chatColor + splited[2] + " is not online.");
                           }
                        }
                        else
                        {
                           if (Ollivanders2.mcVersionCheck())
                           {
                              world.playSound(owl.getLocation(), Sound.ENTITY_PARROT_HURT, 1, 0);
                           }
                           else
                           {
                              world.playSound(owl.getLocation(), Sound.ENTITY_CAT_HISS, 1, 0);
                           }
                           sender.sendMessage(Ollivanders2.chatColor + splited[2] + " is not online.");
                        }
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   /**
    * This creates the spell projectile.
    *
    * @param player the player that cast the spell
    * @param name the name of the spell cast
    * @param wandC the wand check value for the held wand
    */
   private void createSpellProjectile (Player player, O2SpellType name, double wandC)
   {
      if (Ollivanders2Common.libsDisguisesSpells.contains(name) && !Ollivanders2.libsDisguisesEnabled)
      {
         return;
      }

      //spells go here, using any of the three types of m
      String spellClass = "net.pottercraft.Ollivanders2.Spell." + name.toString();

      Constructor c;
      try
      {
         //Maybe you have to use Integer.TYPE here instead of Integer.class
         c = Class.forName(spellClass).getConstructor(Ollivanders2.class, Player.class, Double.class);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return;
      }

      try
      {
         p.addProjectile((O2Spell) c.newInstance(p, player, wandC));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Action by player to cast a spell
    *
    * @since 2.2.7
    * @param player the player casting the spell
    */
   private void castSpell (Player player)
   {
      O2Player o2p = p.players.getPlayer(player.getUniqueId());
      if (o2p == null)
      {
         if (Ollivanders2.debug)
            p.getLogger().info("Unable to find o2player casting spell.");

         return;
      }

      O2SpellType spell = o2p.getWandSpell();

      if (spell != null)
      {
         double wandCheck;
         boolean playerHoldsWand = p.playerCommon.holdsWand(player, EquipmentSlot.HAND);
         if (playerHoldsWand)
         {
            if (Ollivanders2.debug)
               p.getLogger().info("OllivandersListener:castSpell: player holds a wand in their primary hand");

            wandCheck = p.playerCommon.wandCheck(player, EquipmentSlot.HAND);
            allyWand(player);
         }
         else
         {
            if (Ollivanders2.debug)
            {
               p.getLogger().info("OllivandersListener:castSpell: player does not hold a wand in their primary hand");
            }
            return;
         }

         createSpellProjectile(player, spell, wandCheck);
         o2p.setSpellRecentCastTime(spell);
         int spellCastCount = p.getSpellNum(player, spell);
         if (spellCastCount < 100 || spell == O2SpellType.AVADA_KEDAVRA)
         {
            if (Ollivanders2.debug)
            {
               p.getLogger().info("OllivandersListener:castSpell: allow cast spell");
            }

            o2p.setWandSpell(null);
            p.setO2Player(player, o2p);
         }
      }
   }

   /**
    * Handle events when player interacts with an item in their hand.
    *
    * @param event the player interact event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void onPlayerInteract (PlayerInteractEvent event)
   {
      Player player = event.getPlayer();
      Action action = event.getAction();

      if (Ollivanders2.debug)
         p.getLogger().info("onPlayerInteract: enter");

      if (action == null || player == null)
      {
         return;
      }

      //
      // A right or left click of the primary hand when holding a wand is used to make a magical action.
      //
      if ((event.getHand() == EquipmentSlot.HAND) && (p.playerCommon.holdsWand(player, EquipmentSlot.HAND)))
      {
         if (p.players.playerEffects.hasEffect(player.getUniqueId(), O2EffectType.SLEEPING))
         {
            event.setCancelled(true);
            return;
         }

         //
         // A left click of the primary hand is used to cast a spell
         //
         if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
         {
            if (Ollivanders2.debug)
               p.getLogger().info("OllivandersListener:onPlayerInteract: left click action");

            castSpell(player);
         }

         //
         // A right click is used:
         // - to determine if the wand is the player's destined wand
         // - to brew a potion if they are holding a glass bottle in their off hand and facing a cauldron
         //
         else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
         {
            if (!p.playerCommon.holdsWand(player))
               return;

            if (Ollivanders2.debug)
               p.getLogger().info("OllivandersListener:onPlayerInteract: right click action");

            Block cauldron = (playerFacingCauldron(player));
            if ((cauldron != null) && (player.getInventory().getItemInOffHand().getType() == Material.GLASS_BOTTLE))
            {
               if (Ollivanders2.debug)
                  p.getLogger().info("OllivandersListener:onPlayerInteract: brewing potion");

               brewPotion(player, cauldron);
               return;
            }

            if (Ollivanders2.debug)
               p.getLogger().info("OllivandersListener:onPlayerInteract: waving destined wand");

            // play a sound and visual effect when they right-click their destined wand with no spell
            Location location = player.getLocation();
            location.setY(location.getY() + 1.6);
            player.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 0);
            player.getWorld().playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
         }
      }
      //
      // A right or left click of the off hand is used to rotate through mastered spells for non-verbal spell casting.
      //
      else if ((event.getHand() == EquipmentSlot.OFF_HAND) && (p.playerCommon.holdsWand(player, EquipmentSlot.HAND)))
      {
         if (p.players.playerEffects.hasEffect(player.getUniqueId(), O2EffectType.SLEEPING))
         {
            event.setCancelled(true);
            return;
         }

         rotateNonVerbalSpell(player, action);
      }
   }

   /**
    * If non-verbal spell casting is enabled, selects a new spell from mastered spells.
    *
    * @param player the player rotating spells
    * @param action the player action
    */
   void rotateNonVerbalSpell (Player player, Action action)
   {
      if (!Ollivanders2.useNonVerbalCasting)
         return;

      if (Ollivanders2.debug)
         p.getLogger().info("Rotating mastered spells for non-verbal casting.");

      if (!p.playerCommon.holdsWand(player, EquipmentSlot.OFF_HAND))
         return;

      O2Player o2p = p.players.getPlayer(player.getUniqueId());
      if (o2p == null)
         return;

      boolean reverse = false;
      // right click rotates through spells backwards
      if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
         reverse = true;

      o2p.shiftMasterSpell(reverse);
      O2SpellType spell = o2p.getMasterSpell();
      if (spell != null)
      {
         String spellName = p.common.firstLetterCapitalize(p.common.enumRecode(spell.toString()));
         player.sendMessage(Ollivanders2.chatColor + "Wand master spell set to " + spellName);
      }
      else
      {
         if (Ollivanders2.debug)
         {
            player.sendMessage(Ollivanders2.chatColor + "You have not mastered any spells.");
         }
      }
   }

   /**
    * Handle player joining event.
    *
    * @param event the player join event
    */
   @EventHandler(priority = EventPriority.NORMAL)
   public void onPlayerJoin (PlayerJoinEvent event)
   {
      Player player = event.getPlayer();

      O2Player o2p = p.players.getPlayer(player.getUniqueId());

      if (o2p == null) // new player
      {
         o2p = p.getO2Player(player);
      }
      else // existing player
      {

         // update player's display name in case it has changed
         if (!o2p.getPlayerName().equalsIgnoreCase(player.getName()))
         {
            o2p.setPlayerName(player.getName());
         }

         // add player to their house team
         p.houses.addPlayerToHouseTeam(player);

         // do player join actions
         o2p.onJoin();
      }

      // re-add them to player list (in case they have changed from above actions)
      p.setO2Player(player, o2p);

      // show log in message
      if (p.getConfig().getBoolean("showLogInMessage"))
      {
         StringBuilder message = new StringBuilder();

         if (player.hasPlayedBefore())
         {
            message.append("Welcome back, ");
            message.append(player.getName());
            message.append("\n");
            message.append(o2p.getLogInMessage());
         }
         else
         {
            message.append("You're a wizard, ");
            message.append(player.getName());
         }

         player.sendMessage(Ollivanders2.chatColor + message.toString());
      }

      p.getLogger().info("Player " + player.getName() + " joined.");
   }

   /**
    * Handle player quitting.
    *
    * @param event the quit event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onPlayerQuit (PlayerQuitEvent event)
   {
      playerQuit(event.getPlayer());
   }

   /**
    * Handle player being kicked from the server.
    *
    * @param event the kick event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onPlayerKick (PlayerKickEvent event)
   {
      playerQuit(event.getPlayer());
   }

   /**
    * Upkeep when a player leaves the game.
    *
    * @param player the player
    */
   private void playerQuit (Player player)
   {
      O2Player o2p = p.players.getPlayer(player.getUniqueId());
      if (o2p == null)
         return;

      // do player quit actions
      o2p.onQuit();

      p.getLogger().info("Player " + player.getName() + " left.");
   }

   /**
    * Handle player death event.
    *
    * @param event the player death event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onPlayerDeath (PlayerDeathEvent event)
   {
      if (p.getConfig().getBoolean("deathExpLoss"))
      {
         O2Player o2p = p.players.getPlayer(event.getEntity().getUniqueId());

         if (o2p == null)
            return;

         o2p.onDeath();

         p.setO2Player(event.getEntity(), o2p);
      }
   }

   /**
    * This checks if a player kills another player, and if so, adds a soul
    * to the attacking o2player
    *
    * @param event the entity damage event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onEntityDamage (EntityDamageByEntityEvent event)
   {
      if (event.getEntity() instanceof Player)
      {
         Player damaged = (Player) event.getEntity();

         if (event.getDamager() instanceof Player)
         {
            Player attacker = (Player) event.getDamager();
            if (damaged.getHealth() - event.getDamage() <= 0)
            {
               p.getO2Player(attacker).addSoul();
            }
         }
         if (event.getDamager() instanceof Wolf)
         {
            Wolf wolf = (Wolf) event.getDamager();
            if (wolf.isAngry())
            {
               if (!p.players.playerEffects.hasEffect(damaged.getUniqueId(), O2EffectType.LYCANTHROPY))
               {
                  LYCANTHROPY effect = new LYCANTHROPY(p, 100, damaged.getUniqueId());
                  p.players.playerEffects.addEffect(effect);
               }
            }
         }
      }
   }

   /**
    * Handles when players receive damage.
    *
    * @param event the player damage event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onPlayerDamage (EntityDamageEvent event)
   {
      if (checkSpongify(event))
      {
         return;
      }

      //Horcrux code
      List<StationarySpellObj> stationarys = p.stationarySpells.getActiveStationarySpells();
      if (event.getEntity() instanceof Player)
      {
         Damageable plyr = (Damageable) event.getEntity();
         UUID pid = event.getEntity().getUniqueId();
         if ((plyr.getHealth() - event.getDamage()) <= 0)
         {
            for (StationarySpellObj stationary : stationarys)
            {
               if (stationary.name == StationarySpells.HORCRUX && stationary.getCasterID().equals(pid))
               {
                  Location tp = stationary.location;
                  tp.setY(tp.getY() + 1);
                  plyr.teleport(tp);

                  Collection<PotionEffect> potions = ((Player) event.getEntity()).getActivePotionEffects();
                  for (PotionEffect potion : potions)
                  {
                     ((Player) event.getEntity()).removePotionEffect(potion.getType());
                  }
                  event.setCancelled(true);
                  plyr.setHealth(((Player) plyr).getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                  p.stationarySpells.removeStationarySpell(stationary);
                  return;
               }
            }
         }
      }
   }

   /**
    * Checks to see if the entity was within a spongify stationary spell object. If so, cancells the damage event
    *
    * @param event the Entity  Damage Event
    * @return true if the entity was within spongify
    */
   private boolean checkSpongify (EntityDamageEvent event)
   {
      Entity entity = event.getEntity();
      for (StationarySpellObj spell : p.stationarySpells.getActiveStationarySpells())
      {
         if (spell instanceof MOLLIARE && event.getCause() == DamageCause.FALL)
         {
            if (spell.isInside(entity.getLocation()))
            {
               event.setCancelled(true);
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Cancels any block place event inside of a colloportus object
    *
    * @param event the block place event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onColloBlockPlaceEvent (BlockPlaceEvent event)
   {
      if (p.stationarySpells.isInsideOf(StationarySpells.COLLOPORTUS, event.getBlock().getLocation()))
      {
         if (event.getPlayer().isPermissionSet("Ollivanders2.BYPASS"))
         {
            if (!event.getPlayer().hasPermission("Ollivanders2.BYPASS"))
            {
               event.getBlock().breakNaturally();
            }
         }
         else
         {
            event.getBlock().breakNaturally();
         }
      }
   }

   /**
    * Cancels any block break event inside of a colloportus object
    *
    * @param event the block break event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onColloBlockBreakEvent (BlockBreakEvent event)
   {
      if (p.stationarySpells.isInsideOf(StationarySpells.COLLOPORTUS, event.getBlock().getLocation()))
      {
         if (event.getPlayer().isPermissionSet("Ollivanders2.BYPASS"))
         {
            if (!event.getPlayer().hasPermission("Ollivanders2.BYPASS"))
            {
               event.setCancelled(true);
            }
         }
         else
         {
            event.setCancelled(true);
         }
      }
   }

   /**
    * Cancels any block physics event inside of a colloportus object
    *
    * @param event the block physics event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onColloBlockPhysicsEvent (BlockPhysicsEvent event)
   {
      if (p.stationarySpells.isInsideOf(StationarySpells.COLLOPORTUS, event.getBlock().getLocation()))
      {
         event.setCancelled(true);
      }
   }

   /**
    * Cancels any block interact event inside a colloportus object
    *
    * @param event the player interact event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onColloPlayerInteract (PlayerInteractEvent event)
   {
      if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)
      {
         if (p.stationarySpells.isInsideOf(StationarySpells.COLLOPORTUS, event.getClickedBlock().getLocation()))
         {
            if (event.getPlayer().isPermissionSet("Ollivanders2.BYPASS"))
            {
               if (!event.getPlayer().hasPermission("Ollivanders2.BYPASS"))
               {
                  event.setCancelled(true);
               }
            }
            else
            {
               event.setCancelled(true);
            }
         }
      }
   }

   /**
    * Cancels any piston extend event inside a colloportus
    *
    * @param event the block piston extend event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onColloPistonExtend (BlockPistonExtendEvent event)
   {
      ArrayList<COLLOPORTUS> collos = new ArrayList<>();
      for (StationarySpellObj stat : p.stationarySpells.getActiveStationarySpells())
      {
         if (stat instanceof COLLOPORTUS)
         {
            collos.add((COLLOPORTUS) stat);
         }
      }

      List<Block> blocks = event.getBlocks();
      BlockFace direction = event.getDirection();
      for (Block block : blocks)
      {
         Block newBlock = block.getRelative(direction.getModX(), direction.getModY(), direction.getModZ());
         for (COLLOPORTUS collo : collos)
         {
            if (collo.isInside(newBlock.getLocation()) || collo.isInside(block.getLocation()))
            {
               event.setCancelled(true);
               return;
            }
         }
      }
   }

   /**
    * Cancels any piston retract event inside of a colloportus
    *
    * @param event the block Piston Retract Event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onColloPistonRetract (BlockPistonRetractEvent event)
   {
      if (event.isSticky())
      {
         if (p.stationarySpells.isInsideOf(StationarySpells.COLLOPORTUS, event.getRetractLocation()))
         {
            event.setCancelled(true);
         }
      }
   }

   /**
    * Cancels any block change by an entity inside of a colloportus
    *
    * @param event the entity Change Block Event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onColloEntityChangeBlock (EntityChangeBlockEvent event)
   {
      Location loc = event.getBlock().getLocation();
      Entity entity = event.getEntity();
      if (p.stationarySpells.isInsideOf(StationarySpells.COLLOPORTUS, loc))
      {
         event.setCancelled(true);
         if (event.getEntityType() == EntityType.FALLING_BLOCK)
         {
            loc.getWorld().dropItemNaturally(loc, new ItemStack(((FallingBlock) entity).getMaterial()));
         }
      }
   }

   /**
    * If a block is broken that is temporary, prevent it from dropping anything.
    *
    * @param event the block break event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onTemporaryBlockBreak (BlockBreakEvent event)
   {
      Block block = event.getBlock();
      List<Block> tempBlocks = p.getTempBlocks();
      if (tempBlocks.contains(block))
      {
         event.setCancelled(true);
         tempBlocks.remove(block);
         block.setType(Material.AIR);
      }
   }

   /**
    * If a block is a tempBlock or is inside colloportus, then don't blow it up.
    *
    * @param event the entity explode event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void onExplosion (EntityExplodeEvent event)
   {
      List<Block> blockListCopy = new ArrayList<>();
      blockListCopy.addAll(event.blockList());
      List<Block> tempBlocks = p.getTempBlocks();
      for (Block block : blockListCopy)
      {
         if (tempBlocks.contains(block))
         {
            event.blockList().remove(block);
         }
         for (StationarySpellObj stat : p.stationarySpells.getActiveStationarySpells())
         {
            if (stat instanceof COLLOPORTUS)
            {
               if (stat.isInside(block.getLocation()))
               {
                  event.blockList().remove(block);
               }
            }
         }
      }
   }

   /**
    * If a wand is not already allied with a player, this allies it.
    *
    * @param player player holding a wand.
    */
   public void allyWand (Player player)
   {
      ItemStack wand = player.getInventory().getItemInMainHand();
      ItemMeta wandMeta = wand.getItemMeta();
      List<String> wandLore = wandMeta.getLore();
      if (wandLore.size() == 1)
      {
         wandLore.add(player.getUniqueId().toString());
         wandMeta.setLore(wandLore);
         wand.setItemMeta(wandMeta);
         player.getInventory().setItemInMainHand(wand);
      }
   }

   /**
    * Prevents a transfigured entity from changing any blocks by exploding.
    *
    * @param event the entity explode event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void transfiguredEntityExplodeCancel (EntityExplodeEvent event)
   {
      if (event.getEntity() != null)
      {
         for (O2Spell proj : p.getProjectiles())
         {
            if (proj instanceof Transfiguration)
            {
               Transfiguration trans = (Transfiguration) proj;
               if (trans.getToID() == event.getEntity().getUniqueId())
               {
                  event.setCancelled(true);
               }
            }
         }
      }
   }

   /**
    * When an item is picked up by a player, if the item is a portkey, the player will be teleported there.
    *
    * @param event the player Pickup Item Event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void portkeyPickUp (EntityPickupItemEvent event)
   {
      Entity entity = event.getEntity();
      if (entity instanceof Player)
      {
         Player player = (Player) entity;
         Item item = event.getItem();
         ItemMeta meta = item.getItemStack().getItemMeta();
         List<String> lore;
         if (meta.hasLore())
         {
            lore = meta.getLore();
         }
         else
         {
            lore = new ArrayList<>();
         }
         for (String s : lore)
         {
            if (s.startsWith("Portkey"))
            {
               String[] portArray = s.split(" ");
               Location to;
               to = new Location(Bukkit.getServer().getWorld(UUID.fromString(portArray[1])),
                     Double.parseDouble(portArray[2]),
                     Double.parseDouble(portArray[3]),
                     Double.parseDouble(portArray[4]));
               to.setDirection(player.getLocation().getDirection());
               for (Entity e : player.getWorld().getEntities())
               {
                  if (player.getLocation().distance(e.getLocation()) <= 2)
                  {
                     e.teleport(to);
                  }
               }
               player.teleport(to);
               lore.remove(lore.indexOf(s));
               meta.setLore(lore);
               item.getItemStack().setItemMeta(meta);
               return;
            }
         }
      }
   }

   /**
    * Cancels any targeting of players with the Cloak of Invisibility
    * or inside of a REPELLO_MUGGLETON while the targeting entity is
    * outside it.
    *
    * @param event the Entity Target Event
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void cloakPlayer (EntityTargetEvent event)
   {
      Entity target = event.getTarget();
      if (target instanceof Player)
      {
         if (p.getO2Player((Player)target).isInvisible())
         {
            event.setCancelled(true);
         }
      }
      if (target != null)
      {
         for (StationarySpellObj stat : p.stationarySpells.getActiveStationarySpells())
         {
            if (stat instanceof REPELLO_MUGGLETON)
            {
               if (stat.isInside(target.getLocation()))
               {
                  if (!stat.isInside(event.getEntity().getLocation()))
                  {
                     event.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   /**
    * Cancels any targeting of players who own inferi by that inferi
    *
    * @param event the Entity Target Event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void inferiTarget (EntityTargetEvent event)
   {
      Entity target = event.getTarget();
      Entity entity = event.getEntity();
      for (O2Spell sp : p.getProjectiles())
      {
         if (sp instanceof MORTUOS_SUSCITATE)
         {
            Transfiguration trans = (Transfiguration) sp;
            if (trans.getToID() == entity.getUniqueId() && trans.player == target)
            {
               event.setCancelled(true);
            }
         }
      }
   }

   /**
    * This drops a random wand when a witch dies
    *
    * @param event the entity death event
    */
   @EventHandler(priority = EventPriority.NORMAL)
   public void witchWandDrop (EntityDeathEvent event)
   {
      if (event.getEntityType() == EntityType.WITCH && p.getConfig().getBoolean("witchDrop"))
      {
         int wandType = Math.abs(Ollivanders2.random.nextInt() % 4);
         int coreType = Math.abs(Ollivanders2.random.nextInt() % 4);
         String[] woodArray = {"Spruce", "Jungle", "Birch", "Oak"};
         String[] coreArray = {"Spider Eye", "Bone", "Rotten Flesh", "Gunpowder"};
         ItemStack wand = new ItemStack(Material.STICK);
         List<String> lore = new ArrayList<>();
         lore.add(woodArray[wandType] + " and " + coreArray[coreType]);
         ItemMeta meta = wand.getItemMeta();
         meta.setLore(lore);
         meta.setDisplayName("Wand");
         wand.setItemMeta(meta);
         event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), wand);
      }
   }

   /**
    * When a player consumes something, see if it was a potion and apply the effect if it was.
    *
    * @param event the player item consume event
    */
   @EventHandler(priority = EventPriority.NORMAL)
   public void onPlayerDrink (PlayerItemConsumeEvent event)
   {
      ItemStack item = event.getItem();
      if (item.getType() == Material.POTION)
      {
         Player player = event.getPlayer();

         if (Ollivanders2.debug)
         {
            p.getLogger().info(player.getDisplayName() + " drank a potion.");
         }

         O2Player o2p = p.getO2Player(player);

         ItemMeta meta = item.getItemMeta();
         if (meta.hasLore())
         {
            O2Potion potion = p.getO2Potions().findPotionByItemMeta(meta);

            if (potion != null)
            {
               if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potion.getPotionType()))
               {
                  return;
               }

               potion.drink(o2p, player);
            }
         }
      }
   }

   /**
    * Event fires when a player right clicks with a broom in their hand
    *
    * @param event the player interact event
    */
   @EventHandler(priority = EventPriority.HIGH)
   public void broomClick (PlayerInteractEvent event)
   {
      Player player = event.getPlayer();

      if (((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK))
            && (player.getInventory().getItemInMainHand() != null)
            && (this.p.common.isBroom(player.getInventory().getItemInMainHand())))
      {
         UUID playerUid = player.getUniqueId();
         Set<UUID> flying = OllivandersSchedule.getFlying();
         if (flying.contains(playerUid))
         {
            flying.remove(playerUid);
         }
         else
         {
            flying.add(playerUid);
         }
      }
   }

   /**
    * Process book read events when bookLearning is enabled.
    *
    * @param event the player interact event
    */
   @EventHandler(priority = EventPriority.LOWEST)
   public void onBookRead (PlayerInteractEvent event)
   {
      // only run this if bookLearning is enabled
      if (!Ollivanders2.useBookLearning)
         return;

      Action action = event.getAction();
      if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)
      {
         Player player = event.getPlayer();

         ItemStack heldItem = player.getInventory().getItemInMainHand();
         if (heldItem.getType() == Material.WRITTEN_BOOK)
         {
            if (Ollivanders2.debug)
               p.getLogger().info(player.getDisplayName() + " reading a book and book learning is enabled.");

            // reading a book, if it is a spell book we want to let the player "learn" the spell.
            List<String> bookLore = heldItem.getItemMeta().getLore();

            O2Books.readLore(bookLore, player, p);
         }
      }
   }

   /**
    * When a user holds their spell journal, replace it with an updated version of the book.
    *
    * @param event the player item held event
    */
   @EventHandler (priority = EventPriority.LOWEST)
   public void onSpellJournalHold (PlayerItemHeldEvent event)
   {
      // only run this if spellJournal is enabled
      if (event == null || !p.getConfig().getBoolean("spellJournal"))
         return;

      Player player = event.getPlayer();
      int slotIndex = event.getNewSlot();

      ItemStack heldItem = player.getInventory().getItem(slotIndex);
      if (heldItem != null && heldItem.getType() == Material.WRITTEN_BOOK)
      {
         BookMeta bookMeta = (BookMeta)heldItem.getItemMeta();
         if (bookMeta.getTitle().equalsIgnoreCase("Spell Journal"))
         {
            O2Player o2Player = p.getO2Player(player);
            ItemStack spellJournal = o2Player.getSpellJournal();

            player.getInventory().setItem(slotIndex, spellJournal);
         }
      }
   }

   /**
    * Handle potion making actions.
    *
    * @param event the player toggle sneak event
    */
   @EventHandler (priority = EventPriority.NORMAL)
   public void onPotionBrewing (PlayerToggleSneakEvent event)
   {
      Player player = event.getPlayer();

      // is the player sneaking
      if (!event.isSneaking())
      {
         return;
      }

      Block cauldron = playerFacingCauldron(player);
      if (cauldron == null)
      {
         return;
      }

      // check that the item held is in their left hand
      ItemStack heldItem = player.getInventory().getItemInOffHand();
      if (heldItem == null || heldItem.getAmount() == 0)
      {
         return;
      }

      String ingredientName = heldItem.getItemMeta().getDisplayName();

      // put the item in the player's off hand in to the cauldron
      Location spawnLoc = cauldron.getLocation();
      World world = cauldron.getWorld();

      Item item = world.dropItem(spawnLoc.add(+0.5, +0.5, +0.5), heldItem.clone());

      if (item == null)
      {
         if (Ollivanders2.debug)
            p.getLogger().info("onPotionBrewing: failed to spawn dropped item in cauldron");

         return;
      }
      else
      {
         player.sendMessage(Ollivanders2.chatColor + "Added " + ingredientName);
      }

      item.setVelocity(new Vector(0, 0, 0));
      player.getInventory().setItemInOffHand(null);
   }

   /**
    * Determine if a player is facing a cauldron.
    *
    * @param player the player to check
    * @return the cauldron if a player is facing one, null otherwise
    */
   private Block playerFacingCauldron (Player player)
   {
      List<Block> blocksInFront = player.getLineOfSight(null, 3);
      Block cauldron = null;

      for (Block block : blocksInFront)
      {
         if (block.getType() == Material.CAULDRON)
         {
            cauldron = block;
            break;
         }
      }

      return cauldron;
   }

   /**
    * Brew a potion from the ingredients in a cauldron.
    *
    * @apiNote assumes player is holding a glass bottle in their off hand and will set off hand item to null
    * @param player the player brewing the potion
    * @param cauldron the cauldron of ingredients
    */
   void brewPotion (Player player, Block cauldron)
   {
      if (Ollivanders2.debug)
         p.getLogger().info("OllivandersListener:brewPotion: brewing potion");

      Block under = cauldron.getRelative(BlockFace.DOWN);
      if (under.getType() == Material.FIRE || under.getType() == Material.LAVA || under.getType() == Material.STATIONARY_LAVA)
      {
         O2Potions potions = p.getO2Potions();

         ItemStack potion = potions.brewPotion(cauldron, player);

         if (potion == null)
         {
            player.sendMessage(Ollivanders2.chatColor + "The cauldron appears unchanged. Perhaps you should check your recipe");
            return;
         }

         // remove ingredients from cauldron
         for (Entity e : cauldron.getWorld().getNearbyEntities(cauldron.getLocation(), 1, 1, 1))
         {
            if (e instanceof Item)
            {
               e.remove();
            }
         }

         player.getWorld().playEffect(cauldron.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
         player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
         player.getInventory().setItemInOffHand(potion);
      }
      else
      {
         if (Ollivanders2.debug)
         {
            p.getLogger().info("Cauldron is not over a hot block");
         }
      }
   }

   /**
    * Handle effects for O2SplashPotion throws
    *
    * @param event the potion splash event
    */
   @EventHandler (priority = EventPriority.HIGH)
   public void onSplashPotion (PotionSplashEvent event)
   {
      ThrownPotion thrown = event.getEntity();
      ItemMeta meta = thrown.getItem().getItemMeta();

      O2Potion potion = p.getO2Potions().findPotionByItemMeta(meta);

      if (potion != null)
      {
         if (potion instanceof O2SplashPotion)
         {
            ((O2SplashPotion)potion).thrownEffect(event);
         }
      }
   }

   /**
    * Handle player interact events when they are affected by an effect that alters interacts.
    *
    * @param event the player interact event
    */
   @EventHandler (priority = EventPriority.HIGHEST)
   public void onAffectedInteract (PlayerInteractEvent event)
   {
      Player player = event.getPlayer();

      if (p.players.playerEffects.hasEffect(player.getUniqueId(), O2EffectType.SLEEPING))
      {
         // cannot interact with anything while asleep
         event.setCancelled(true);
         return;
      }
   }

   /**
    * Handle player sleep event.
    *
    * @param event the player bed enter event
    */
   @EventHandler (priority = EventPriority.HIGH)
   public void onPlayerSleep (PlayerBedEnterEvent event)
   {
      Player player = event.getPlayer();

      if (p.players.playerEffects.hasEffect(player.getUniqueId(), O2EffectType.AWAKE))
      {
         // cannot sleep while awake effect is active
         event.setCancelled(true);
         return;
      }
   }
}