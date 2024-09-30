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
package io.github.aivruu.packetboard.manager;

import io.github.aivruu.packetboard.board.CachedBoardModel;
import io.github.aivruu.packetboard.event.general.BoardCreateEvent;
import io.github.aivruu.packetboard.event.general.BoardDeleteEvent;
import io.github.aivruu.packetboard.event.modify.BoardLinesModificationEvent;
import io.github.aivruu.packetboard.event.modify.BoardSingleLineModificationEvent;
import io.github.aivruu.packetboard.event.modify.BoardTitleModificationEvent;
import io.github.aivruu.packetboard.event.general.BoardToggleEvent;
import io.github.aivruu.packetboard.board.BoardRepositoryModel;
import io.github.aivruu.packetboard.repository.RepositoryModel;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Random;

/**
 * This class is used as upper-level main-manager for all {@link CachedBoardModel} during runtime,
 * using internal API utilities for boards' internal-handling, and in-cache control. Also providing
 * plugin-events firing.
 *
 * @since 1.0.0
 */
public class BoardManager {
  /**
   * The format used for the player's scoreboard's objective-name.
   *
   * @since 1.0.0
   */
  private static final String SCOREBOARD_OBJECTIVE_BASE_FORMAT = "pb-%s";
  /**
   * Used for random number-generation for {@link #SCOREBOARD_OBJECTIVE_BASE_FORMAT}.
   *
   * @since 1.0.0
   */
  private static final Random RANDOM = new Random();
  private final RepositoryModel<CachedBoardModel> boardRepository;

  /**
   * Creates a new instance of {@link BoardManager} using the given parameters.
   *
   * @param boardRepository a {@link BoardRepositoryModel} instance.
   * @since 1.0.0
   */
  public BoardManager(final RepositoryModel<CachedBoardModel> boardRepository) {
    this.boardRepository = boardRepository;
  }

  /**
   * Creates a new scoreboard for the player using the given parameters.
   *
   * @param player the player to who create the scoreboard.
   * @param title the scoreboard's title.
   * @param lines the scoreboard's lines/content.
   * @return {@code true} if the scoreboard was created, shown to the player, and cached. Otherwise, will
   *     return false if the {@link BoardCreateEvent} is cancelled, or the scoreboard couldn't be shown.
   * @see CachedBoardModel#show()
   * @since 1.0.0
   */
  public boolean create(final Player player, final Component title, final Component... lines) {
    final var cachedBoardModel = new CachedBoardModel(player.getUniqueId().toString(),
      SCOREBOARD_OBJECTIVE_BASE_FORMAT.formatted(RANDOM.nextInt()), title, lines, true);
    final var boardCreateEvent = new BoardCreateEvent(player, cachedBoardModel);
    Bukkit.getPluginManager().callEvent(boardCreateEvent);
    // First check if the event is cancelled, then check if the board-model has an error to
    // show it to the player.
    if (boardCreateEvent.isCancelled() || cachedBoardModel.show().error()) {
      return false;
    }
    // Board-model in-cache saving.
    this.boardRepository.saveSync(cachedBoardModel);
    return true;
  }

  /**
   * Deletes the player's scoreboard, and removes the model from cache.
   *
   * @param player the player to who delete the scoreboard.
   * @return {@code true} if the scoreboard was deleted, and the model was removed from cache. Other-wise
   *     it will return {@code false}.
   * @see BoardRepositoryModel#deleteSync(String)
   * @since 1.0.0
   */
  public boolean delete(final Player player) {
    Bukkit.getPluginManager().callEvent(new BoardDeleteEvent(player));
    // Board deleting for player, and from repository's cache.
    // Ignore provided status for deletion operation, the model must be removed from cache.
    return this.boardRepository.deleteSync(player.getUniqueId().toString());
  }

  /**
   * Changes the current visibility-status for the player's scoreboard.
   *
   * @param player the player to who hide/show their scoreboard.
   * @return {@code true} if the scoreboard was turned-on, otherwise, it will return {@code false} due to
   *     different reasons, the scoreboard was turned-off, the toggle-operation has failed, the toggle-event
   *     was cancelled by another plugin, or the player isn't connected.
   * @see CachedBoardModel#toggle()
   * @since 1.0.0
   */
  public boolean toggle(final Player player) {
    final var cachedBoardModel = this.boardRepository.findSync(player.getUniqueId().toString());
    if (cachedBoardModel == null) {
      return false;
    }
    final var boardToggleEvent = new BoardToggleEvent(player, cachedBoardModel.visible());
    Bukkit.getPluginManager().callEvent(boardToggleEvent);
    if (boardToggleEvent.isCancelled()) {
      return false;
    }
    final var boardToggleStatus = cachedBoardModel.toggle();
    if (boardToggleStatus.error()) {
      return false;
    }
    // At this point it's not null.
    this.boardRepository.updateSync(boardToggleStatus.result());
    return boardToggleStatus.turnedOn();
  }

