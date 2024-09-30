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

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderParsingUtils {
  /** Used for from Legacy to Component parsing for PlaceholderAPI it's placeholders with legacy-chars support. */
  private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
  private static final boolean PLACEHOLDER_API_AVAILABLE;

  static {
    final var pluginManager = Bukkit.getPluginManager();
    PLACEHOLDER_API_AVAILABLE = (pluginManager.getPlugin("PlaceholderAPI") != null)
      && pluginManager.isPluginEnabled("PlaceholderAPI");
  }

  /**
   * Parses the given component into legacy-text for PlaceholderAPI application, only if this is
   * available -> {@link #PLACEHOLDER_API_AVAILABLE}.
   *
   * @param player the player used for placeholders-applying.
   * @param text the text to parse.
   * @return A {@link Component} with processed-placeholders. If PlaceholderAPI isn't available, it will return
   *     the given {@link Component}.
   * @since 1.0.0
   */
  public static Component parse(final Player player, final Component text) {
    if (!PLACEHOLDER_API_AVAILABLE) return text;
    // Serialize given component into string for placeholders applying.
    final var parsedToLegacyText = LEGACY_COMPONENT_SERIALIZER.serialize(text);
    return LEGACY_COMPONENT_SERIALIZER.deserialize(PlaceholderAPI.setPlaceholders(player, parsedToLegacyText));
  }
}
