package net.darmo_creations.mccode.interpreter.types;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/**
 * A wrapper type for Minecraft’s {@link Level} class.
 */
public class WorldProxy {
  private final ServerLevel world;

  /**
   * Create a world wrapper.
   *
   * @param world The world object to wrap.
   */
  public WorldProxy(ServerLevel world) {
    this.world = world;
  }

  /**
   * Return the wrapped world object.
   */
  public ServerLevel getWorld() {
    return this.world;
  }
}
