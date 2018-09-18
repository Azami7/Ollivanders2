package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

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
   public BOMBARDA ()
   {
      super();

      spellType = O2SpellType.BOMBARDA;

      flavorText = new ArrayList<String>() {{
         add("\"Bombarda?\"\n\"And wake up everyone in Hogwarts?\" -Albus Potter and Scorpius Malfoy");
         add("An explosion incantation.");
      }};

      text = "Bombarda creates an explosion which doesn't damage the terrain.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public BOMBARDA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.BOMBARDA;

      strength = 0.8;
   }
}