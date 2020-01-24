package com.myththewolf.ServerButler.lib.moderation.interfaces;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.Optional;

/**
 * This interface represents a moderation action
 */
public interface ModerationAction {
    /**
     * Gets the reason for the action applied
     *
     * @return The reason
     */
    String getReason();

    /**
     * Gets the target user
     *
     * @return A optional, empty if the target type is not a user or is not present
     */
    default Optional<MythPlayer> getTargetUser() {
        return Optional.empty();
    }

    /**
     * Gets the moderator who applied the action
     *
     * @return A optional, empty if the action was applied using the console
     */
    Optional<MythPlayer> getModeratorUser();


    /**
     * Gets the target IP address
     *
     * @return A optional, empty if the target type is not a IP address or is not present
     */
    default Optional<PlayerInetAddress> getTargetIP() {
        return Optional.empty();
    }

    /**
     * Gets the expire date & time of the action
     *
     * @return A optional,empty if the action does not expire
     */
    default Optional<DateTime> getExpireDate() {
        return Optional.empty();
    }

    /**
     * Gets the date & time of when the action was applied.
     *
     * @return The date/time
     */
    DateTime getDateApplied();

    /**
     * Gets the action type of this instance
     *
     * @return The type
     */
    ActionType getActionType();

    /**
     * Gets the target type of this instance
     *
     * @return The target type
     */
    TargetType getTargetType();

    /**
     * Gets the ID of this instance
     *
     * @return
     */
    String getDatabaseID();

    /**
     * Checks if the action was applied via the console
     *
     * @return True if the action was applied from console
     */
    default boolean isFromConsole() {
        return !getModeratorUser().isPresent();
    }

    /**
     * Checks if the action was applied via a player
     *
     * @return True if the action was applied from a player
     */
    default boolean isFromPlayer() {
        return !isFromConsole();
    }

    /**
     * Checks if the target is a player
     *
     * @return True if the target is a player
     */
    default boolean targetIsPlayer() {
        return getTargetType().equals(TargetType.BUKKIT_PLAYER);
    }

    /**
     * Checks if the target is a IP address
     *
     * @return True if the target is a IP address
     */
    default boolean targetIsIP() {
        return !targetIsPlayer();
    }

    default JSONObject toJSON() {
        JSONObject object = new JSONObject();
        object.put("targetType", getTargetType());
        object.put("target", targetIsPlayer() ? getTargetUser().orElseThrow(IllegalStateException::new) : getTargetIP().orElseThrow(IllegalStateException::new));
        object.put("reason", getReason());
        object.put("moderator", getModeratorUser().isPresent() ? getModeratorUser().get().getUUID() : JSONObject.NULL);
        object.put("expireDate", getExpireDate().isPresent() ? getExpireDate().map(TimeUtils::dateToString).get() : JSONObject.NULL);
        object.put("dateApplied", TimeUtils.dateToString(getDateApplied()));
        object.put("ID", getDatabaseID());
        object.put("actionType", getActionType());
        ServerButler.getInstance().getLogger().warning(object.toString(4));
        return object;
    }
}
