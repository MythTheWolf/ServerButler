package com.myththewolf.ServerButler.commands.admin.discord;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.DiscordCommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.util.Optional;
import java.util.stream.Collectors;

public class eval extends DiscordCommandAdapter {
    private ServerButler inst;
    public eval(ServerButler instance){
        this.inst = instance;
    }
    @Override
    public void onCommand(Message orgin, TextChannel channel, Server server, Optional<MythPlayer> sender, String[] args) {
        if (args.length < 1) {
            reply("**Usage:** ;eval code");
        }
        if(!orgin.getAuthor().getName().equals("MythTheWolf")){
            reply(":x: You do not have permission.");
            return;
        }
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        StringBuilder header = new StringBuilder();
        header.append("load(\"nashorn:mozilla_compat.js\");");
        String[] packagesToImport = { "com.myththewolf.ServerButler.lib.cache;","com.myththewolf.ServerButler.lib.MythUtils","com.myththewolf.ServerButler.lib.config"};
        for(String packagE : packagesToImport){
            header.append("importPackage('" + packagE + "');");
        }
        engine.put("sb", inst);
        engine.put("pl",sender.get());
        engine.put("DataCacheClass", DataCache.class);
        engine.put("BukkitClass", Bukkit.class);
        engine.put("ConfigClass", ConfigProperties.class);
        header.append("var dc = DataCacheClass.static;");
        header.append("var bk = BukkitClass.static;");
        header.append("var cp = ConfigClass.static;");
        header.append(StringUtils.arrayToString(0, args));
        String answer = "?";
        String type = "?";
        try {
            Object res = engine.eval(header.toString());
            if(res == null){
              answer = "null";
            type = null;
            }else{
                answer = res.toString();
                type = res.getClass().getSimpleName();
            }
        }catch (ScriptException e){
            reply(":bomb: ```" + e.getMessage()+"```");
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(orgin.getAuthor());
        embedBuilder.setTitle("Java Eval");
        embedBuilder.addField(":printer: Result:","```"+answer+"```",false);
        embedBuilder.addField(":wrench: Result Type:","```"+type+"```",false);
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setFooter("ServerButler version "+ServerButler.plugin.getDescription().getVersion());
        channel.sendMessage(embedBuilder);
    }
}
