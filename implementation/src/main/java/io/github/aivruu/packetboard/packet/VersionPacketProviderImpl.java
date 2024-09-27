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

import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * {@link VersionPacketProviderModel} implementation for internal packets-handling for scoreboards
 * functions for Minecraft {@code 1.21.1} version.
 *
 * @since 1.0.0
 */
public class VersionPacketProviderImpl implements VersionPacketProviderModel {
  private static final Optional<NumberFormat> NUMBER_FORMAT = Optional.of(BlankFormat.INSTANCE);
  private final ServerScoreboard serverScoreboard = MinecraftServer.getServer().getScoreboard();
  private ClientboundSetObjectivePacket clientboundSetObjectivePacket;
  private ClientboundSetDisplayObjectivePacket clientboundSetDisplayObjectivePacket;
  private ClientboundSetScorePacket clientboundSetScorePacket;

  @Override
  public void create(final Player player, final String scoreboardObjectiveId, final Component title, final Component... lines) {
    final var serverPlayerConnection = ((CraftPlayer) player).getHandle().connection;
    final var playerOwnerId = player.getUniqueId().toString();
    // Scoreboard objectives declaration and packet-sending.
    final var objective = this.serverScoreboard.addObjective(scoreboardObjectiveId, ObjectiveCriteria.DUMMY,
      new AdventureComponent(title), ObjectiveCriteria.RenderType.INTEGER, true, BlankFormat.INSTANCE);
    this.sendObjectivePacket(serverPlayerConnection, objective, ClientboundSetObjectivePacket.METHOD_ADD);
    this.sendDisplayObjectivePacket(serverPlayerConnection, objective);
    // Scoreboard scores declaration and packet-sending.
    for (int i = 0; i < lines.length; i++) {
      this.sendScorePacket(serverPlayerConnection, playerOwnerId, objective.getName(),
        (lines.length - i), new AdventureComponent(lines[i]));
    }
  }

  private void sendObjectivePacket(final ServerPlayerConnection serverPlayerConnection, final Objective objective, final int mode) {
    this.clientboundSetObjectivePacket = new ClientboundSetObjectivePacket(objective, mode);
    serverPlayerConnection.send(this.clientboundSetObjectivePacket);
  }

  private void sendDisplayObjectivePacket(final ServerPlayerConnection serverPlayerConnection, final Objective objective) {
    this.clientboundSetDisplayObjectivePacket = new ClientboundSetDisplayObjectivePacket(
      DisplaySlot.SIDEBAR, objective);
    serverPlayerConnection.send(this.clientboundSetDisplayObjectivePacket);
  }

  private void sendScorePacket(final ServerPlayerConnection serverPlayerConnection, final String ownerName, final String objectiveName,
                               final int scoreIndex, final net.minecraft.network.chat.Component component) {
    this.clientboundSetScorePacket = new ClientboundSetScorePacket(
      ownerName, objectiveName, scoreIndex,
      Optional.of(component),
      NUMBER_FORMAT);
    serverPlayerConnection.send(clientboundSetScorePacket);
  }

  @Override
  public void sendLines(final Player player, final String scoreboardObjectiveId, final Component... lines) {
    final var serverPlayerConnection = ((CraftPlayer) player).getHandle().connection;
    final var objective = this.serverScoreboard.getObjective(scoreboardObjectiveId);
    for (int i = 0; i < lines.length; i++) {
      this.sendScorePacket(serverPlayerConnection, player.getName(), objective.getName(),
        (lines.length - i), new AdventureComponent(lines[i]));
    }
  }

  @Override
  public void sendLine(final Player player, final int line, final Component text, final String scoreboardObjectiveId) {
    final var serverPlayerConnection = ((CraftPlayer) player).getHandle().connection;
    final var playerOwnerId = player.getUniqueId().toString();
    final var objective = this.serverScoreboard.getObjective(scoreboardObjectiveId);
    this.sendScorePacket(serverPlayerConnection, playerOwnerId, objective.getName(),
      line, new AdventureComponent(text));
  }

  @Override
  public void sendTitle(final Player player, final Component title, final String scoreboardObjectiveId) {
    final var serverPlayerConnection = ((CraftPlayer) player).getHandle().connection;
    // The objective for this player's scoreboard never will be null at this point.
    this.serverScoreboard.removeObjective(this.serverScoreboard.getObjective(scoreboardObjectiveId));
    final var newObjective = this.serverScoreboard.addObjective(scoreboardObjectiveId, ObjectiveCriteria.DUMMY,
      new AdventureComponent(title),
      ObjectiveCriteria.RenderType.INTEGER, true, BlankFormat.INSTANCE);
    this.sendObjectivePacket(serverPlayerConnection, newObjective, ClientboundSetObjectivePacket.METHOD_CHANGE);
    this.sendDisplayObjectivePacket(serverPlayerConnection, newObjective);
  }

  @Override
  public void delete(final Player player, final String scoreboardObjectiveId) {
    final var serverPlayerConnection = ((CraftPlayer) player).getHandle().connection;
    final var objective = this.serverScoreboard.getObjective(scoreboardObjectiveId);
    // The objective for this player's scoreboard never will be null at this point.
    this.serverScoreboard.removeObjective(objective);
    // Scoreboard objective packets initialization with removal params and sending.
    this.clientboundSetObjectivePacket = new ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_REMOVE);
    serverPlayerConnection.send(this.clientboundSetObjectivePacket);
  }

  private int scoreByLineNumber(final int line, final Component... lines) {
    return lines.length - line - 1;
  }
}
