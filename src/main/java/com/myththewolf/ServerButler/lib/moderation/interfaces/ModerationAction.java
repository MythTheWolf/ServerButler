package com.myththewolf.ServerButler.lib.moderation.interfaces;

import com.myththewolf.ServerButler.lib.player.impl.MythPlayer;

import java.util.Optional;

/**
 * This interface represents a moderation action
 */
public interface ModerationAction {
    public String getReason();
    public Optional<MythPlayer> getTargetUser();
    public Optional<MythPlayer> getModeratorUser();
    public Optional<String> getTargetIP();
    public Optional<String> getExpireDateString();
    public ActionType getActionType();
    public TargetType getTargetType();
    public String getDatabaseID();
    default boolean isFromConsole(){
        return !getModeratorUser().isPresent();
    }

    default boolean isFromPlayer(){
        return !isFromConsole();
    }

    default boolean targetIsPlayer(){
        return getTargetType().equals(TargetType.BUKKIT_PLAYER);
    }
    default boolean targetIsIP(){
        return !targetIsPlayer();
    }

}
