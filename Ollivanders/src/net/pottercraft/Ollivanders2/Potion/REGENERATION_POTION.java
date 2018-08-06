package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * The regeneration potion restores a player's experience after death if deathExpLoss is enabled.
 *
 * @author Azami7
 */
public final class REGENERATION_POTION extends O2Potion
{
   public REGENERATION_POTION (Ollivanders2 plugin)
   {
      super(plugin);

      ingredients.put(Material.BONE, 1);
      ingredients.put(Material.SPIDER_EYE, 1);
      ingredients.put(Material.SULPHUR, 1);
      ingredients.put(Material.ROTTEN_FLESH, 1);
      ingredients.put(Material.ENDER_PEARL, 1);
      ingredients.put(Material.SUGAR, 4);

      name = "Regeneration Potion";
      text = "This potion will heal a player." + getIngredientsText();
      flavorText.add("\"Bone of the father, unknowingly given, you will renew your son! Flesh of the servant, willingly sacrificed, you will revive your master. Blood of the enemy, forcibly taken, you will resurrect your foe.\" -Peter Pettigrew");

      effect = new PotionEffect(PotionEffectType.REGENERATION, duration, 1);
      potionColor = Color.WHITE;
   }

   public void drink (O2Player o2p, Player player) { }
}
