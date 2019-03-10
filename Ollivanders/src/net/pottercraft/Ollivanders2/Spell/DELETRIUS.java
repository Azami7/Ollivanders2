package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

/**
 * Deletes an item entity.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class DELETRIUS extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DELETRIUS ()
   {
      super();

      spellType = O2SpellType.DELETRIUS;

      flavorText = new ArrayList<String>() {{
         add("The Eradication Spell");
         add("'Deletrius!' Mr Diggory shouted, and the smoky skull vanished in a wisp of smoke.");
      }};

      text = "Cause an item entity to stop existing.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public DELETRIUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.DELETRIUS;
      setUsesModifier();

      worldGuardFlags.add(DefaultFlag.ITEM_PICKUP);

      projectilePassThrough.add(Material.FIRE);
      projectilePassThrough.add(Material.WATER);
   }

   /**
    * Delete a item
    */
   @Override
   protected void doCheckEffect ()
   {
      List<Item> items = getItems(1.5);

      if (items.size() > 0)
      {
         Item item = items.get(0);

         item.remove();
         kill();
      }
   }
}