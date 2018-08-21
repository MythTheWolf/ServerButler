package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Optional;

public class eval extends CommandAdapter {
    private ServerButler inst;
    public eval(ServerButler instance){
        this.inst = instance;
    }
    @Override
    @CommandPolicy(consoleRequiredArgs = -1,userRequiredArgs = 1,commandUsage = "/eval <java code>")
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        StringBuilder header = new StringBuilder();
        header.append("load(\"nashorn:mozilla_compat.js\");");
        String[] packagesToImport = { "com.myththewolf.ServerButler.lib.cache;","com.myththewolf.ServerButler.lib.MythUtils","com.myththewolf.ServerButler.lib.config"};
        for(String packagE : packagesToImport){
            header.append("importPackage('packagE');");
        }
        engine.put("sb",inst);
        engine.put("pl",sender.get());
        engine.put("DataCacheClass",DataCache.class);
        header.append("var dc = DataCacheClass.static;");
        header.append(StringUtils.arrayToString(0, args));

        try {
            Object res = engine.eval(header.toString());
            if(res == null){
                reply("null");
            }else{
                reply(res.toString());
            }
        }catch (ScriptException e){
            reply(e.getMessage());
        }
    }
}
