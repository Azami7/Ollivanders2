package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Disarms an entity of it's held item, flinging the item in the direction of the caster with force determined by the spell level.
 *
 * @author lownes
 * @author Azami7
 */
public final class EXPELLIARMUS extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EXPELLIARMUS ()
   {
      super();

      spellType = O2SpellType.EXPELLIARMUS;

      flavorText = new ArrayList<String>() {{
         add("The Disarming Charm");
         add("They made the most of the last few hours in which they were allowed to do magic before the holidays... and practised disarming each other by magic. Harry was getting very good at it.");
         add("A handy (even life-saving) spell for removing an object from an enemy’s grasp.");
         add("\"Expelliarmus is a useful spell, Harry, but the Death Eaters seem to think it is your signature move, and I urge you not to let it become so!” -Remus Lupin");
         add("The Disarming Charm lies at the heart of a good duelling technique. It allows the duelist to rebound an opponent's spell in the hope that the rebounded spell will strike the opponent and leave him or her vulnerable to further attack.");
      }};

      text = "Item held by an entity is flung a distance.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public EXPELLIARMUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.EXPELLIARMUS;
      setUsesModifier();
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity entity : getLivingEntities(1.5))
      {
         if (entity.getUniqueId() == player.getUniqueId())
            continue;

         ItemStack itemInHand = entity.getEquipment().getItemInMainHand().clone();
         if (itemInHand.getType() != Material.AIR)
         {
            if (holdsWand(entity))
            {
               allyWand(itemInHand);
            }
            entity.getEquipment().setItemInMainHand(null);
            Item item = entity.getWorld().dropItem(entity.getEyeLocation(), itemInHand);
            item.setVelocity(player.getEyeLocation().toVector().subtract(item.getLocation().toVector()).normalize().multiply(usesModifier / 10));
         }
         kill();
         return;
      }
   }

   /**
    * Does the player hold a wand item?
    *
    * @param entity - Player to check.
    * @return True if the player holds a wand. False if not.
    */
   public boolean holdsWand (LivingEntity entity)
   {
      return p.common.isWand(entity.getEquipment().getItemInMainHand());
   }

   /**
    * Modifies the ItemStack, allying it to the disarming player.
    *
    * @param wand - ItemStack that may be a wand.
    */
   private void allyWand (ItemStack wand)
   {
      ItemMeta wandMeta = wand.getItemMeta();
      List<String> wandLore = wandMeta.getLore();
      if (wandLore.size() == 1)
      {
         wandLore.add(player.getUniqueId().toString());
         wandMeta.setLore(wandLore);
         wand.setItemMeta(wandMeta);
      }
      else
      {
         wandLore.set(1, player.getUniqueId().toString());
      }
      wandMeta.setLore(wandLore);
      wand.setItemMeta(wandMeta);
   }
}