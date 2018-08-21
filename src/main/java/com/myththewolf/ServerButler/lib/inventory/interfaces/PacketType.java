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
    //PLAYER
    BAN_PLAYER,
    TEMPBAN_PLAYER,
    PARDON_PLAYER,
    MUTE_PLAYER,
    SOFTMUTE_PLAYER,
    UNMUTE_PLAYER,
    DELETE_PLAYER, //TODO: Delete player
    KICK_PLAYER,
    VIEW_PLAYER_HISTORY, //TODO: Player History
    VIEW_PLAYER_EXTA_INFO, //TODO: Player extra info
    //IPS
    VIEW_PLAYER_IPS,
    VIEW_IP_OPTIONS,
    BAN_IP,
    PARDON_IP,
    TEMPBAN_IP,
    DELETE_IP, //TODO: Delete IP
    LIST_PLAYERS,
    VIEW_IP_HISTORY, //TODO: IP History
    VIEW_IP_EXTRA_INFO //TODO: IP Exta info
}
