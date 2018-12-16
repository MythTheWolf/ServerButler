package com.myththewolf.ServerButler.lib.bungee.packets;


import com.myththewolf.ServerButler.ServerButler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public interface BungeeSender {
    default void sendToAll(Player player,BungeePacketType type, JSONObject object) {
        sendToAll(player,type, object.toString());
    }

    default void sendToAll(Player player,BungeePacketType type, String data) {
        sendTo(player,"ALL", type, data);
    }

    default void sendTo(Player player,String server, BungeePacketType type, String data) {
        forwardString(type.toString(),server,data,player);
    }
    default void forwardString(String subchannel, String target, String s,Player p){
        try{
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF("Forward");
            out.writeUTF(target);
            out.writeUTF(subchannel); // "customchannel" for example
            byte[] data = s.getBytes();
            out.writeShort(data.length);
            out.write(data);
            p.sendPluginMessage(ServerButler.plugin, "BungeeCord", b.toByteArray());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
