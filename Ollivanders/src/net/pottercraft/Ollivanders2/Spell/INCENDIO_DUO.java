package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Creates a larger incendio that strafes and doubles the radius and duration.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class INCENDIO_DUO extends IncendioSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCENDIO_DUO ()
   {
      super();

      flavorText.add("A Stronger Fire-Making Charm");
      text = "Incendio Duo will burn blocks and entities it passes by. It's radius is twice that of Incendio and it's duration half.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public INCENDIO_DUO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      location.add(vector);
      strafe = true;
      blockRadius = 2;
      radius = 2;
      distance = 2;
      duration = 2;
   }
}