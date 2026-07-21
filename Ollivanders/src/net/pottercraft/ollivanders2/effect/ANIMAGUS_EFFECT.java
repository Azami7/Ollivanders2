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
 * Permanently disguises a player as their pre-determined Animagus animal form (from their {@link O2Player} data),
 * customizing the form's appearance and blocking behaviors inconsistent with an animal (block interaction, flight,
 * item pickup/hold/consume/drop). Always permanent; use {@link #kill()} to revert to human form.
 *
 * @author Azami7
 * @see ShapeShift
 */
public class ANIMAGUS_EFFECT extends ShapeShift {
    /**
     * The color or type variant for the animal form (e.g. Cat.Type, DyeColor for wool, Horse.Color), from the
     * player's {@link O2Player} data. If it fails to parse, a corrected variant is written back to that data.
     */
    String colorVariant;

    /**
     * Constructor. Reads the animal form and color variant from the target's {@link O2Player} data; kills the effect
     * if that data or the form is missing.
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    ignored - animagus effect is always permanent
     * @param isPermanent ignored - animagus effect is always permanent
     * @param pid         the unique ID of the player to transform
     */
    public ANIMAGUS_EFFECT(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);

        effectType = O2EffectType.ANIMAGUS_EFFECT;
        checkDurationBounds();

        transformed = false;

        // technically possible to return a null but this should never happen
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(targetID);
        if (o2p == null) {
            common.printDebugMessage("o2player cannot be found", null, null, false);
            kill();
            return;
        }

        form = o2p.getAnimagusForm();
        colorVariant = o2p.getAnimagusColor();

        if (form == null) {
            common.printDebugMessage("Unable to get animagus form for " + o2p.getPlayerName(), null, null, false);
            kill();
        }
    }

    /**
     * Transform the player on the first tick, deferred from the constructor so the player entity is fully
     * initialized before the LibDisguises disguise is applied.
     */
    @Override
    protected void doCheckEffect() {
        if (!transformed && !kill) {
            transform();
        }
    }

    /**
     * Apply type-specific appearance to the disguise watcher: the stored {@link #colorVariant} where the form
     * supports variants, safe defaults for special states (e.g. creeper not ignited), and adult age for ageables.
     * Writes back a corrected variant if the stored one fails to parse. No-op in test mode.
     */
    @Override
    void customizeWatcher() {
        if (Ollivanders2.testMode)
            return;

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

            ((WolfWatcher) watcher).setTamed(true);
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
     * No-op; this effect is always permanent. Use {@link #kill()} to revert the player to human form.
     *
     * @param perm ignored - effect is always permanent
     */
    @Override
    public void setPermanent(boolean perm) {
    }

    @Override
    public void doRemove() {
    }

    /**
     * Cancel block interaction while transformed.
     */
    @Override
    void doOnPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            common.printDebugMessage("ANIMAGUS_EFFECT: cancelling PlayerInteractEvent", null, null, false);
        }
    }

    /**
     * Cancel flight while transformed.
     */
    @Override
    void doOnPlayerToggleFlightEvent(@NotNull PlayerToggleFlightEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        if (event.isFlying()) {
            event.setCancelled(true);
            common.printDebugMessage("ANIMAGUS_EFFECT: cancelling PlayerToggleFlightEvent", null, null, false);
        }
    }

    /**
     * Cancel item pickup while transformed.
     */
    @Override
    void doOnPlayerPickupItemEvent(@NotNull EntityPickupItemEvent event) {
        if (!event.getEntity().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
        common.printDebugMessage("ANIMAGUS_EFFECT: cancelling EntityPickupItemEvent", null, null, false);
    }

    /**
     * Cancel hotbar item selection while transformed.
     */
    @Override
    void doOnPlayerItemHeldEvent(@NotNull PlayerItemHeldEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
        common.printDebugMessage("ANIMAGUS_EFFECT: cancelling PlayerItemHeldEvent", null, null, false);

    }

    /**
     * Cancel item consumption while transformed.
     */
    @Override
    void doOnPlayerItemConsumeEvent(@NotNull PlayerItemConsumeEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
        common.printDebugMessage("ANIMAGUS_EFFECT: cancelling PlayerItemConsumeEvent", null, null, false);
    }

    /**
     * Cancel item dropping while transformed.
     */
    @Override
    void doOnPlayerDropItemEvent(@NotNull PlayerDropItemEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
        common.printDebugMessage("ANIMAGUS_EFFECT: cancelling PlayerDropItemEvent", null, null, false);
    }
}