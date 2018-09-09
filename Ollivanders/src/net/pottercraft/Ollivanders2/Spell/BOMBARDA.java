package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Creates an explosion at the target which scales with the player's level in the spell. Doesn't break blocks.
 *
 * @see BombardaSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class BOMBARDA extends BombardaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public BOMBARDA (O2SpellType type)
   {
      super(type);

      flavorText.add("\"Bombarda?\"\n\"And wake up everyone in Hogwarts?\" -Albus Potter and Scorpius Malfoy");
      flavorText.add("An explosion incantation.");
      text = "Bombarda creates an explosion which doesn't damage the terrain.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param type
    * @param rightWand
    */
   public BOMBARDA (Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, type, rightWand);
      strength = 0.8;
   }
}