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

import io.github.aivruu.packetboard.board.status.BoardModificationStatusProvider;
import io.github.aivruu.packetboard.packet.PacketProviderAccessUtils;
import io.github.aivruu.packetboard.repository.CachableModel;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * This record is used as cachable-model to represent active scoreboards for any connected player.
 *
 * @param id the player's unique id.
 * @param objectiveId the scoreboard's objective's unique id.
 * @param title the scoreboard's title.
 * @param lines the scoreboard's lines.
 * @param visible if the scoreboard is turned-on or not.
 * @since 1.0.0
 */
public record CachedBoardModel(String id, String objectiveId, Component title, Component[] lines, boolean visible) implements CachableModel {
  /**
   * Returns a new {@link Builder} for this {@link CachedBoardModel}.
   *
   * @return The new {@link Builder} instance.
   * @since 1.0.0
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the {@link Player} reference for this scoreboard's owner based on the owner's unique id.
   *
   * @return The {@link Player} for this scoreboard's owner, or {@code null} if the player isn't online.
   * @since 1.0.0
   */
  public @Nullable Player player() {
    return Bukkit.getPlayer(UUID.fromString(this.id));
  }

  /**
   * Shows the scoreboard to the player.
   *
   * @return The {@link BoardModificationStatusProvider} with the status for the operation.
   *     - {@link BoardModificationStatusProvider#withCreate()} if the scoreboard was created correctly.
   *     <p>
   *     - {@link BoardModificationStatusProvider#withError()} if the player isn't connected.
   * @since 1.0.0
   */
  public BoardModificationStatusProvider show() {
    final var player = this.player();
    // If player isn't on the server, don't create any scoreboard.
    if (player == null) {
      return BoardModificationStatusProvider.withError();
    }
    // Send packets to the player with scoreboard information and attributes.
    PacketProviderAccessUtils.create(player, this.objectiveId, this.title, this.lines);
    return BoardModificationStatusProvider.withCreate();
  }

  /**
   * Deletes the scoreboard from the player.
   *
   * @return The {@link BoardModificationStatusProvider} with the status for the operation.
   *     - {@link BoardModificationStatusProvider#withDelete()} if the scoreboard was deleted correctly.
   *     <p>
   *     - {@link BoardModificationStatusProvider#withError()} if the player isn't connected.
   * @since 1.0.0
   */
  public BoardModificationStatusProvider delete() {
    final var player = this.player();
    if (player == null) {
      return BoardModificationStatusProvider.withError();
    }
    PacketProviderAccessUtils.delete(player, this.objectiveId);
    return BoardModificationStatusProvider.withDelete();
  }

  /**
   * Toggles the scoreboard's visibility-status for the player.
   *
   * @return The {@link BoardModificationStatusProvider} with the status for the operation.
   *     - {@link BoardModificationStatusProvider#withTurnOn(CachedBoardModel)} if the scoreboard was turned-on.
   *     <p>
   *     - {@link BoardModificationStatusProvider#withTurnOff(CachedBoardModel)} if the scoreboard was turned-off.
   *     <p>
   *     - {@link BoardModificationStatusProvider#withError()} if the player isn't connected.
   * @since 1.0.0
   */
  public BoardModificationStatusProvider toggle() {
    final var player = this.player();
    if (player == null) {
      return BoardModificationStatusProvider.withError();
    }
    // Basically, if the scoreboard was already turned-off or not, we show, or hide the board to the player,
    // and we provide a new object-instance with the updated information.
    if (!visible) {
      PacketProviderAccessUtils.create(player, this.objectiveId, this.title, this.lines);
      return BoardModificationStatusProvider.withTurnOn(CachedBoardModel.builder()
        .id(this.id)
        .objectiveId(this.objectiveId)
        .title(this.title)
        .lines(this.lines)
        .visible(true)
        .build());
    }
    PacketProviderAccessUtils.delete(player, this.objectiveId);
    return BoardModificationStatusProvider.withTurnOff(
      CachedBoardModel.builder()
        .id(this.id)
        .objectiveId(this.objectiveId)
        .title(this.title)
        .lines(this.lines)
        .visible(false)
        .build()
    );
  }

  /**
   * Updates the title for the player's scoreboard without make modifications to the current board-model.
   *
   * @param text the new title for the scoreboard.
   * @since 1.0.0
   */
  public void titleWithoutUpdate(final Component text) {
    final var player = this.player();
    if (player == null) return;
    PacketProviderAccessUtils.sendTitle(player, text, this.objectiveId);
  }

  /**
   * Updates the title for the player's scoreboard.
   *
   * @param title the new title for the scoreboard.
   * @return The {@link BoardModificationStatusProvider} with the status for the operation.
   *    - {@link BoardModificationStatusProvider#withModifiedTitle(CachedBoardModel)} if the title was updated correctly.
   *    <p>
   *    - {@link BoardModificationStatusProvider#withError()} if the player isn't connected.
   * @since 1.0.0
   */
  public BoardModificationStatusProvider title(final Component title) {
    final var player = this.player();
    if (player == null) {
      return BoardModificationStatusProvider.withError();
    }
    PacketProviderAccessUtils.sendTitle(player, title, this.objectiveId);
    return BoardModificationStatusProvider.withModifiedTitle(
      CachedBoardModel.builder()
        .id(this.id)
        .objectiveId(this.objectiveId)
        .title(title)
        .lines(this.lines)
        .visible(true)
        .build());
  }

