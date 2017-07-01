package Quidditch;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * @author autumnwoz
 */
public class Arena {
    private final String name;
    private final Location center;
    private ArenaState state;
    private Size size;

    /**
     * The sizes of the arenas
     */
    private final int MEDIUM_X_SIZE = 41;
    private final int MEDIUM_Y_SIZE = 60;
    private final int LARGE_X_SIZE = 0;
    private final int LARGE_Y_SIZE = 0;


    /**
     * @param name
     *            The name of the arena
     * @param center
     *            The center of the arena
     */
    public Arena(String name, Location center, Size size) {
        this.name = name;
        this.center = center;
        this.state = ArenaState.FREE;
        this.size = size;
    }

    /**
     * @return The name of the arena
     */
    public String getName() {
        return name;
    }

    /**
     * @return The center of the arena
     */
    public Location getCenter() {
        return center;
    }

    /**
     * @return The {@link Size}
     */
    public Size getSize() {
        return size;
    }

    /**
     * @return The {@link ArenaState}
     */
    public ArenaState getState() {
        return state;
    }

    /**
     * Start the game
     */
    public void start() {
        state = ArenaState.RUNNING;
    }

    /**
     * Ends the game
     */
    public void endGame() {
        state = ArenaState.FREE;
    }

    /**
     * @param e
     *            The {@link BlockBreakEvent}
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (isInside(e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    /**
     * @param e
     *            The {@link BlockPlaceEvent}
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (isInside(e.getBlock().getLocation())) {
            e.setBuild(false);
        }
    }

    /**
     * @param e
     *            The {@link BlockPistonExtendEvent}
     */
    @EventHandler(ignoreCancelled = true)
    public void onPistonPush(BlockPistonExtendEvent e) {
        for (Block block : e.getBlocks()) {
            if (isInside(block.getLocation())) {
                e.setCancelled(true);
                return;
            }
        }

    }

    /**
     * @param e
     *            The {@link BlockPistonRetractEvent}
     */
    @EventHandler(ignoreCancelled = true)
    public void onPistonPull(BlockPistonRetractEvent e) {
        Block block = e.getBlock();
        block = block.getRelative(e.getDirection().getOppositeFace(), 2);
        if (isInside(block.getLocation())) {
            e.setCancelled(true);
        }
    }

    /**
     * @param e
     *            The {@link EntityExplodeEvent}
     */
    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent e) {
        for (Block block : e.blockList()) {
            if (isInside(block.getLocation())) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "[" + "name=" + name + ", center=" + center + ", size=" + size + "]";
    }

    private Boolean isInside(Location location) {
        if (size == Size.MEDIUM) {
            if (Math.abs(location.getBlockX() - center.getBlockX()) <= MEDIUM_X_SIZE) {
                if (Math.abs(location.getBlockY() - center.getBlockY()) <= MEDIUM_Y_SIZE) {
                    return true;
                }
            }
        } else if (size == Size.LARGE) {
            if (Math.abs(location.getBlockX() - center.getBlockX()) <= LARGE_X_SIZE) {
                if (Math.abs(location.getBlockY() - center.getBlockY()) <= LARGE_Y_SIZE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The state of the arena
     */
    public static enum ArenaState {
        /**
         * The arena is ready to play a new game.
         */
        FREE,
        /**
         * A game is currently being played in the arena.
         */
        RUNNING;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    /**
     * The state of the arena
     */
    public static enum Size {
        /**
         * A medium sized arena.
         */
        MEDIUM,
        /**
         * A large sized arena.
         */
        LARGE;

        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }
}
