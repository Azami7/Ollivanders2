package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * All spells cast are twice as powerful.
 *
 * @author Azami7
 * @author cakenggt
 */
public final class BARUFFIOS_BRAIN_ELIXIR extends O2Potion
{
   public BARUFFIOS_BRAIN_ELIXIR (Ollivanders2 plugin, O2PotionType potionType)
   {
      super(plugin, potionType);

      ingredients.put(Material.REDSTONE, 5);
      ingredients.put(Material.GOLD_NUGGET, 1);
      ingredients.put(Material.SUGAR, 2);
      ingredients.put(Material.MONSTER_EGG, 1);

      name = "Baruffio's Brain Elixir";
      text = "Baruffio's Brain Elixir is a potion that increases the taker's brain power. All spells cast are twice as "
            + "powerful." + getIngredientsText();

      flavorText.add("\"I've performed tests on the potion sample you collected. My best guess is that it was supposed to be Baruffio's Brain Elixir. Now, that's a potion which doesn't work at the best of times, but whoever brewed this was seriously incompetent! Forget boosting one's brain; this concoction would most likely melt it!\" —Gethsemane Prickle");
   }

   public void drink (O2Player o2p, Player player)
   {
      if (!extendEffect(o2p))
      {
         o2p.addEffect(new net.pottercraft.Ollivanders2.Effect.BARUFFIOS_BRAIN_ELIXIR(p, O2EffectType.BARUFFIOS_BRAIN_ELIXIR, duration));
      }

      player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))
            + "You feel clarity of thought.");
   }
}
