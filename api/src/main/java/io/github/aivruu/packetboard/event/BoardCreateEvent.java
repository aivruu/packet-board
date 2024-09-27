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
package io.github.aivruu.packetboard.event;

import io.github.aivruu.packetboard.board.CachedBoardModel;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is fired once the player joins to the server before their scoreboard is created.
 *
 * @since 1.0.0
 */
public class BoardCreateEvent extends Event implements Cancellable {
  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Player player;
  private final CachedBoardModel cachedBoardModel;
  private boolean cancelled;

  public BoardCreateEvent(final Player player, final CachedBoardModel cachedBoardModel) {
    this.player = player;
    this.cachedBoardModel = cachedBoardModel;
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
   * Returns the created board-model for the player during this event.
   *
   * @return The created board-model for the player.
   * @since 1.0.0
   */
  public CachedBoardModel board() {
    return this.cachedBoardModel;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(final boolean cancel) {
    this.cancelled = cancel;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
