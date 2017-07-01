package Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by Azami7 on 6/28/17.
 *
 * @author Azami7
 */
public class INCARNATIO_EQUUS extends IncarnatioSuper
{
   public INCARNATIO_EQUUS(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      effect = new Effect.INCARNATIO_EQUUS(player, Effects.INCARNATIO_EQUUS, 1);
   }
}
