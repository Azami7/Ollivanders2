package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Vanishes an entity. The entity will reappear after a certain time.
 *
 * @author lownes
 * @author Azami7
 */
public final class EVANESCO extends Transfiguration
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EVANESCO (O2SpellType type)
   {
      super(type);

      flavorText.add("The Vanishing Spell");
      flavorText.add("The contents of Harry’s potion vanished; he was left standing foolishly beside an empty cauldron.");
      text="Evanesco will vanish an entity.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param type
    * @param rightWand
    */
   public EVANESCO (Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, type, rightWand);
   }

   public void checkEffect ()
   {
      simpleTransfigure(null, null);
   }
}