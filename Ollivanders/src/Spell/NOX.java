package Spell;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by Azami7 on 6/29/17.
 *
 * Cancels the effect of the LUMOS spell or removes the effect of a Night Vision potion.
 *
 * @author lownes
 */
public class NOX extends SpellProjectile implements Spell
{
   public NOX(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect() {
      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
      kill();
   }
}
