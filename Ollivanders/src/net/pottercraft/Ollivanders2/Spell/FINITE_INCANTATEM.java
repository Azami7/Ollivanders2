package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Reduces any spell effects on an item.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class FINITE_INCANTATEM extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FINITE_INCANTATEM ()
   {
      super();

      spellType = O2SpellType.FINITE_INCANTATEM;

      flavorText = new ArrayList<String>() {{
         add("\"He pointed his wand at the rampart, cried, \"Finite!\" and it steadied.\"");
         add("\"Try Finite Incantatem, that should stop the rain if it’s a hex or curse.\"  -Hermione Granger");
         add("\"Stop! Stop!\" screamed Lockhart, but Snape took charge. \"Finite Incantatum!\" he shouted; Harry's feet stopped dancing, Malfoy stopped laughing, and they were able to look up.");
         add("The General Counter-Spell");
      }};

      text = "Reduces all spell effects on an item or player.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FINITE_INCANTATEM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.FINITE_INCANTATEM;
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(2))
      {
         if (live instanceof Player)
         {
            Player ply = (Player) live;

            p.players.playerEffects.ageAllEffects(ply.getUniqueId(), (int)(usesModifier * 1200));

            kill();
            return;
         }
      }
      for (Item item : getItems(1))
      {
         ItemStack stack = item.getItemStack();
         ItemMeta meta = stack.getItemMeta();
         if (meta.hasLore())
         {
            List<String> lore = meta.getLore();
            ArrayList<String> newLore = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++)
            {
               String string = lore.get(i);
               if (string.contains("Geminio "))
               {
                  String[] loreParts = lore.get(i).split(" ");
                  int magnitude = Integer.parseInt(loreParts[1]);
                  magnitude -= (int) usesModifier;
                  if (magnitude > 0)
                  {
                     newLore.add("Geminio " + magnitude);
                  }
               }
               else if (string.contains("Flagrante "))
               {
                  String[] loreParts = lore.get(i).split(" ");
                  int magnitude = Integer.parseInt(loreParts[1]);
                  magnitude -= (int) usesModifier;
                  if (magnitude > 0)
                  {
                     newLore.add("Flagrante " + magnitude);
                  }
               }
               else if (string.contains("Portkey "))
               {
                  //remove the portkey by not adding it to newLore
               }
               else
               {
                  newLore.add(lore.get(i));
               }
            }
            meta.setLore(newLore);
            stack.setItemMeta(meta);
            item.setItemStack(stack);
         }
         kill();
      }
   }
}