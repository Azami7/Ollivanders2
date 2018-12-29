package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.pottercraft.Ollivanders2.*;
import net.pottercraft.Ollivanders2.StationarySpell.ALIQUAM_FLOO;
import net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS;
import net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS;
import net.pottercraft.Ollivanders2.StationarySpell.HORCRUX;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

/**
 * Gives information on LivingEntity (health) and StationarySpellObj (duration)
 * and weather (duration) and Player (spell effects). Range of spell depends on level.
 *
 * @author lownes
 * @author Azami7
 */
public final class INFORMOUS extends Arithmancy
{
   List<LivingEntity> iEntity = new ArrayList();
   List<StationarySpellObj> iSpell = new ArrayList();
   boolean toldWeather = false;
   private double lifeTime;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INFORMOUS ()
   {
      super();

      spellType = O2SpellType.INFORMOUS;

      flavorText = new ArrayList<String>() {{
         add("Basic Arithmancy");
      }};

      text = "Gives information on a living entity, weather, player, or stationary spell.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INFORMOUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INFORMOUS;
      setUsesModifier();

      lifeTime = usesModifier * 16;
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity entity : getLivingEntities(1.5))
      {
         if (entity.getUniqueId() == player.getUniqueId())
            continue;

         if (!iEntity.contains(entity))
         {
            String entityName = entity.getType().toString();
            if (entity instanceof HumanEntity)
               entityName = entity.getName();

            // health level
            player.sendMessage(Ollivanders2.chatColor + entityName + " has " + ((Damageable) entity).getHealth() + " health.");
            if (entity instanceof Player)
            {
               Player target = (Player)entity;

               // food level
               player.sendMessage(Ollivanders2.chatColor + " has " + target.getFoodLevel() + " food level.");

               // detectable effects
               UUID pid = player.getUniqueId();
               String infoText = p.players.playerEffects.detectEffectWithInformous(pid);
               if (infoText != null)
               {
                  player.sendMessage(Ollivanders2.chatColor + infoText);
               }

               // line of sight
               if (target.canSee(player))
                  player.sendMessage(Ollivanders2.chatColor + " can see you.");
               else
                  player.sendMessage(Ollivanders2.chatColor + " cannot see you.");

               // house
               if (p.houses.isSorted(target))
               {
                  player.sendMessage(Ollivanders2.chatColor + " is a member of " + p.houses.getHouse(target).getName() + ".");
               }
               else
                  player.sendMessage(Ollivanders2.chatColor + " has not been sorted.");
            }
            iEntity.add(entity);
         }
      }
      for (StationarySpellObj spell : p.stationarySpells.getActiveStationarySpells())
      {
         if (spell.isInside(location) && !iSpell.contains(spell))
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
               player.sendMessage(Ollivanders2.chatColor + spell.getSpellType().toString() + " of player " + Bukkit.getPlayer(spell.getCasterID()).getName() + " of radius " + spell.radius);
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
            iSpell.add(spell);
         }
      }
      if (location.getY() > 256 && !toldWeather)
      {
         toldWeather = true;
         String weather;
         World world = location.getWorld();
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
         player.sendMessage(Ollivanders2.chatColor + "There will be " + weather + " for " + weatherTime / 20 + " more seconds.");
         if (thunder)
         {
            player.sendMessage(Ollivanders2.chatColor + "There will be thunder for " + thunderTime / 20 + " more seconds.");
         }
      }
      if (lifeTicks > lifeTime)
      {
         kill();
      }
   }
}