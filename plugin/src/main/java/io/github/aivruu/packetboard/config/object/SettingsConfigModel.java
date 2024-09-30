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
package io.github.aivruu.packetboard.config.object;

import io.github.aivruu.packetboard.board.RuntimeScoreboardMode;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class SettingsConfigModel {
  @Comment("""
    This mean that the plugin will use the animated-title feature, only for the 'GLOBAL'
    mode.""")
  public boolean enableAnimatedTitleFeature = false;

  @Comment("This mean that the scoreboard's lines will be be updated every 'x' ticks-amount, or not.")
  public boolean enableLinesRefreshing = true;

  @Comment("The refreshing-rate in seconds, for the scoreboard's animated-title.")
  public byte animatedTitleUpdateRateSeconds = 1;

  @Comment("""
    The refreshing-rate for the scoreboard's lines, in seconds.""")
  public byte linesUpdateRateSeconds = 1;

  @Comment("""
    The mode that will run the plugin's scoreboards during runtime.
    There four available modes until now:
    - GLOBAL : The same scoreboard-format will be used for all players.
    - WORLD : The scoreboard-format will be different depending on the world where
    player is.
    - PERMISSION : The scoreboard-format will be different depending on the permission
    the player have.
    - GROUP : The scoreboard-format will be different depending on the permission-group, or groups
    that the player have designed, this mode requires LuckPerms installed.""")
  public RuntimeScoreboardMode mode = RuntimeScoreboardMode.GLOBAL;

  @Comment("""
    The title that will be displayed in the animated-title feature.
    This will be displayed in the 'GLOBAL' mode only.""")
  public Component[] animatedTitleContent = {
    Component.text("<aqua>PacketBoard"),
    Component.text("<green>PacketBoard"),
    Component.text("<yellow>PacketBoard"),
    Component.text("<red>PacketBoard"),
    Component.text("<dark_aqua>PacketBoard"),
    Component.text("<gray>PacketBoard")
  };

  @Comment("""
    The static-title that will be displayed in the scoreboard.
    This will be displayed in the 'GLOBAL' mode only.""")
  public String globalTitle = "<blue>PacketBoard";

  @Comment("""
    The lines that will be displayed in the scoreboard.
    This will be displayed in the 'GLOBAL' mode only.""")
  public Component[] globalLines = {
    Component.text("<aqua>Line 1"),
    Component.text("<yellow>Line 2"),
    Component.text("<red>Line 3"),
    Component.text("<green>Line 4")
  };

  @Comment("""
    This section is for scoreboards-by-world configurations, here you can define all the scoreboard-formats
    for all the worlds that are in your server, or that are available for the players.""")
  public BoardWorldSection[] scoreboardWorld = { new BoardWorldSection() };

  @Comment("""
    This section is for scoreboards-by-permission configurations, here you can define all the scoreboard-formats
    for all the permissions that you need, and that players can have.""")
  public BoardPermissionSection[] scoreboardPermission = { new BoardPermissionSection() };

  @Comment("""
    This section is for scoreboards-by-group configurations, here you can define all the scoreboard-formats
    for all the permission-groups that you have, and that players can have.

    It requires "LuckPerms" dependency installed on your server.""")
  public BoardGroupSection[] scoreboardGroup = { new BoardGroupSection() };

  @ConfigSerializable
  public static class BoardWorldSection {
    @Comment("The world where this scoreboard will appear.")
    public String designedWorld = "spawn1";

    @Comment("The title for this world's scoreboard.")
    public String title = "<gradient:blue:dark_gray>PacketBoard (Spawn)";

    @Comment("The lines for this world's scoreboard.")
    public Component[] lines = {
      Component.text("<aqua>Line 1"),
      Component.text("<yellow>Line 2"),
      Component.text("<red>Line 3"),
      Component.text("<green>Line 4")
    };
  }

  @ConfigSerializable
  public static class BoardPermissionSection {
    @Comment("The permission required to see this scoreboard.")
    public String node = "staffmode.permission";

    @Comment("The title for this permission's scoreboard.")
    public String title = "<gradient:blue:red>PacketBoard | Staff";

    @Comment("The lines for this permission's scoreboard.")
    public Component[] lines = {
      Component.text("<aqua>Line 1"),
      Component.text("<yellow>Line 2"),
      Component.text("<red>Line 3"),
      Component.text("<green>Line 4")
    };
  }

  @ConfigSerializable
  public static class BoardGroupSection {
    @Comment("The group, or groups that could see this scoreboard-format.")
    public String designedGroup = "dev";

    @Comment("The scoreboard-title for this group, or groups.")
    public String title = "<gradient:blue:yellow>PacketBoard | TPS %server_tps%";

    @Comment("The scoreboard-lines for this group, or groups.")
    public Component[] lines = {
      Component.text("<aqua>Line 1"),
      Component.text("<yellow>Line 2"),
      Component.text("<red>Line 3"),
      Component.text("<green>Line 4")
    };
  }
}
