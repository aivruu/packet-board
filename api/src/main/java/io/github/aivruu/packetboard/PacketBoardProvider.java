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
package io.github.aivruu.packetboard;

import org.jetbrains.annotations.Nullable;

/**
 * This class is used as {@link PacketBoard} API instances provider.
 *
 * @since 1.0.0
 */
public class PacketBoardProvider {
  private static @Nullable PacketBoard instance;

  /**
   * Returns the current {@link PacketBoard} instance.
   *
   * @return The {@link PacketBoard} instance.
   * @throws IllegalStateException if the API isn't initialized yet.
   * @since 1.0.0
   */
  public static PacketBoard get() {
    if (instance == null) {
      throw new IllegalStateException("PacketBoard's API isn't initialized yet.");
    }
    return instance;
  }

  /**
   * Sets the given {@link PacketBoard}'s implementation to the API class-field.
   *
   * @param impl the {@link PacketBoard} implementation to define as API instance.
   * @throws IllegalStateException if the API is already initialized.
   * @since 1.0.0
   */
  public static void set(final PacketBoard impl) {
    if (instance != null) {
      throw new IllegalStateException("PacketBoard's API is already initialized.");
    }
    instance = impl;
  }
}
