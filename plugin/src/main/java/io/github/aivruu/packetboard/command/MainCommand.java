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
import io.github.aivruu.packetboard.manager.BoardManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class MainCommand implements RegistrableCommandModel {
  private final BoardManager boardManager;

  public MainCommand(final BoardManager boardManager) {
    this.boardManager = boardManager;
  }

  @Override
  @SuppressWarnings("UnstableApiUsage")
  public LiteralCommandNode<CommandSourceStack> register() {
    return Commands.literal("packetboard")
      .executes(ctx -> {
        final var playerSender = (Player) ctx.getSource().getSender();
        this.boardManager.title(playerSender, Component.text("Title Example"));
        this.boardManager.lines(playerSender, Component.text("Score 1"), Component.text("Score 2"));
        return Command.SINGLE_SUCCESS;
      })
      .build();
  }
}
