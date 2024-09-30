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
package io.github.aivruu.packetboard.board.status;

import io.github.aivruu.packetboard.board.CachedBoardModel;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

/**
 * This record is used as status-provider for {@link CachedBoardModel} any possible operations.
 *
 * @param status the status-code for this instance.
 * @param result the result, a {@link CachedBoardModel} instance for this provider-instance, or {@code null}.
 * @since 1.0.0
 */
public record BoardModificationStatusProvider(byte status, @Nullable CachedBoardModel result) {
  /**
   * The scoreboard was created successfully.
   *
   * @since 1.0.0
   */
  public static final byte CREATED_STATUS = 0;
  /**
   * The scoreboard was deleted without any error.
   *
   * @since 1.0.0
   */
  public static final byte DELETED_STATUS = 1;
  /**
   * The scoreboard was toggled-off correctly.
   *
   * @since 1.0.0
   */
  public static final byte TURNED_OFF_STATUS = 2;
  /**
   * The scoreboard was toggled-on correctly.
   *
   * @since 1.0.0
   */
  public static final byte TURNED_ON_STATUS = 3;
  /**
   * The scoreboard's title was modified successfully.
   *
   * @since 1.0.0
   */
  public static final byte MODIFIED_TITLE_STATUS = 4;
  /**
   * The scoreboard's lines were modified or deleted successfully.
   *
   * @since 1.0.0
   */
  public static final byte MODIFIED_LINES_STATUS = 5;
  /**
   * An error occurred while performing the operation.
   *
   * @since 1.0.0
   */
  public static final byte ERROR_STATUS = 6;

  /**
   * Creates a new {@link BoardModificationStatusProvider} with the {@link #CREATED_STATUS}.
   *
   * @return The {@link BoardModificationStatusProvider} with the {@link #CREATED_STATUS}, and a {@code null} result.
   * @since 1.0.0
   */
  public static BoardModificationStatusProvider withCreate() {
    return new BoardModificationStatusProvider(CREATED_STATUS, null);
  }

  /**
   * Creates a new {@link BoardModificationStatusProvider} with the {@link #DELETED_STATUS}.
   *
   * @return The {@link BoardModificationStatusProvider} with the {@link #DELETED_STATUS}, and a {@code null} result.
   * @since 1.0.0
   */
  public static BoardModificationStatusProvider withDelete() {
    return new BoardModificationStatusProvider(DELETED_STATUS, null);
  }

  /**
   * Creates a new {@link BoardModificationStatusProvider} with the {@link #TURNED_OFF_STATUS}.
   *
   * @param id the turned-off scoreboard's owner's id.
   * @param objectiveId the turned-off scoreboard's objective id.
   * @param title the scoreboard's title.
   * @param lines the scoreboard's lines.
   * @return The {@link BoardModificationStatusProvider} with the {@link #TURNED_OFF_STATUS}, and a new
   *     modified {@link CachedBoardModel}.
   * @since 1.0.0
   */
  public static BoardModificationStatusProvider withTurnOff(final String id, final String objectiveId, final Component title,
                                                            final Component[] lines) {
    return new BoardModificationStatusProvider(TURNED_OFF_STATUS, new CachedBoardModel(id, objectiveId, title, lines, false));
  }

  /**
   * Creates a new {@link BoardModificationStatusProvider} with the {@link #TURNED_ON_STATUS}.
   *
   * @param id the turned-on scoreboard's owner's id.
   * @param objectiveId the turned-on scoreboard's objective id.
   * @param title the scoreboard's title.
   * @param lines the scoreboard's lines.
   * @return The {@link BoardModificationStatusProvider} with the {@link #TURNED_ON_STATUS}, and a new
   *     modified {@link CachedBoardModel}.
   * @since 1.0.0
   */
  public static BoardModificationStatusProvider withTurnOn(final String id, final String objectiveId, final Component title,
                                                           final Component[] lines) {
    return new BoardModificationStatusProvider(TURNED_ON_STATUS, new CachedBoardModel(id, objectiveId, title, lines, true));
  }

