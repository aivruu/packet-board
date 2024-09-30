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

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class MessagesConfigModel {
  public String[] scoreboardControlUsage = {
    "<blue>[PacketBoard] <gradient:yellow:green>Scoreboard's Control Commands:",
    "<gradient:yellow:green>- /scoreboard toggle | Turn On/Off your scoreboard.",
    "<gradient:yellow:green>- /scoreboard title <text> | Modify your scoreboard's title.",
    "<gradient:yellow:green>- /scoreboard line <line> <text> | Modify your scoreboard's specified-line.",
    "<gradient:yellow:green>- /scoreboard remove <line> | Remove the specified-line from your scoreboard.",
  };

  public String[] help = {
    "<blue>[PacketBoard] <gradient:yellow:green>Scoreboard's Control Commands:",
    "<gradient:yellow:green>- /packetboard help | Display the help message.",
    "<gradient:yellow:green>- /packetboard reload | Reload the plugin's configurations and threads' attributes.",
    "",
    "<gradient:gray:aqua><hover:show_text:'Click to execute the command.'><click:run_command:'/scoreboard'>-> Check usage-guide for the scoreboard's own commands.</click></hover>",
  };

  public String reloadSuccess = "<blue>[PacketBoard] <green>The plugin have been fully-reloaded correctly!";

  public String reloadFailed = "<blue>[PacketBoard] <red>The configuration-files couldn't reloaded!";

  public String scoreboardTurnedOn = "<blue>[PacketBoard] <gradient:yellow:green>The scoreboard has been turned-on!";

  public String scoreboardTurnedOff = "<blue>[PacketBoard] <gradient:yellow:red>The scoreboard has been turned-off!";

  public String scoreboardTitleControlDisabled = "<blue>[PacketBoard] <red>The scoreboard's title-control is disabled due to title-animation.";

  public String scoreboardTitleModified = "<blue>[PacketBoard] <gradient:aqua:yellow>The scoreboard's title has been modified to <title>!";

  public String scoreboardLineModified = "<blue>[PacketBoard] <gradient:aqua:yellow>The scoreboard's line <line> has been modified!";

  public String scoreboardLineUnmodified = "<blue>[PacketBoard] <red>The line couldn't be modified, check and validate the line-number you gave!";

  public String scoreboardLineRemoved = "<blue>[PacketBoard] <gradient:yellow:red>The line has been removed from the scoreboard!";

  public String scoreboardLineDeletionFailed = "<blue>[PacketBoard] <red>Failed to remove the line from the scoreboard!";
}
