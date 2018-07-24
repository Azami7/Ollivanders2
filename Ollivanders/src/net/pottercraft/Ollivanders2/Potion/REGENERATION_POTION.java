package net.pottercraft.Ollivanders2.Potion;

import org.bukkit.Material;

/**
 * The regeneration potion heals a player.
 *
 * @author Azami7
 */
public final class REGENERATION_POTION extends Potion
{
   public REGENERATION_POTION ()
   {
      name = "Regeneration Potion";
      text = "This potion will heal a player.";
      flavorText.add("\"Bone of the father, unknowingly given, you will renew your son! Flesh of the servant, willingly sacrificed, you will revive your master. Blood of the enemy, forcibly taken, you will resurrect your foe.\" -Peter Pettigrew");

      ingredients.put(Material.BONE, 1);
      ingredients.put(Material.SPIDER_EYE, 1);
      ingredients.put(Material.SULPHUR, 1);
      ingredients.put(Material.ROTTEN_FLESH, 1);
      ingredients.put(Material.ENDER_PEARL, 1);
   }
}
