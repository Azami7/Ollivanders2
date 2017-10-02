package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Turn target entity in to a Silverfish.
 *
 * @author lownes
 * @author Azami7
 */
public final class ENTOMORPHIS extends MetatrepoSuper
{
   public ENTOMORPHIS ()
   {
      super();

      flavorText.add("What wouldn't he give to strike now, to jinx Dudley so thoroughly he'd have to crawl home like an insect, struck dumb, sprouting feelers...");
      flavorText.add("The Insect Jinx");
      text = "Entomorphis will transfigure an entity into a silverfish a duration dependent on your experience. If it hits a stone brick, cobblestone, or chiseled stone, it will turn that into a silverfish monster egg.";
   }

   public ENTOMORPHIS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      animalShape = EntityType.SILVERFISH;
   }
}