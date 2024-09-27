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

/**
 * This interface is used as base-model for internal scoreboards creation and modification functions
 * using minecraft-protocol.
 *
 * @since 1.0.0
 */
public interface VersionPacketProviderModel {
  /**
   * Sends the correspond packets for the player's scoreboard creation with the given title and lines.
   *
   * @param player the player to who create this scoreboard.
   * @param scoreboardObjectiveId the player's scoreboard's objective's unique id.
   * @param title the title of the scoreboard.
   * @param lines the lines of the scoreboard.
   * @since 1.0.0
   */
  void create(final Player player, final String scoreboardObjectiveId, final Component title, final Component... lines);

  /**
   * Sends the correspond packets for the player's scoreboard's lines modification.
   *
   * @param player the player to who modify the scoreboard's lines.
   * @param scoreboardObjectiveId the player's scoreboard's objective's unique id.
   * @param lines the new lines to be shown within the scoreboard.
   * @since 1.0.0
   */
  void sendLines(final Player player, final String scoreboardObjectiveId, final Component... lines);

  /**
   * Sends the correspond packets for the player's scoreboard's specific line modification.
   *
   * @param player the player to who modify the scoreboard's lines.
   * @param line the line-number to be modified.
   * @param text the text to be set for that line.
   * @param scoreboardObjectiveId the player's scoreboard's objective's unique id.
   * @since 1.0.0
   */
  void sendLine(final Player player, final int line, final Component text, final String scoreboardObjectiveId);

  /**
   * Sends the correspond packets for the player's scoreboard's title modification.
   *
   * @param player the player to who modify the scoreboard's title.
   * @param scoreboardObjectiveId the player's scoreboard's objective's unique id.
   * @param title the new title to be shown within the scoreboard.
   * @since 1.0.0
   */
  void sendTitle(final Player player, final Component title, final String scoreboardObjectiveId);

  /**
   * Sends the correspond packets for the player's scoreboard's removal.
   *
   * @param player the player to who remove the scoreboard.
   * @param scoreboardObjectiveId the player's scoreboard's objective's unique id.
   * @since 1.0.0
   */
  void delete(final Player player, final String scoreboardObjectiveId);
}
