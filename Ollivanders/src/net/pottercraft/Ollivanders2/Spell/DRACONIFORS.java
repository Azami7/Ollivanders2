package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Turn target entity in to a Dragon. OllivandersPlayerListener.draconiforsBlockChange()
 * keeps any transfigured dragons from destroying terrain.
 *
 * @see MetatrepoSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class DRACONIFORS extends MetatrepoSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DRACONIFORS ()
   {
      super();

      flavorText.add("The Draconifors Transfiguration");
      flavorText.add("\"It was great! Now I can turn anything into dragons!\" -Hermione Granger");
      text = "Turn an entity in to a dragon.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public DRACONIFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      animalShape = EntityType.ENDER_DRAGON;
   }
}