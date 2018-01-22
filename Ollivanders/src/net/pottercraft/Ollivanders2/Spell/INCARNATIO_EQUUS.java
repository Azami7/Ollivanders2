package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.HorseWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turn target player in to a horse.
 *
 * @since 2.2.3
 * @author Azami7
 */
public final class INCARNATIO_EQUUS extends PlayerDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_EQUUS ()
   {
      super();

      text = "Turns target player in to a horse.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public INCARNATIO_EQUUS(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      targetType = EntityType.HORSE;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      HorseWatcher watcher = (HorseWatcher)disguise.getWatcher();
      watcher.setAdult();

      // randomize appearance
      watcher.setStyle(INCARNATIO_EQUUS.getRandomHorseStyle());
      watcher.setColor(INCARNATIO_EQUUS.getRandomHorseColor());
   }

   /**
    * Get a random horse style.
    *
    * @return
    */
   public static Horse.Style getRandomHorseStyle ()
   {
      Horse.Style style;

      int rand = Math.abs(Ollivanders2.random.nextInt() % 20);
      if (rand == 0)
         style = Horse.Style.BLACK_DOTS;
      else if (rand == 1)
         style = Horse.Style.WHITE;
      else if (rand == 2)
         style = Horse.Style.WHITE_DOTS;
      else if (rand == 3)
         style = Horse.Style.WHITEFIELD;
      else
         style = Horse.Style.NONE;

      return style;
   }

   /**
    * Get a random horse color.
    *
    * @return
    */
   public static Horse.Color getRandomHorseColor ()
   {
      Horse.Color color;

      int rand = Math.abs(Ollivanders2.random.nextInt() % 7);
      if (rand == 0)
         color = Horse.Color.BLACK;
      else if (rand == 1)
         color = Horse.Color.BROWN;
      else if (rand == 2)
         color = Horse.Color.CHESTNUT;
      else if (rand == 3)
         color = Horse.Color.CREAMY;
      else if (rand == 4)
         color = Horse.Color.DARK_BROWN;
      else if (rand == 5)
         color = Horse.Color.GRAY;
      else
         color = Horse.Color.WHITE;

      return color;
   }
}
