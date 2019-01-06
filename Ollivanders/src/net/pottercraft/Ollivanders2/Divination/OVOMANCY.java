package net.pottercraft.Ollivanders2.Divination;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

public class OVOMANCY extends O2Divination
{
    public OVOMANCY (Ollivanders2 plugin, Player pro, Player tar, Integer exp)
    {
        super(plugin, pro, tar, exp);

        divintationType = O2DivinationType.OVOMANCY;
        maxAccuracy = 40;

        prophecyPrefix.add("The shape of the egg whites means that");
        prophecyPrefix.add("Through the teachings of Orpheus, it is foretold that");
        prophecyPrefix.add("The omen of the egg reveals that");
    }
}
