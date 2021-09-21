package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT;
import net.pottercraft.ollivanders2.stationaryspell.NULLUM_EVANESCUNT;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

/**
 * Creates a port key.
 */
public final class PORTUS extends O2Spell
{
   private final String[] wordsArray;

   public static final String portus = "Portkey";
   private Location toLoc;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public PORTUS(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.PORTUS;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("For a moment the kettle trembled, glowing with an odd blue light; then it quivered to rest, as solidly black as ever.");
         add("Almost any inanimate object can be turned into a Portkey. Once bewitched, the object will transport anyone who grasps it to a pre-arranged destination.");
      }};

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
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PORTUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand, @NotNull String[] wordsArray)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PORTUS;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      this.wordsArray = wordsArray;
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PORTUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PORTUS;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      Location loc = player.getLocation();
      wordsArray = new String[4];
      wordsArray[0] = "portus";
      wordsArray[1] = Double.toString(((int) loc.getX()));
      wordsArray[2] = Double.toString(((int) loc.getY()));
      wordsArray[3] = Double.toString(((int) loc.getZ()));
   }

   @Override
   protected void doInitSpell()
   {
      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.BUILD);
         worldGuardFlags.add(Flags.INTERACT);
      }
   }

   @Override
   protected void doCheckEffect()
   {
      if (toLoc == null)
         setDestinationLocation();

      for (Item item : getItems(1.5))
      {
         if (!checkPermissions(item.getLocation(), toLoc))
         {
            continue;
         }

         // update item meta
         ItemMeta meta = item.getItemStack().getItemMeta();

         if (meta == null)
            continue;

         List<String> lore = null;
         if (meta.hasLore())
            lore = meta.getLore();

         if (lore == null)
            lore = new ArrayList<>();

         World world = toLoc.getWorld();
         if (world == null)
            continue;

         lore.add(portus + " " + world.getUID() + " " + (toLoc.getX()) + " " + (toLoc.getY()) + " " + (toLoc.getZ()));
         meta.setLore(lore);
         item.getItemStack().setItemMeta(meta);

         kill();
      }

      if (hasHitTarget())
      {
         kill();
      }
   }

   private void setDestinationLocation()
   {
      if (wordsArray.length == 4)
      {
         try
         {
            toLoc = new Location(player.getWorld(),
                  Double.parseDouble(wordsArray[1]),
                  Double.parseDouble(wordsArray[2]),
                  Double.parseDouble(wordsArray[3]));
         }
         catch (NumberFormatException e)
         {
            toLoc = player.getLocation().clone();
         }
      } else
      {
         toLoc = player.getLocation().clone();
      }
   }

   private boolean checkPermissions(@NotNull Location fromLoc, @NotNull Location toLoc)
   {
      // can you apparate out of this location?
      boolean canApparateOut = true;
      for (O2StationarySpell stat : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
      {
         if (stat instanceof NULLUM_EVANESCUNT && stat.isInside(fromLoc)
                 && !stat.getCasterID().equals(player.getUniqueId()))
         {
            stat.flair(10);
            canApparateOut = false;
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
         }
      }
      if (player.isPermissionSet("Ollivanders2.BYPASS"))
      {
         if (player.hasPermission("Ollivanders2.BYPASS"))
         {
            canApparateOut = true;
         }
      }

      if (!canApparateOut)
         return false;

      boolean canApparateIn = true;
      for (O2StationarySpell stat : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
      {
         if (stat instanceof NULLUM_APPAREBIT && stat.isInside(toLoc) && !stat.getCasterID().equals(player.getUniqueId()))
         {
            stat.flair(10);
            canApparateIn = false;
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
         }
      }
      if (player.isPermissionSet("Ollivanders2.BYPASS"))
      {
         if (player.hasPermission("Ollivanders2.BYPASS"))
         {
            canApparateIn = true;
         }
      }

      return canApparateIn;
   }
}