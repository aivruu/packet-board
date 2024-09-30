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
package io.github.aivruu.packetboard.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.aivruu.packetboard.PacketBoardPlugin;
import io.github.aivruu.packetboard.component.ComponentParserUtils;
import io.github.aivruu.packetboard.config.ConfigurationProvider;
import io.github.aivruu.packetboard.config.object.MessagesConfigModel;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.List;

public class MainCommand implements RegistrableCommandModel {
  private final PacketBoardPlugin plugin;
  private final ConfigurationProvider<MessagesConfigModel> messagesConfigProvider;

  public MainCommand(final PacketBoardPlugin plugin, final ConfigurationProvider<MessagesConfigModel> messagesConfigProvider) {
    this.plugin = plugin;
    this.messagesConfigProvider = messagesConfigProvider;
  }

  @Override
  public List<String> alias() {
    return List.of("pb", "pboard", "packetb");
  }

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public LiteralCommandNode<CommandSourceStack> register() {
    return Commands.literal("packetboard")
      .executes(commandContext -> {
        commandContext.getSource()
          .getSender()
          .sendMessage(ComponentParserUtils.apply("<gradient:yellow:green>Running PacketBoard plugin on version '1.0.0'."));
        return Command.SINGLE_SUCCESS;
      })
      .then(Commands.literal("help")
        .requires(source -> source.getSender().hasPermission("packetboard.command.help"))
        .executes(commandContext -> {
          commandContext.getSource().getSender().sendMessage(ComponentParserUtils.apply(
            this.messagesConfigProvider.configModel().help));
          return Command.SINGLE_SUCCESS;
        })
      )
      .then(Commands.literal("reload")
        .requires(source -> source.getSender().hasPermission("packetboard.command.reload"))
        .executes(commandContext -> {
          final var messages = this.messagesConfigProvider.configModel();
          final var sender = commandContext.getSource().getSender();
          final var reloadStatus = this.plugin.reload();
          if (reloadStatus == PacketBoardPlugin.SUCCESSFUL_RELOAD_STATUS) {
            sender.sendMessage(ComponentParserUtils.apply(messages.reloadSuccess));
          } else if (reloadStatus == PacketBoardPlugin.CONFIGURATION_RELOAD_ANOMALY_STATUS) {
            sender.sendMessage(ComponentParserUtils.apply(messages.reloadFailedDueToConfiguration));
          } else {
            sender.sendMessage(ComponentParserUtils.apply(messages.reloadFailedDueToExecutors));
          }
          return Command.SINGLE_SUCCESS;
        })
      )
      .build();
  }
}
