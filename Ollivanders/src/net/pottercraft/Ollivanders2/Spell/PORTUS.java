package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.NULLUM_APPAREBIT;
import net.pottercraft.Ollivanders2.StationarySpell.NULLUM_EVANESCUNT;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

/**
 * Creates a port key.
 */
public final class PORTUS extends Charms
{
   private final String[] wordsArray;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PORTUS ()
   {
      super();

      flavorText.add("For a moment the kettle trembled, glowing with an odd blue light; then it quivered to rest, as solidly black as ever.");
      flavorText.add("Almost any inanimate object can be turned into a Portkey. Once bewitched, the object will transport anyone who grasps it to a pre-arranged destination.");
      text = "Portus is a spell which creates a portkey. To cast it, hold a wand in your hand "
            + "and look directly at the item you wish to enchant. Then say 'Portus x y z', where x y and z are the coordinates "
            + "you wish the portkey to link to. When this item is picked up, the holder and the entities around them will be "
            + "transported to the destination. Anti-apparition and anti-disapparition spells will stop this, but only if present "
            + "during the creation of the portkey, and will cause the creation to fail. If the portkey is successfully made, then "
            + "it can be used to go to that location regardless of the spells put on it. A portkey creation will not fail if the "
            + "caster of the protective enchantments is the portkey maker. Portkeys can be used to cross worlds as well, if you use "
            + "a portkey which was made in a different world. If the enchantment is said incorrectly, then the portkey will be created "
            + "linking to the caster's current location.";

      wordsArray = null;
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    * @param wordsArray
    */
   public PORTUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand, String[] wordsArray)
   {
      super(plugin, player, name, rightWand);
      this.wordsArray = wordsArray;
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public PORTUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      Location loc = player.getLocation();
      wordsArray = new String[4];
      wordsArray[0] = "portus";
      wordsArray[1] = Double.toString(((int) loc.getX()));
      wordsArray[2] = Double.toString(((int) loc.getY()));
      wordsArray[3] = Double.toString(((int) loc.getZ()));
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (Item item : getItems(1))
      {
         boolean canApparateOut = true;
         for (StationarySpellObj stat : p.getStationary())
         {
            if (stat instanceof NULLUM_EVANESCUNT && stat.isInside(player.getLocation()) && stat.active
                  && !stat.getPlayerUUID().equals(player.getUniqueId()))
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
               }
               catch (NumberFormatException e)
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
               if (stat instanceof NULLUM_APPAREBIT && stat.isInside(to) && stat.active && !stat.getPlayerUUID().equals(player.getUniqueId()))
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