  /**
   * Creates a new {@link BoardModificationStatusProvider} with the {@link #MODIFIED_TITLE_STATUS}.
   *
   * @param id the modified scoreboard's owner's id.
   * @param objectiveId the modified scoreboard's objective id.
   * @param newTitle the new scoreboard's title.
   * @param lines the scoreboard's lines.
   * @return The {@link BoardModificationStatusProvider} with the {@link #MODIFIED_TITLE_STATUS}, and a new
   *     modified {@link CachedBoardModel}.
   * @since 1.0.0
   */
  public static BoardModificationStatusProvider withModifiedTitle(final String id, final String objectiveId, final Component newTitle,
                                                                  final Component[] lines) {
    return new BoardModificationStatusProvider(MODIFIED_TITLE_STATUS, new CachedBoardModel(id, objectiveId, newTitle, lines, true));
  }

  /**
   * Creates a new {@link BoardModificationStatusProvider} with the {@link #MODIFIED_LINES_STATUS}.
   *
   * @param id the modified scoreboard's owner's id.
   * @param objectiveId the modified scoreboard's objective id.
   * @param title the scoreboard's title.
   * @param newLines the new scoreboard's lines.
   * @return The {@link BoardModificationStatusProvider} with the {@link #MODIFIED_LINES_STATUS}, and a new
   *     modified {@link CachedBoardModel}.
   * @since 1.0.0
   */
  public static BoardModificationStatusProvider withModifiedLines(final String id, final String objectiveId, final Component title,
                                                                  final Component[] newLines) {
    return new BoardModificationStatusProvider(MODIFIED_LINES_STATUS, new CachedBoardModel(id, objectiveId, title, newLines, true));
  }

  /**
   * Creates a new {@link BoardModificationStatusProvider} with the {@link #ERROR_STATUS}.
   *
   * @return The {@link BoardModificationStatusProvider} with the {@link #ERROR_STATUS}, and a {@code null} result.
   * @since 1.0.0
   */
  public static BoardModificationStatusProvider withError() {
    return new BoardModificationStatusProvider(ERROR_STATUS, null);
  }

  /**
   * Returns whether the status-code for this provider-instance is {@link #CREATED_STATUS}.
   *
   * @return Whether the status-code for this provider-instance is {@link #CREATED_STATUS}.
   * @since 1.0.0
   */
  public boolean created() {
    return this.status == CREATED_STATUS;
  }

  /**
   * Returns whether the status-code for this provider-instance is {@link #DELETED_STATUS}.
   *
   * @return Whether the status-code for this provider-instance is {@link #DELETED_STATUS}.
   * @since 1.0.0
   */
  public boolean deleted() {
    return this.status == DELETED_STATUS;
  }

  /**
   * Returns whether the status-code for this provider-instance is {@link #TURNED_OFF_STATUS}.
   *
   * @return Whether the status-code for this provider-instance is {@link #TURNED_OFF_STATUS}.
   * @since 1.0.0
   */
  public boolean turnedOff() {
    return this.status == TURNED_OFF_STATUS;
  }

  /**
   * Returns whether the status-code for this provider-instance is {@link #TURNED_ON_STATUS}.
   *
   * @return Whether the status-code for this provider-instance is {@link #TURNED_ON_STATUS}.
   * @since 1.0.0
   */
  public boolean turnedOn() {
    return this.status == TURNED_ON_STATUS;
  }

  /**
   * Returns whether the status-code for this provider-instance is {@link #MODIFIED_TITLE_STATUS}.
   *
   * @return Whether the status-code for this provider-instance is {@link #MODIFIED_TITLE_STATUS}.
   * @since 1.0.0
   */
  public boolean modifiedTitle() {
    return this.status == MODIFIED_TITLE_STATUS;
  }

  /**
   * Returns whether the status-code for this provider-instance is {@link #MODIFIED_LINES_STATUS}.
   *
   * @return Whether the status-code for this provider-instance is {@link #MODIFIED_LINES_STATUS}.
   * @since 1.0.0
   */
  public boolean modifiedLines() {
    return this.status == MODIFIED_LINES_STATUS;
  }

  /**
   * Returns whether the status-code for this provider-instance is {@link #ERROR_STATUS}.
   *
   * @return Whether the status-code for this provider-instance is {@link #ERROR_STATUS}.
   * @since 1.0.0
   */
  public boolean error() {
    return this.status == ERROR_STATUS;
  }
}
