package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Transfigures an entity into a horse.
 *
 * @author lownes
 * @author Azami7
 */
public final class METATREPO_EQUUS extends MetatrepoSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public METATREPO_EQUUS ()
   {
      super();

      text = "Transforms target entity in to a horse.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public METATREPO_EQUUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      animalShape = EntityType.HORSE;
   }
}