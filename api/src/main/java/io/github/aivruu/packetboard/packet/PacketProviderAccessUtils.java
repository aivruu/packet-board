// This file is part of packet-board, licensed under the GNU License.
//
// Copyright (c) 2024 aivruu
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
package io.github.aivruu.packetboard.packet;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * This class is used as utility for {@link VersionPacketProviderModel}'s implementation's functions
 * access for scoreboard's internal creation, modifications and deletion.
 *
 * @since 1.0.0
 */
public class PacketProviderAccessUtils {
  /**
   * The packet-provider field used to be initialized with the provider's implementation,
   * and for internal scoreboard's creation, modifications and deletion.
   *
   * @since 1.0.0
   */
  private static final VersionPacketProviderModel VERSION_PACKET_PROVIDER_MODEL;

  static {
    try {
      // PacketProvider implementation initialization logic.
      final var implClass = Class.forName("io.github.aivruu.packetboard.packet.VersionPacketProviderImpl");
      VERSION_PACKET_PROVIDER_MODEL = (VersionPacketProviderModel) implClass.getConstructor().newInstance();
    } catch (final ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException
      | SecurityException | IllegalAccessException ignored) {
      // Should never happen due that the implementation-subproject always is compiled with the final jar.
      throw new IllegalStateException("PacketProvider implementation couldn't be found at the jar.");
    }
  }

  /**
   * Sends the packets for the scoreboard creation, and shows it to the player.
   *
   * @param player The player to show the scoreboard.
   * @param objectiveId the scoreboard's objective's id.
   * @param title The title of the scoreboard.
   * @param lines The lines of the scoreboard.
   * @since 1.0.0
   */
  public static void create(final Player player, final String objectiveId, final Component title, final Component... lines) {
    VERSION_PACKET_PROVIDER_MODEL.create(player, objectiveId, title, lines);
  }

  /**
   * Sends the packets for the scoreboard's lines modification.
   *
   * @param player The player to show the scoreboard.
   * @param objectiveId the scoreboard's objective's id.
   * @param lines The new lines to be shown within the scoreboard.
   * @since 1.0.0
   */
  public static void sendLines(final Player player, final String objectiveId, final Component... lines) {
    VERSION_PACKET_PROVIDER_MODEL.sendLines(player, objectiveId, lines);
  }

  /**
   * Sends the packets for the scoreboard's lines modification.
   *
   * @param player The player to show the scoreboard.
   * @param line the line-number to modify.
   * @param text The text to be shown within that line.
   * @param objectiveId the scoreboard's objective's id.
   * @since 1.0.0
   */
  public static void sendLine(final Player player, final int line, final Component text, final String objectiveId) {
    VERSION_PACKET_PROVIDER_MODEL.sendLine(player, line, text, objectiveId);
  }

  /**
   * Sends the packets for the scoreboard's title modification.
   *
   * @param player The player to show the scoreboard.
   * @param title The new title to be shown within the scoreboard.
   * @param objectiveId the scoreboard's objective's id.
   * @since 1.0.0
   */
  public static void sendTitle(final Player player, final Component title, final String objectiveId) {
    VERSION_PACKET_PROVIDER_MODEL.sendTitle(player, title, objectiveId);
  }

  /**
   * Sends the packets for the scoreboard's deletion from player's screen.
   *
   * @param player The player to who delete the scoreboard.
   * @param objectiveId the scoreboard's objective's id.
   * @since 1.0.0
   */
  public static void delete(final Player player, final String objectiveId) {
    VERSION_PACKET_PROVIDER_MODEL.delete(player, objectiveId);
  }
}
