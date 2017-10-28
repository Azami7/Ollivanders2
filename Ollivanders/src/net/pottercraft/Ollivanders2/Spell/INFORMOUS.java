package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.*;
import net.pottercraft.Ollivanders2.StationarySpell.ALIQUAM_FLOO;
import net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS;
import net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS;
import net.pottercraft.Ollivanders2.StationarySpell.HORCRUX;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effect.LYCANTHROPY;
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

      flavorText.add("Basic Arithmancy");
      text = "Gives information on a living entity, weather, player, or stationary spell.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public INFORMOUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      lifeTime = usesModifier * 16;
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity entity : getLivingEntities(1))
      {
         if (!iEntity.contains(entity))
         {
            player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + entity.getType().toString() + " has " + ((Damageable) entity).getHealth() + " health.");
            if (entity instanceof Player)
            {
               Player ePlayer = (Player) entity;
               O2Player o2p = p.getO2Player(ePlayer);
               for (OEffect effect : o2p.getEffects())
               {
                  if (effect instanceof LYCANTHROPY)
                  {
                     player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + ePlayer.getName() + " has Lycanthropy.");
                  }
                  else
                  {
                     player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + ePlayer.getName() + " has " + Effects.recode(effect.name) + " with " + effect.duration / 20 + " seconds left.");
                  }
               }
            }
            iEntity.add(entity);
         }
      }
      for (StationarySpellObj spell : p.getStationary())
      {
         if (spell.isInside(location) && !iSpell.contains(spell))
         {
            if (spell instanceof COLLOPORTUS)
            {
               player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + spell.name.toString() + " of radius " + spell.radius + " has " + spell.duration / 1200 + " power left.");
            }
            else if (spell instanceof HORCRUX)
            {
               player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + spell.name.toString() + " of player " + Bukkit.getPlayer(spell.getPlayerUUID()).getName() + " of radius " + spell.radius);
            }
            else if (spell instanceof ALIQUAM_FLOO)
            {
               player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + "Floo registration of " + ((ALIQUAM_FLOO) spell).getFlooName());
            }
            else if (spell instanceof HARMONIA_NECTERE_PASSUS)
            {
               player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + "Vanishing Cabinet");
            }
            else
            {
               player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + spell.name.toString() + " of radius " + spell.radius + " has " + spell.duration / 20 + " seconds left.");
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
         player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + "There will be " + weather + " for " + weatherTime / 20 + " more seconds.");
         if (thunder)
         {
            player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor")) + "There will be thunder for " + thunderTime / 20 + " more seconds.");
         }
      }
      if (lifeTicks > lifeTime)
      {
         kill();
      }
   }
}