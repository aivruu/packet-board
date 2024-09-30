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
package io.github.aivruu.packetboard.util;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LuckPermsUtil {
  private static final @Nullable UserManager USER_MANAGER;

  static {
    final var pluginManager = Bukkit.getPluginManager();
    final var luckPermsAvailability = pluginManager.getPlugin("LuckPerms") != null;
    USER_MANAGER = luckPermsAvailability ? LuckPermsProvider.get().getUserManager() : null;
  }

  /**
   * Returns the primary-group for the player with this id.
   *
   * @param playerId the player's unique id.
   * @return The player's primary permission-group, or {@code null} if LuckPerms isn't available, or
   *     player's user-information isn't available.
   * @see #USER_MANAGER
   * @since 1.0.0
   */
  public static @Nullable String primaryGroup(final UUID playerId) {
    if (USER_MANAGER == null) {
      return null;
    }
    final var user = USER_MANAGER.getUser(playerId);
    return (user == null) ? null : user.getPrimaryGroup();
  }
}
