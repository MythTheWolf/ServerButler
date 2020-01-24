package com.myththewolf.ServerButler.commands.player;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class token extends CommandAdapter implements SQLAble {
    @Override
    @CommandPolicy(commandUsage = "/token", userRequiredArgs = 0)
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        if (!sender.isPresent()) {
            return;
        }

        MythPlayer player = sender.get();
        if (player.getDiscordID().isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "You are already linked!");
            return;
        }
        reply(ConfigProperties.PREFIX + "Generating key...");
        String key = StringUtils.getToken(10);
        ComponentBuilder builder = new ComponentBuilder(ConfigProperties.PREFIX + "Done! Use this command in discord: ;" + "link " + key);
        BaseComponent[] components = {new TextComponent("Click to suggest in chat, Cntrl+A then Cntrl+C to copy")};
        builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ";link " + key)).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, components));
        player.getBukkitPlayer().orElseThrow(IllegalStateException::new).spigot().sendMessage(builder.create());

        prepareAndExecuteUpdateExceptionally("INSERT INTO `SB_Discord` (`token`,`UUID`) VALUES (?,?)", 2, key, player
                .getUUID());
    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.LINK_PERMISSION;
    }
}
