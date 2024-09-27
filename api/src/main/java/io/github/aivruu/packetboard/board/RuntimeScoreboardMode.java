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
package io.github.aivruu.packetboard.board;

/**
 * This enum is used to represent all possible mode-types within the scoreboards can be
 * used.
 *
 * @since 1.0.0
 */
public enum RuntimeScoreboardMode {
  /**
   * A unique scoreboard-format will be shown to each player.
   *
   * @since 1.0.0
   */
  GLOBAL,
  /**
   * A scoreboard-format will be shown to each player based-on the world is.
   *
   * @since 1.0.0
   */
  WORLD,
  /**
   * A scoreboard-format will be shown to each player based-on the permission they have.
   *
   * @since 1.0.0
   */
  PERMISSION,
  /**
   * A scoreboard-format will be shown to each player based-on the permission-group they
   * are in.
   *
   * @since 1.0.0
   */
  GROUP
}
