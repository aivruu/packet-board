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
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link VersionPacketProviderModel} implementation for internal packets-handling for scoreboards
 * functions for Minecraft {@code 1.21.1} version.
 *
 * @since 1.0.0
 */
public class AdaptationModule implements VersionPacketProviderModel {
  private static final Map<String, ClientboundSetScorePacket[]> SET_SCORE_PACKET_BY_UUID = new ConcurrentHashMap<>();
  private static final Optional<NumberFormat> NUMBER_FORMAT = Optional.of(BlankFormat.INSTANCE);
  private final Scoreboard scoreboard = MinecraftServer.getServer().getScoreboard();

  @Override
  public void create(final Player player, final String scoreboardObjectiveId, final Component title, final Component... lines) {
    final var serverPlayerConnection = ((CraftPlayer) player).getHandle().connection;
    // Scoreboard objectives declaration and packet-sending.
    final var objective = this.scoreboard.addObjective(scoreboardObjectiveId, ObjectiveCriteria.DUMMY,
      new AdventureComponent(title), ObjectiveCriteria.RenderType.INTEGER, false, BlankFormat.INSTANCE);
    serverPlayerConnection.send(new ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_REMOVE));
    serverPlayerConnection.send(new ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_ADD));
    serverPlayerConnection.send(new ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, objective));
    // Lines processing for set-score-packets and sending.
    for (final var clientboundSetScorePacket : this.processLinesToPacketArray(player, objective, lines)) {
      serverPlayerConnection.send(clientboundSetScorePacket);
    }
  }

  private void sendObjectivePackets(final ServerPlayerConnection serverPlayerConnection, final Objective objective) {
    // The objective for this player's scoreboard shouldn't be null at this point.
    serverPlayerConnection.send(new ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_REMOVE));
    serverPlayerConnection.send(new ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_ADD));
    serverPlayerConnection.send(new ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, objective));
  }

  @Override
  public void sendLines(final Player player, final String scoreboardObjectiveId, final Component... lines) {
    final var serverPlayerConnection = ((CraftPlayer) player).getHandle().connection;
    final var objective = this.scoreboard.getObjective(scoreboardObjectiveId);
    this.sendObjectivePackets(serverPlayerConnection, objective);
    for (final var clientboundSetScorePacket : this.processLinesToPacketArray(player, objective, lines)) {
      serverPlayerConnection.send(clientboundSetScorePacket);
    }
  }

  @Override
  public void sendLine(final Player player, final int line, final Component text, final String scoreboardObjectiveId) {
    final var serverPlayerConnection = ((CraftPlayer) player).getHandle().connection;
    final var objective = this.scoreboard.getObjective(scoreboardObjectiveId);
    this.sendObjectivePackets(serverPlayerConnection, objective);
    this.processLineToPacketArray(player, objective, line, text);
    for (final var clientboundSetScorePacket : this.processLineToPacketArray(player, objective, line, text)) {
      serverPlayerConnection.send(clientboundSetScorePacket);
    }
  }

  @Override
  public void sendTitle(final Player player, final Component title, final String scoreboardObjectiveId) {
    final var objective = this.scoreboard.getObjective(scoreboardObjectiveId);
    // The objective for this player's scoreboard never will be null at this point.
    objective.setDisplayName(new AdventureComponent(title));
    ((CraftPlayer) player).getHandle().connection.send(new ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_CHANGE));
  }

  @Override
  public void delete(final Player player, final String scoreboardObjectiveId) {
    final var objective = this.scoreboard.getObjective(scoreboardObjectiveId);
    // The objective for this player's scoreboard never will be null at this point.
    this.scoreboard.removeObjective(objective);
    SET_SCORE_PACKET_BY_UUID.remove(player.getUniqueId().toString());
    ((CraftPlayer) player).getHandle().connection.send(new ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_REMOVE));
  }

  private ClientboundSetScorePacket[] processLinesToPacketArray(final Player player, final Objective objective, final Component[] lines) {
    final var packetArray = new ClientboundSetScorePacket[lines.length];
    for (byte i = 0 ; i < packetArray.length ; i++) {
      packetArray[i] = new ClientboundSetScorePacket(player.getName(), objective.getName(), (lines.length - i),
        Optional.of(new AdventureComponent(lines[i])), NUMBER_FORMAT);
    }
    SET_SCORE_PACKET_BY_UUID.put(player.getUniqueId().toString(), packetArray);
    return packetArray;
  }

  private ClientboundSetScorePacket[] processLineToPacketArray(final Player player, final Objective objective, final int number,
                                                               final Component line) {
    final var packetArray = SET_SCORE_PACKET_BY_UUID.get(player.getUniqueId().toString());
    for (byte i = 0 ; i < packetArray.length ; i++) {
      if (i != number) {
        continue;
      }
      packetArray[i] = new ClientboundSetScorePacket(player.getName(), objective.getName(), number, Optional.of(new AdventureComponent(line)), NUMBER_FORMAT);
      break;
    }
    return packetArray;
  }
}
