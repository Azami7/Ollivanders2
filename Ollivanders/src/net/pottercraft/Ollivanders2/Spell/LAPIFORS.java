package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.RabbitType;
import me.libraryaddict.disguise.disguisetypes.watchers.RabbitWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Transfigures entity into a rabbit.
 *
 * @since 2.2.3
 * @link https://github.com/Azami7/Ollivanders2/issues/51
 * @author Azami7
 */
public final class LAPIFORS extends FriendlyMobDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LAPIFORS ()
   {
      super();

      flavorText.add("\"Lapifors, the transformation of a small object into a rabbit\" -Hermione Granger");

      text = "The transfiguration spell Lapifors will transfigure an entity into a chicken.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public LAPIFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      targetType = EntityType.RABBIT;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      RabbitWatcher watcher = (RabbitWatcher)disguise.getWatcher();
      watcher.setAdult();

      watcher.setType(LAPIFORS.getRandomRabbitType());
   }

   /**
    * Get a random rabbit type. Odds are 1/60 to get a Killer Bunny.
    *
    * @return
    */
   public static RabbitType getRandomRabbitType ()
   {
      RabbitType type;

      int rand = Math.abs(Ollivanders2.random.nextInt() % 61);

      if (rand < 10)
         type = RabbitType.BROWN;
      else if (rand < 20)
         type = RabbitType.BLACK;
      else if (rand < 30)
         type = RabbitType.WHITE;
      else if (rand < 40)
         type = RabbitType.GOLD;
      else if (rand < 50)
         type = RabbitType.PATCHES;
      else if (rand < 60)
         type = RabbitType.PEPPER;
      else
         type = RabbitType.KILLER_BUNNY;

      return type;
   }
}
