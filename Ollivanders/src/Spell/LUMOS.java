package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Gives nightvision for an amount of time depending on the player's
 * spell level.
 *
 * @author lownes
 */
public class LUMOS extends SpellProjectile implements Spell
{
   public LUMOS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, (int) (usesModifier * 1200), 1), true);
      kill();
   }
}