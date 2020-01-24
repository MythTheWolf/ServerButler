package com.myththewolf.ServerButler.commands.player.discord;

import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.DiscordCommandAdapter;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class link extends DiscordCommandAdapter implements SQLAble {
    @Override
    public void onCommand(Message orgin, TextChannel channel, Server server, Optional<MythPlayer> sender, String[] args) {
        if (args.length < 1) {
            reply("**Usage:** ;link <token>");
            return;
        }
        if (sender.flatMap(MythPlayer::getDiscordID).isPresent()) {
            reply(":x: You already are linked!");
            return;
        }
        reply(":mag: Searching database...");
        ResultSet resultSet = prepareAndExecuteSelectExceptionally("SELECT * FROM `SB_Discord` WHERE `token` = ?", 1, args[0]);
        try {
            if (resultSet.next()) {
                String UUID = resultSet.getString("UUID");
                MythPlayer mp = DataCache.getPlayer(UUID).orElseThrow(IllegalStateException::new);
                reply(":white_check_mark: Hello," + mp.getName() + "! (UUID: " + mp.getUUID() + ")");
                reply(":warning: Please Re-Join the minecraft server in order to re-fresh discord permissions.");
                mp.setDiscordID(orgin.getAuthor().getIdAsString());
                mp.updatePlayer();
                return;
            } else {
                reply(":warning: Invalid token! (0 Results)");
                return;
            }
        } catch (SQLException e) {
            reply(":bomb: **ERROR:** " + e.getMessage());
        }
    }
}
