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

import java.lang.reflect.InvocationTargetException;

/**
 * This class is used as utility for {@link VersionPacketProviderModel}'s implementation's functions
 * access for scoreboard's internal creation, modifications and deletion.
 *
 * @since 1.0.0
 */
public class PacketProviderAccessor {
  /**
   * The packet-provider field used to be initialized with the provider's implementation,
   * and for internal scoreboard's creation, modifications and deletion.
   *
   * @since 1.0.0
   */
  public static final VersionPacketProviderModel PACKET_PROVIDER_IMPL;

  static {
    try {
      // PacketProvider implementation initialization logic.
      final var implClass = Class.forName("io.github.aivruu.packetboard.packet.VersionPacketProviderImpl");
      PACKET_PROVIDER_IMPL = (VersionPacketProviderModel) implClass.getConstructor().newInstance();
    } catch (final ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException
                   | SecurityException | IllegalAccessException ignored) {
      // Should never happen due that the implementation-subproject always is compiled with the final jar.
      throw new IllegalStateException("PacketProvider implementation couldn't be found at the jar.");
    }
  }
}
