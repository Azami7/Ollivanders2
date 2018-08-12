package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Doubles spell experience gained when casting spells.
 *
 * @author Azami7
 * @author cakenggt
 */
public final class MEMORY_POTION extends O2Potion
{
   public MEMORY_POTION (Ollivanders2 plugin)
   {
      super(plugin);

      ingredients.put(Material.SUGAR_CANE, 3);
      ingredients.put(Material.GLOWSTONE_DUST, 2);
      ingredients.put(Material.FEATHER, 2);
      ingredients.put(Material.BAKED_POTATO, 1);
      ingredients.put(Material.SUGAR, 2);


      name = "Memory Potion";
      text = "This potion improves the drinker's memory. All spell experience is doubled." + getIngredientsText();
   }

   public void drink (O2Player o2p, Player player)
   {
      if (!extendEffect(o2p))
      {
         o2p.addEffect(new net.pottercraft.Ollivanders2.Effect.MEMORY_POTION(p, O2EffectType.MEMORY_POTION, duration));
      }

      player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))
            + "You feel more alert.");
   }
}
