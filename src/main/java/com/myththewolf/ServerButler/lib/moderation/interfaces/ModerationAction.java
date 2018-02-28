package com.myththewolf.ServerButler.lib.moderation.interfaces;

import com.myththewolf.ServerButler.lib.player.impl.IMythPlayer;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.joda.time.DateTime;

import java.util.Optional;

/**
 * This interface represents a moderation action
 */
public interface ModerationAction {
    String getReason();

    default Optional<MythPlayer> getTargetUser() {
        return Optional.empty();
    }

    Optional<MythPlayer> getModeratorUser();

    default public Optional<String> getTargetIP() {
        return Optional.empty();
    }

    default Optional<String> getExpireDateString(){ return Optional.empty(); }
    DateTime getDateApplied();
    ActionType getActionType();

    TargetType getTargetType();

    public String getDatabaseID();

    default boolean isFromConsole() {
        return !getModeratorUser().isPresent();
    }

    default boolean isFromPlayer() {
        return !isFromConsole();
    }

    default boolean targetIsPlayer() {
        return getTargetType().equals(TargetType.BUKKIT_PLAYER);
    }

    default boolean targetIsIP() {
        return !targetIsPlayer();
    }

}
