package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

/**
 * The Forgetfulness Potion is a potion which causes a degree of memory loss in the drinker.
 *
 * <p>Reference: http://harrypotter.wikia.com/wiki/Forgetfulness_Potion</p>
 *
 * @author Azami7
 * @since 2.2.7
 */
public final class FORGETFULLNESS_POTION extends O2Potion {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public FORGETFULLNESS_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.FORGETFULLNESS_POTION;

        ingredients.put(O2ItemType.MISTLETOE_BERRIES, 4);
        ingredients.put(O2ItemType.VALERIAN_SPRIGS, 2);
        ingredients.put(O2ItemType.LETHE_RIVER_WATER, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        text = "The Forgetfulness Potion is a potion which causes a degree of memory loss in the drinker.";
        flavorText.add("Hermione Granger: \"What are the three most crucial ingredients in a Forgetfulness Potion?\"\nRon Weasley: \"I forgot.\"");

        potionColor = Color.fromRGB(195, 71, 0);
    }

    /**
     * Drink this potion and do effects
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        O2Player o2p = p.getO2Player(player);
        if (o2p == null) {
            player.sendMessage(Ollivanders2.chatColor + "Nothing seems to happen.");
            return;
        }

        int coinToss = Math.abs(Ollivanders2Common.random.nextInt() % 2);

        int memLoss = Math.abs(Ollivanders2Common.random.nextInt() % 20) + 1;

        String lostSpell = "";

        if (coinToss > 0) {
            Map<O2SpellType, Integer> knownSpells = o2p.getKnownSpells();
            if (knownSpells.size() > 0) {
                Set<O2SpellType> keySet = knownSpells.keySet();
                ArrayList<O2SpellType> listOfSpells = new ArrayList<>(keySet);
                int index = Math.abs(Ollivanders2Common.random.nextInt() % listOfSpells.size());

                // spell to affect
                O2SpellType spell = listOfSpells.get(index);

                // decrease their skill level
                int curLevel = o2p.getSpellCount(spell);
                o2p.setSpellCount(spell, curLevel - memLoss);
                lostSpell = listOfSpells.get(index).toString();
            }
        }
        else {
            Map<O2PotionType, Integer> knownPotions = o2p.getKnownPotions();
            if (knownPotions.size() > 0) {
                Set<O2PotionType> keySet = knownPotions.keySet();
                ArrayList<O2PotionType> listOfPotions = new ArrayList<>(keySet);
                int index = Math.abs(Ollivanders2Common.random.nextInt() % listOfPotions.size());

                // potion to affect
                O2PotionType potion = listOfPotions.get(index);

                // decrease their skill level
                int curLevel = o2p.getPotionCount(potion);
                o2p.setPotionCount(potion, curLevel - memLoss);
                lostSpell = listOfPotions.get(index).toString();
            }
        }

        common.printDebugMessage("Forgetfullness Potion: " + player.getDisplayName() + " lost " + memLoss + " experience  with " + lostSpell, null, null, false);

        player.sendMessage(Ollivanders2.chatColor + "It feels like you've forgotten something.");
    }
}