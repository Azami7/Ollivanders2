package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Repairs an itemstack you aim it at.
 *
 * @author lownes
 * @author Azami7
 */
public final class REPARO extends Charms
{
   public O2SpellType spellType = O2SpellType.REPARO;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("The Mending Charm");
      add("Mr. Weasley took Harry's glasses, gave them a tap of his wand and returned them, good as new.");
      add("The Mending Charm will repair broken objects with a flick of the wand.  Accidents do happen, so it is essential to know how to mend our errors.");
   }};

   protected String text = "Repair the durability of a tool.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public REPARO () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public REPARO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<Item> items = getItems(1);
      for (Item item : items)
      {
         ItemStack stack = item.getItemStack();
         int dur = stack.getDurability();
         dur -= usesModifier * usesModifier;
         if (dur < 0)
         {
            dur = 0;
         }
         stack.setDurability((short) dur);
         item.setItemStack(stack);
         kill();
      }
   }
}