  /**
   * Sets new content/lines for the player's scoreboard.
   *
   * @param player the player to who modify the scoreboard's lines.
   * @param lines the new lines to be shown within the scoreboard.
   * @return {@code true} if the lines were modified, and the model was updated in cache. Otherwise, it will
   *     return {@code false} if the modification-operation suffered a failure, or the player isn't online
   *     or the scoreboard was turned-off previously.
   * @see CachedBoardModel#visible()
   * @see CachedBoardModel#lines(Component...)
   * @since 1.0.0
   */
  public boolean lines(final Player player, final Component... lines) {
    final var cachedBoardModel = this.boardRepository.findSync(player.getUniqueId().toString());
    if ((cachedBoardModel == null) || !cachedBoardModel.visible()) {
      return false;
    }
    Bukkit.getPluginManager().callEvent(new BoardLinesModificationEvent(player, lines));
    final var linesModificationStatus = cachedBoardModel.lines(lines);
    if (linesModificationStatus.error()) {
      return false;
    }
    this.boardRepository.updateSync(linesModificationStatus.result());
    return true;
  }

  /**
   * Sets a new text-component for the given player's scoreboard's line.
   *
   * @param player the player to who modify the scoreboard.
   * @param line the scoreboard's line to modify.
   * @param text the text to be shown on that line.
   * @return {@code true} if the line has been modified, and the model was updated in cache. Otherwise, it will
   *     return {@code false} if the modification-operation suffered a failure, or the player isn't online
   *     or the scoreboard was turned-off previously.
   * @see CachedBoardModel#visible()
   * @see CachedBoardModel#line(int, Component)
   * @since 1.0.0
   */
  public boolean line(final Player player, final int line, final Component text) {
    final var cachedBoardModel = this.boardRepository.findSync(player.getUniqueId().toString());
    if ((cachedBoardModel == null) || !cachedBoardModel.visible()) {
      return false;
    }
    Bukkit.getPluginManager().callEvent(new BoardSingleLineModificationEvent(player, (byte) line, text));
    final var lineModificationStatus = cachedBoardModel.line(line, text);
    if (lineModificationStatus.error()) {
      return false;
    }
    final var updatedCachedBoardModel = lineModificationStatus.result();
    this.boardRepository.updateSync(updatedCachedBoardModel);
    return true;
  }

  /**
   * Removes a specific line from the player's scoreboard.
   *
   * @param player the player that owns the scoreboard.
   * @param line the line to be removed.
   * @return {@code true} if the line was removed, and the model was updated in cache. Otherwise, it will
   *     return {@code false} if the board-model doesn't exist, the board is turned-off, or the modification-operation
   *     suffered an error.
   * @see CachedBoardModel#visible()
   * @see CachedBoardModel#removeLine(int)
   * @since 1.0.0
   */
  public boolean removeLine(final Player player, final int line) {
    final var cachedBoardModel = this.boardRepository.findSync(player.getUniqueId().toString());
    if ((cachedBoardModel == null) || !cachedBoardModel.visible()) {
      return false;
    }
    final var lineRemovalStatus = cachedBoardModel.removeLine(line);
    if (lineRemovalStatus.error()) {
      return false;
    }
    // At this point it's not null.
    final var modifiedBoard = lineRemovalStatus.result();
    Bukkit.getPluginManager().callEvent(new BoardLinesModificationEvent(player, modifiedBoard.lines()));
    this.boardRepository.updateSync(modifiedBoard);
    return true;
  }

  /**
   * Sets a new title-component for the player's scoreboard.
   *
   * @param player the player to who modify the scoreboard's lines.
   * @param title the new title to be shown within the scoreboard.
   * @return {@code true} if the title were modified, and the model was updated in cache. Otherwise, it will
   *     return {@code false} if the modification-operation suffered a failure, or the player isn't online
   *     or the scoreboard was turned-off previously.
   * @see CachedBoardModel#visible()
   * @see CachedBoardModel#title(Component)
   * @since 1.0.0
   */
  public boolean title(final Player player, final Component title) {
    final var cachedBoardModel = this.boardRepository.findSync(player.getUniqueId().toString());
    if ((cachedBoardModel == null) || !cachedBoardModel.visible()) {
      return false;
    }
    final var titleModificationStatus = cachedBoardModel.title(title);
    if (titleModificationStatus.error()) {
      return false;
    }
    Bukkit.getPluginManager().callEvent(new BoardTitleModificationEvent(player, title));
    this.boardRepository.updateSync(titleModificationStatus.result());
    return true;
  }

  /**
   * Deletes all the cached-boards for the yet online players, then removes all the models from
   * the repository's cache.
   *
   * @since 1.0.0
   */
  public void close() {
    // Deleting all visible, and turned-on scoreboards before models deletion.
    for (final var cachedBoardModel : this.boardRepository.findAllSync()) {
      if (!cachedBoardModel.visible()) continue;
      // Ignore provided status for deletion operation, only delete it.
      cachedBoardModel.delete();
    }
    // In-cache models deletion.
    this.boardRepository.clearRegistry();
  }
}
