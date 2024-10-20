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
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.aivruu.packetboard.util.ComponentUtils;
import io.github.aivruu.packetboard.config.ConfigurationProvider;
import io.github.aivruu.packetboard.config.object.MessagesConfigModel;
import io.github.aivruu.packetboard.config.object.SettingsConfigModel;
import io.github.aivruu.packetboard.manager.BoardManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

public class ScoreboardControlCommand implements RegistrableCommandModel {
  private final BoardManager boardManager;
  private final ConfigurationProvider<MessagesConfigModel> messagesConfigProvider;
  private final ConfigurationProvider<SettingsConfigModel> settingsConfigProvider;

  public ScoreboardControlCommand(final BoardManager boardManager, final ConfigurationProvider<MessagesConfigModel> messagesConfigProvider,
                                  final ConfigurationProvider<SettingsConfigModel> settingsConfigProvider) {
    this.boardManager = boardManager;
    this.messagesConfigProvider = messagesConfigProvider;
    this.settingsConfigProvider = settingsConfigProvider;
  }

  @Override
  public List<String> alias() {
    return List.of("sb", "b");
  }

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public LiteralCommandNode<CommandSourceStack> register() {
    return Commands.literal("board")
      .requires(sender -> sender instanceof final Player player && player.hasPermission("packetboard.command.scoreboard"))
      .executes(commandContext -> {
        commandContext.getSource()
          .getSender()
          .sendMessage(this.messagesConfigProvider.configModel().scoreboardControlUsage);
        return Command.SINGLE_SUCCESS;
      })
      .then(Commands.literal("toggle")
        .requires(source -> source.getSender().hasPermission("packetboard.command.toggle"))
        .executes(commandContext -> {
          final var player = (Player) commandContext.getSource().getSender();
          final var messages = this.messagesConfigProvider.configModel();
          if (this.boardManager.toggle(player)) {
            player.sendMessage(ComponentUtils.apply(messages.scoreboardTurnedOn));
          } else {
            player.sendMessage(ComponentUtils.apply(messages.scoreboardTurnedOff));
          }
          return Command.SINGLE_SUCCESS;
        })
      )
      .then(Commands.literal("title")
        .requires(source -> source.getSender().hasPermission("packetboard.command.title"))
        .then(Commands.argument("text", StringArgumentType.string())
          .executes(commandContext -> {
            final var messages = this.messagesConfigProvider.configModel();
            final var sender = commandContext.getSource().getSender();
            if (this.settingsConfigProvider.configModel().enableAnimatedTitleFeature) {
              sender.sendMessage(ComponentUtils.apply(messages.scoreboardTitleControlDisabled));
            } else {
              final var parsedGivenTitle = ComponentUtils.apply(StringArgumentType.getString(commandContext, "text"));
              // Sender casting into Player and title modification trigger.
              this.boardManager.title((Player) sender, parsedGivenTitle);
              sender.sendMessage(ComponentUtils.apply(messages.scoreboardTitleModified));
            }
            return Command.SINGLE_SUCCESS;
          })
        )
      )
      .then(Commands.literal("line")
        .requires(source -> source.getSender().hasPermission("packetboard.command.line"))
        .executes(ctx -> {
          ctx.getSource().getSender().sendMessage(Component.text("Missing scoreboard line!").color(NamedTextColor.RED));
          return Command.SINGLE_SUCCESS;
        })
        .then(Commands.argument("number", IntegerArgumentType.integer(1, 16))
          .executes(ctx -> {
            ctx.getSource().getSender().sendMessage(Component.text("Missing scoreboard line's content!").color(NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
          })
          .then(Commands.argument("content", StringArgumentType.string())
            .executes(commandContext -> {
              final var messages = this.messagesConfigProvider.configModel();
              final var sender = commandContext.getSource().getSender();
              final var specifiedLineNumber = IntegerArgumentType.getInteger(commandContext, "line");
              final var specifiedContent = StringArgumentType.getString(commandContext, "content");
              final var changedLine = this.boardManager.line((Player) sender, specifiedLineNumber,
                ComponentUtils.apply(specifiedContent));
              if (changedLine) {
                sender.sendMessage(ComponentUtils.apply(messages.scoreboardLineModified,
                  Placeholder.parsed("line", Integer.toString(specifiedLineNumber))));
              } else {
                sender.sendMessage(ComponentUtils.apply(messages.scoreboardLineUnmodified));
              }
              return Command.SINGLE_SUCCESS;
            })
          )
        )
      )
      .then(Commands.literal("remove")
        .requires(source -> source.getSender().hasPermission("packetboard.command.remove"))
        .executes(ctx -> {
          ctx.getSource().getSender().sendMessage(Component.text("Missing scoreboard line!").color(NamedTextColor.RED));
          return Command.SINGLE_SUCCESS;
        })
        .then(Commands.argument("number", IntegerArgumentType.integer(1, 16))
          .executes(commandContext -> {
            final var messages = this.messagesConfigProvider.configModel();
            final var sender = commandContext.getSource().getSender();
            final var specifiedLineNumber = IntegerArgumentType.getInteger(commandContext, "remove");
            // Sender casting into Player, line removal trigger and result verification.
            final var removedLine = this.boardManager.removeLine((Player) sender, specifiedLineNumber);
            if (removedLine) {
              sender.sendMessage(ComponentUtils.apply(messages.scoreboardLineRemoved));
            } else {
              sender.sendMessage(ComponentUtils.apply(messages.scoreboardLineDeletionFailed));
            }
            return Command.SINGLE_SUCCESS;
          })
        )
      )
      .build();
  }
}
