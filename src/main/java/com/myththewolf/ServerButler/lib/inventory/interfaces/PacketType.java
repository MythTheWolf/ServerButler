package com.myththewolf.ServerButler.lib.inventory.interfaces;

/**
 * A enumeration for all packet types
 *
 */
public enum PacketType {
    TOGGLE_CHANNEL_ON,
    TOGGLE_CHANNEL_OFF,
    VIEW_CHANNEL_OPTIONS,
    SET_WRITE_CHANNEL,
    VIEW_PLAYER_OPTIONS,
    BAN_PLAYER,
    TEMPBAN_PLAYER,
    PARDON_PLAYER,
    MUTE_PLAYER,
    SOFTMUTE_PLAYER,
    UNMUTE_PLAYER,
    DELETE_PLAYER,
    KICK_PLAYER,
    VIEW_PLAYER_HISTORY
}
