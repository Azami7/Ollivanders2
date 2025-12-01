package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import me.libraryaddict.disguise.disguisetypes.watchers.AgeableWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.HorseWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.LlamaWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.RabbitWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.CatWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.PandaWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.PolarBearWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.CreeperWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.FoxWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.PigWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SheepWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SpiderWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.ShulkerWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.TurtleWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.TraderLlamaWatcher;

import net.pottercraft.ollivanders2.common.O2Color;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2Player;

import org.bukkit.DyeColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Effect that transforms a player into their Animagus animal form.
 *
 * <p>ANIMAGUS_EFFECT is a permanent transformation that disguises a player as their pre-determined animal form
 * (retrieved from the player's O2Player data). The transformation includes:</p>
 * <ul>
 * <li>Visual disguise as the target animal form using LibDisguises</li>
 * <li>Appearance customization based on animal type (colors, variants, special states)</li>
 * <li>Restrictions preventing animal-like behaviors (flight, item interaction, etc.)</li>
 * <li>Age enforcement ensuring the animal appears as an adult</li>
 * </ul>
 *
 * <p>Supported Animal Forms: Cats, Rabbits, Wolves, Horses, Llamas, Pandas, Polar Bears, Creepers, Foxes, Pigs,
 * Sheep, Slimes, Spiders, Shulkers, and Turtles - each with form-specific appearance customization.</p>
 *
 * <p>This effect is permanent and cannot be modified via setPermanent(). Use kill() to revert the player to
 * human form.</p>
 *
 * @author Azami7
 */
public class ANIMAGUS_EFFECT extends ShapeShiftSuper {
    /**
     * The color or type variant for the animal form.
     *
     * <p>This value is retrieved from the player's O2Player data and specifies appearance variations for the
     * animal form (e.g., Cat.Type, DyeColor for wool, Horse.Color, etc.). It is used during customizeWatcher()
     * to set type-specific appearance properties. If parsing fails, a corrected variant is stored back to the
     * player's O2Player data.</p>
     */
    String colorVariant;

    /**
     * Constructor for creating an Animagus transformation effect.
     *
     * <p>Creates a permanent transformation effect that disguises the player as their pre-determined animal form.
     * The constructor retrieves the animal form and color variant from the target player's O2Player data. If the
     * O2Player cannot be found or the form is null, the effect is killed.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    not used - animagus effect is always permanent
     * @param isPermanent ignored - animagus effect is always permanent
     * @param pid         the unique ID of the player to transform
     */
    public ANIMAGUS_EFFECT(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);

        effectType = O2EffectType.ANIMAGUS_EFFECT;
        checkDurationBounds();

        transformed = false;

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(targetID);
        if (o2p == null) {
            common.printDebugMessage("o2player cannot be found", null, null, false);
            kill();
            return;
        }

        form = o2p.getAnimagusForm();
        colorVariant = o2p.getAnimagusColor();

        if (form == null) {
            common.printDebugMessage("Unable to get animagus form for " + Ollivanders2API.getPlayers().getPlayer(pid).getPlayerName(), null, null, false);
            kill();
        }
    }

    /**
     * Transform the player on first tick if not already transformed.
     *
     * <p>This template method defers the actual transformation to the first upkeep() call rather than executing
     * in the constructor. This ensures the player entity is fully initialized before the LibDisguises disguise
     * is applied.</p>
     */
    @Override
    protected void upkeep() {
        if (!transformed && !kill) {
            transform();
        }
    }

    /**
     * Customize the animal form's appearance based on type and stored color variant.
     *
     * <p>This method applies type-specific appearance customizations:</p>
     * <ul>
     * <li>Color/Type Variants: Parses and applies the stored colorVariant to entity types that support variations
     *     (Cat.Type, Rabbit.Type, DyeColor for wolves/sheep/shulkers, Horse.Color, Llama.Color, Fox.Type)</li>
     * <li>Special Properties: Sets safe defaults for special entity states (e.g., creeper ignited=false, fox sitting=false,
     *     panda main gene=normal)</li>
     * <li>Age Enforcement: Sets all ageable animals to adult form</li>
     * <li>Correction: If color variant parsing fails, stores the corrected variant back to the player's O2Player data</li>
     * </ul>
     */
    @Override
    protected void customizeWatcher() {
        // in case the variant doesn't work, this can be used to fix the value at the end of this method
        String correctedVariant = null;
        if (watcher == null)
            return;

        if (watcher instanceof CatWatcher) {
            CatWatcher catWatcher = (CatWatcher) watcher;
            Cat.Type color = Cat.Type.WHITE;

            try {
                color = Cat.Type.valueOf(colorVariant);
            }
            catch (Exception e) {
                common.printDebugMessage("Failed to parse Cat.Type " + colorVariant, e, null, false);
                correctedVariant = color.toString();
            }

            catWatcher.setType(color);
            catWatcher.setCollarColor(O2Color.getRandomPrimaryDyeableColor().getDyeColor());
        }
        else if (watcher instanceof RabbitWatcher) {
            Rabbit.Type color = Rabbit.Type.WHITE;

            try {
                color = Rabbit.Type.valueOf(colorVariant);
            }
            catch (Exception e) {
                common.printDebugMessage("Failed to parse Rabbit.Type " + colorVariant, e, null, false);
                correctedVariant = color.toString();
            }

            ((RabbitWatcher) watcher).setType(color);
        }
        else if (watcher instanceof WolfWatcher) {
            DyeColor color = DyeColor.WHITE;

            try {
                color = DyeColor.valueOf(colorVariant);
            }
            catch (Exception e) {
                common.printDebugMessage("Failed to parse DyeColor " + colorVariant, e, null, false);
                correctedVariant = color.toString();
            }

            ((WolfWatcher) watcher).isTamed();
            ((WolfWatcher) watcher).setCollarColor(color);
        }
        else if (watcher instanceof HorseWatcher) {
            Horse.Color color = Horse.Color.WHITE;

            try {
                color = Horse.Color.valueOf(colorVariant);
            }
            catch (Exception e) {
                common.printDebugMessage("Failed to parse Horse.Color " + colorVariant, null, null, false);
                correctedVariant = color.toString();
            }

            ((HorseWatcher) watcher).setColor(color);
            ((HorseWatcher) watcher).setStyle(Horse.Style.NONE);
        }
        else if (watcher instanceof LlamaWatcher || watcher instanceof TraderLlamaWatcher) {
            Llama.Color color = Llama.Color.WHITE;

            try {
                color = Llama.Color.valueOf(colorVariant);
            }
            catch (Exception e) {
                common.printDebugMessage("Failed to parse Llama.Color " + colorVariant, e, null, false);
                correctedVariant = color.toString();
            }

            if (watcher instanceof LlamaWatcher)
                ((LlamaWatcher) watcher).setColor(color);
            else
                ((TraderLlamaWatcher) watcher).setColor(color);
        }
        else if (watcher instanceof PandaWatcher) {
            ((PandaWatcher) watcher).setMainGene(Panda.Gene.NORMAL);
            ((PandaWatcher) watcher).setSitting(false);
        }
        else if (watcher instanceof PolarBearWatcher) {
            ((PolarBearWatcher) watcher).setStanding(false);
        }
        else if (watcher instanceof CreeperWatcher) {
            ((CreeperWatcher) watcher).setIgnited(false);
            ((CreeperWatcher) watcher).setPowered(false);
        }
        else if (watcher instanceof FoxWatcher) {
            Fox.Type color = Fox.Type.RED;

            try {
                color = Fox.Type.valueOf(colorVariant);
            }
            catch (Exception e) {
                common.printDebugMessage("Failed to parse Fox.Type " + colorVariant, e, null, false);
                correctedVariant = color.toString();
            }

            ((FoxWatcher) watcher).setType(color);
            ((FoxWatcher) watcher).setSitting(false);
        }
        else if (watcher instanceof PigWatcher) {
            ((PigWatcher) watcher).setSaddled(false);
        }
        else if (watcher instanceof SheepWatcher) {
            DyeColor color = DyeColor.WHITE;

            try {
                color = DyeColor.valueOf(colorVariant);
            }
            catch (Exception e) {
                common.printDebugMessage("Failed to parse DyeColor " + colorVariant, e, null, false);
                correctedVariant = color.toString();
            }

            ((SheepWatcher) watcher).setColor(color);
        }
        else if (watcher instanceof SlimeWatcher) {
            ((SlimeWatcher) watcher).setSize(1);
        }
        else if (watcher instanceof SpiderWatcher) {
            ((SpiderWatcher) watcher).setClimbing(false);
        }
        else if (watcher instanceof ShulkerWatcher) {
            DyeColor color = DyeColor.WHITE;

            try {
                color = DyeColor.valueOf(colorVariant);
            }
            catch (Exception e) {
                common.printDebugMessage("Failed to parse DyeColor " + colorVariant, e, null, false);
                correctedVariant = color.toString();
            }

            ((ShulkerWatcher) watcher).setColor(color);
        }
        else if (watcher instanceof TurtleWatcher) {
            ((TurtleWatcher) watcher).setEgg(false);
        }

        if (watcher instanceof AgeableWatcher) {
            ((AgeableWatcher) watcher).setAdult();
        }

        // fix player's animagus color variant if needed
        if (correctedVariant != null) {
            Ollivanders2API.getPlayers().fixPlayerAnimagusColorVariant(targetID, correctedVariant);
        }
    }

    /**
     * Override to prevent external modification of permanent status.
     *
     * <p>This effect must remain permanent throughout the player's session. Use kill() to revert the player to
     * human form instead.</p>
     *
     * @param perm ignored - effect is always permanent
     */
    @Override
    public void setPermanent(boolean perm) {
    }

    /**
     * Perform cleanup when this Animagus effect is removed.
     *
     * <p>The default implementation does nothing, as animal form cleanup is handled by the LibDisguises removal
     * in the parent ShapeShiftSuper.restore() method.</p>
     */
    @Override
    public void doRemove() {
    }

    /**
     * Prevent block interaction while transformed into animal form.
     *
     * <p>Animals cannot interact with blocks, so block click events are cancelled to prevent unintended
     * block manipulation while in animal form.</p>
     *
     * @param event the player interact event
     */
    @Override
    void doOnPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            common.printDebugMessage("ANIMAGUS_EFFECT: cancelling PlayerInteractEvent", null, null, false);
        }
    }

    /**
     * Prevent flight while transformed into an animal form.
     *
     * <p>Most animal forms cannot naturally fly, so flight toggle attempts are cancelled. This prevents the
     * transformed player from using flight abilities that would be inconsistent with animal form.</p>
     *
     * @param event the player toggle flight event
     */
    @Override
    void doOnPlayerToggleFlightEvent(@NotNull PlayerToggleFlightEvent event) {
        if (event.isFlying()) {
            event.setCancelled(true);
            common.printDebugMessage("ANIMAGUS_EFFECT: cancelling PlayerToggleFlightEvent", null, null, false);
        }
    }

    /**
     * Prevent item pickup while transformed into animal form.
     *
     * <p>Animals cannot interact with items in their inventory, so item pickup attempts are cancelled.</p>
     *
     * @param event the entity item pickup event
     */
    @Override
    void doOnPlayerPickupItemEvent(@NotNull EntityPickupItemEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("ANIMAGUS_EFFECT: cancelling cancelling EntityPickupItemEvent", null, null, false);
    }

    /**
     * Prevent item selection while transformed into animal form.
     *
     * <p>Animals cannot hold or select items, so item held events are cancelled.</p>
     *
     * @param event the player item held event
     */
    @Override
    void doOnPlayerItemHeldEvent(@NotNull PlayerItemHeldEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("ANIMAGUS_EFFECT: cancelling PlayerItemHeldEvent", null, null, false);

    }

    /**
     * Prevent item consumption while transformed into animal form.
     *
     * <p>Animals cannot consume items, so item consumption attempts are cancelled.</p>
     *
     * @param event the player item consume event
     */
    @Override
    void doOnPlayerItemConsumeEvent(@NotNull PlayerItemConsumeEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("ANIMAGUS_EFFECT: cancelling PlayerItemConsumeEvent", null, null, false);
    }

    /**
     * Prevent item dropping while transformed into animal form.
     *
     * <p>Animals cannot drop items, so item drop events are cancelled.</p>
     *
     * @param event the player drop item event
     */
    @Override
    void doOnPlayerDropItemEvent(@NotNull PlayerDropItemEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("ANIMAGUS_EFFECT: cancelling PlayerDropItemEvent", null, null, false);
    }
}