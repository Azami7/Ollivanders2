package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Sets fire to blocks. Also sets fire to living entities and items for an amount of time depending on the player's
 * spell level.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class INCENDIO extends IncendioSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCENDIO ()
   {
      super();

      flavorText.add("The Fire-Making Charm");
      flavorText.add("The ability to produce fire with the flick or a wand can be dangerous to your fellow students (and worse, your books).");
      flavorText.add("From lighting a warm hearth to igniting a Christmas pudding, the Fire-Making Spell is always useful around the wizarding household.");
      text = "Will set alight blocks and entities it passes by.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public INCENDIO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      strafe = false;
      radius = 1;
      blockRadius = 1;
      distance = 1;
      duration = 1;
   }
}