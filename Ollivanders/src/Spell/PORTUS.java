package Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpellObj;

public class PORTUS extends SpellProjectile implements Spell
{

   private final String[] wordsArray;

   public PORTUS (Ollivanders2 plugin, Player player, Spells name,
                  Double rightWand, String[] wordsArray)
   {
      super(plugin, player, name, rightWand);
      this.wordsArray = wordsArray;
   }

   public PORTUS (Ollivanders2 plugin, Player player, Spells name,
                  Double rightWand)
   {
      super(plugin, player, name, rightWand);
      Location loc = player.getLocation();
      wordsArray = new String[4];
      wordsArray[0] = "portus";
      wordsArray[1] = Double.toString(((int) loc.getX()));
      wordsArray[2] = Double.toString(((int) loc.getY()));
      wordsArray[3] = Double.toString(((int) loc.getZ()));
   }

   public void checkEffect ()
   {
      move();
      for (Item item : getItems(1))
      {
         boolean canApparateOut = true;
         for (StationarySpellObj stat : p.getStationary())
         {
            if (stat instanceof StationarySpell.NULLUM_EVANESCUNT && stat.isInside(player.getLocation()) && stat.active && !stat.getPlayerUUID().equals(player.getUniqueId()))
            {
               stat.flair(10);
               canApparateOut = false;
               player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 1);
            }
         }
         if (player.isPermissionSet("Ollivanders2.BYPASS"))
         {
            if (player.hasPermission("Ollivanders2.BYPASS"))
            {
               canApparateOut = true;
            }
         }
         if (canApparateOut)
         {
            Location to;
            if (wordsArray.length == 4)
            {
               try
               {
                  to = new Location(player.getWorld(),
                        Double.parseDouble(wordsArray[1]),
                        Double.parseDouble(wordsArray[2]),
                        Double.parseDouble(wordsArray[3]));
               } catch (NumberFormatException e)
               {
                  to = player.getLocation().clone();
               }
            }
            else
            {
               to = player.getLocation().clone();
            }
            boolean canApparateIn = true;
            for (StationarySpellObj stat : p.getStationary())
            {
               if (stat instanceof StationarySpell.NULLUM_APPAREBIT && stat.isInside(to) && stat.active && !stat.getPlayerUUID().equals(player.getUniqueId()))
               {
                  stat.flair(10);
                  canApparateIn = false;
                  player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 1);
               }
            }
            if (player.isPermissionSet("Ollivanders2.BYPASS"))
            {
               if (player.hasPermission("Ollivanders2.BYPASS"))
               {
                  canApparateIn = true;
               }
            }
            if (canApparateIn)
            {
               ItemMeta meta = item.getItemStack().getItemMeta();
               List<String> lore;
               if (meta.hasLore())
               {
                  lore = meta.getLore();
               }
               else
               {
                  lore = new ArrayList<String>();
               }
               lore.add("Portkey " + to.getWorld().getUID() + " " + Double.toString(to.getX()) + " " + Double.toString(to.getY()) + " " + Double.toString(to.getZ()));
               meta.setLore(lore);
               item.getItemStack().setItemMeta(meta);
            }
         }
         kill();
      }
   }

}