  /**
   * Updates the lines for the player's scoreboard without make modifications to the current board-model.
   *
   * @param lines the new lines for the scoreboard.
   * @since 1.0.0
   */
  public void linesWithoutUpdate(final Component... lines) {
    final var player = this.player();
    if (player == null) return;
    PacketProviderAccessUtils.sendLines(player, this.objectiveId, lines);
  }

  /**
   * Updates the lines for the player's scoreboard.
   *
   * @param lines the new lines for the scoreboard.
   * @return The {@link BoardModificationStatusProvider} with the status for the operation.
   *    - {@link BoardModificationStatusProvider#withModifiedLines(CachedBoardModel)} if the lines were updated correctly.
   *    <p>
   *    - {@link BoardModificationStatusProvider#withError()} if the player isn't connected.
   * @since 1.0.0
   */
  public BoardModificationStatusProvider lines(final Component... lines) {
    final var player = this.player();
    if (player == null) {
      return BoardModificationStatusProvider.withError();
    }
    PacketProviderAccessUtils.sendLines(player, this.objectiveId, lines);
    return BoardModificationStatusProvider.withModifiedLines(
      CachedBoardModel.builder()
        .id(this.id)
        .objectiveId(this.objectiveId)
        .title(this.title)
        .lines(lines)
        .visible(true)
        .build());
  }

  /**
   * Updates the given line's text for the player's scoreboard without make modifications to the current board-model.
   *
   * @param line the line-number to modify.
   * @param text the new text to show on that line.
   * @since 1.0.0
   */
  public void lineWithoutUpdate(final int line, final Component text) {
    final var player = this.player();
    if (player == null) return;
    PacketProviderAccessUtils.sendLine(player, (this.lines.length - line), text, this.objectiveId);
  }

  /**
   * Updates the given line's text for the player's scoreboard.
   *
   * @param line the line-number to modify.
   * @param text the new text to show on that line.
   * @return The {@link BoardModificationStatusProvider} with the status for the operation.
   *    - {@link BoardModificationStatusProvider#withModifiedLines(CachedBoardModel)} if the line has been modified correctly.
   *    <p>
   *    - {@link BoardModificationStatusProvider#withError()} if the player isn't connected, or specified line-number is out
   *    of range for the scoreboard's lines-array.
   * @since 1.0.0
   */
  public BoardModificationStatusProvider line(final int line, final Component text) {
    final var player = this.player();
    if (player == null || line >= this.lines.length) {
      return BoardModificationStatusProvider.withError();
    }
    PacketProviderAccessUtils.sendLine(player, (this.lines.length - line), text, this.objectiveId);
    // Array modification to include new changed-line.
    final var modifiedLinesArray = new Component[this.lines.length];
    for (byte i = 0; i < this.lines.length; i++) {
      modifiedLinesArray[i] = (i == line) ? text : this.lines[i];
    }
    return BoardModificationStatusProvider.withModifiedLines(
      CachedBoardModel.builder()
        .id(this.id)
        .objectiveId(this.objectiveId)
        .title(this.title)
        .lines(modifiedLinesArray)
        .visible(true)
        .build());
  }

  /**
   * Removes the specified line from the player's scoreboard.
   *
   * @param line the scoreboard's score to remove.
   * @return The {@link BoardModificationStatusProvider} with the status for the operation.
   *     - {@link BoardModificationStatusProvider#withModifiedLines(CachedBoardModel)} if the line was removed correctly.
   *     <p>
   *     - {@link BoardModificationStatusProvider#withError()} if the player isn't connected, or the specified line-to-delete
   *     is out of range for the scoreboard's lines array.
   * @since 1.0.0
   */
  public BoardModificationStatusProvider removeLine(final int line) {
    final var player = this.player();
    if ((player == null) || line >= this.lines.length) {
      return BoardModificationStatusProvider.withError();
    }
    // Array modification for element on 'line' index be deleted.
    final var newLines = new Component[this.lines.length - 1];
    for (int i = 0; i < this.lines.length; i++) {
      if (i == line) continue;
      newLines[i] = this.lines[i];
    }
    PacketProviderAccessUtils.sendLines(player, this.objectiveId, newLines);
    return BoardModificationStatusProvider.withModifiedLines(
      CachedBoardModel.builder()
        .id(this.id)
        .objectiveId(this.objectiveId)
        .title(this.title)
        .lines(newLines)
        .visible(true)
        .build());
  }

  public static class Builder {
    private String id;
    private String objectiveId;
    private Component title;
    private Component[] lines;
    private boolean visible;

    public Builder id(final String id) {
      this.id = id;
      return this;
    }

    public Builder objectiveId(final String objectiveId) {
      this.objectiveId = objectiveId;
      return this;
    }

    public Builder title(final Component title) {
      this.title = title;
      return this;
    }

    public Builder lines(final Component... lines) {
      this.lines = lines;
      return this;
    }

    public Builder visible(final boolean visible) {
      this.visible = visible;
      return this;
    }

    public CachedBoardModel build() {
      return new CachedBoardModel(this.id, this.objectiveId, this.title, this.lines, this.visible);
    }
  }
}
