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
package io.github.aivruu.packetboard.event.modify;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is fired when the player's scoreboard's title is modified, and its {@link io.github.aivruu.packetboard.board.CachedBoardModel}
 * is updated in the cache-repository
 *
 * @since 1.0.0
 */
public class BoardTitleModificationEvent extends Event {
  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Player player;
  private final Component newTitle;

  public BoardTitleModificationEvent(final Player player, final Component newTitle) {
    this.player = player;
    this.newTitle = newTitle;
  }

  /**
   * Returns the player involved in this event.
   *
   * @return This event's involved player.
   * @since 1.0.0
   */
  public Player player() {
    return this.player;
  }

  /**
   * Returns the new title of the player's scoreboard.
   *
   * @return The new title of the player's scoreboard.
   * @since 1.0.0
   */
  public Component newTitle() {
    return this.newTitle;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
