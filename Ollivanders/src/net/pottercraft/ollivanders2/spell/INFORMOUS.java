package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.house.O2Houses;
import net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS;
import net.pottercraft.ollivanders2.stationaryspell.HORCRUX;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

/**
 * Gives information on LivingEntity (health) and StationarySpellObj (duration)
 * and weather (duration) and Player (spell effects). Range of spell depends on level.
 *
 * @author lownes
 * @author Azami7
 */
public final class INFORMOUS extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public INFORMOUS(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.INFORMOUS;
      branch = O2MagicBranch.ARITHMANCY;

      flavorText = new ArrayList<>()
      {{
         add("Basic Arithmancy");
      }};

      text = "Gives information on a living entity, weather, player, or stationary spell.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INFORMOUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INFORMOUS;
      branch = O2MagicBranch.ARITHMANCY;

      initSpell();
   }

   /**
    * Give information about an entity, stationary spells at the target, or the weather at the player's location.
    */
   @Override
   protected void doCheckEffect()
   {
      if (!hasHitTarget())
         return;

      boolean gaveInfo = false;

      for (LivingEntity entity : getLivingEntities(1.5))
      {
         if (entity.getUniqueId() == player.getUniqueId())
            continue;

         entityInfo(entity);

         gaveInfo = true;
      }

      if (!gaveInfo)
      {
         for (O2StationarySpell spell : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
         {
            if (spell.isInside(location))
            {
               stationarySpellInfo(spell);

               gaveInfo = true;
            }
         }
      }

      if (!gaveInfo)
      {
         Location playerLocation = player.getLocation();

         if (playerLocation.getY() > 256)
         {
            String weather;
            World world = playerLocation.getWorld();
            if (world == null)
            {
               common.printDebugMessage("INFORMOUS.doCheckEffect: world is null", null, null, true);
               kill();
               return;
            }

            boolean thunder = world.isThundering();

            if (world.hasStorm())
            {
               weather = "rain";
            }
            else
            {
               weather = "clear skies";
            }
            int weatherTime = world.getWeatherDuration();
            int thunderTime = world.getThunderDuration();

            player.sendMessage(Ollivanders2.chatColor + "There will be " + weather + " for " + weatherTime / Ollivanders2Common.ticksPerSecond + " more seconds.");
            if (thunder)
            {
               player.sendMessage(Ollivanders2.chatColor + "There will be thunder for " + thunderTime / Ollivanders2Common.ticksPerSecond + " more seconds.");
            }
         }
      }

      kill();
   }

   /**
    * Give information about a player.
    *
    * @param entity the entity
    */
   private void entityInfo(@NotNull LivingEntity entity)
   {
      String entityName;

      if (entity instanceof HumanEntity)
         entityName = entity.getName();
      else
         entityName = entity.getType().toString();

      // health level
      player.sendMessage(Ollivanders2.chatColor + entityName + " has " + entity.getHealth() + " health.");

      if (entity instanceof Player)
      {
         Player target = (Player) entity;

         // food level
         player.sendMessage(Ollivanders2.chatColor + " has " + target.getFoodLevel() + " food level.");

         // exhaustion level
         player.sendMessage(Ollivanders2.chatColor + " has " + target.getExhaustion() + " exhaustion level.");

         // detectable effects
         String infoText = Ollivanders2API.getPlayers().playerEffects.detectEffectWithInformous(entity.getUniqueId());
         if (infoText != null)
         {
            player.sendMessage(Ollivanders2.chatColor + " " + infoText + ".");
         }

         // line of sight
         if (target.canSee(player))
            player.sendMessage(Ollivanders2.chatColor + " can see you.");
         else
            player.sendMessage(Ollivanders2.chatColor + " cannot see you.");

         // house
         if (O2Houses.useHouses)
         {
            if (Ollivanders2API.getHouses().isSorted(target))
            {
               player.sendMessage(Ollivanders2.chatColor + " is a member of " + Ollivanders2API.getHouses().getHouse(target).getName() + ".");
            }
            else
            {
               player.sendMessage(Ollivanders2.chatColor + " has not been sorted.");
            }
         }
      }
   }

   /**
    * Information about a stationary spell.
    *
    * @param spell the stationary spell
    */
   private void stationarySpellInfo(@NotNull O2StationarySpell spell)
   {
      if (spell instanceof COLLOPORTUS)
      {
         int power;
         if (spell.duration >= 1200)
            power = spell.duration / 1200;
         else
            power = 1;

         player.sendMessage(Ollivanders2.chatColor + spell.getSpellType().toString() + " of radius " + spell.radius + " has " + power + " power left.");
      }
      else if (spell instanceof HORCRUX)
      {
         Player caster = Bukkit.getPlayer(spell.getCasterID());
         String casterString;

         if (caster != null)
            casterString = " of player " + caster.getName();
         else
            casterString = " cast by persons unknown ";

         player.sendMessage(Ollivanders2.chatColor + spell.getSpellType().toString() + casterString + " of radius " + spell.radius);
      }
      else if (spell instanceof ALIQUAM_FLOO)
      {
         player.sendMessage(Ollivanders2.chatColor + "Floo registration of " + ((ALIQUAM_FLOO) spell).getFlooName());
      }
      else if (spell instanceof HARMONIA_NECTERE_PASSUS)
      {
         player.sendMessage(Ollivanders2.chatColor + "Vanishing Cabinet");
      }
      else
      {
         player.sendMessage(Ollivanders2.chatColor + spell.getSpellType().toString() + " of radius " + spell.radius + " has " + spell.duration / 20 + " seconds left.");
      }
   }
}