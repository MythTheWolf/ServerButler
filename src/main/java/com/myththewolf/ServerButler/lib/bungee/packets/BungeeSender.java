package com.myththewolf.ServerButler.lib.bungee.packets;


import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public interface BungeeSender{


    default List<PacketResult> sendToAll(BungeePacketType type, JSONObject data) {
        return ConfigProperties.SOCKETS.stream().map(s -> sendTo(s, type, data)).collect(Collectors.toList());
    }

    default PacketResult sendTo(String server, BungeePacketType type, JSONObject data) {

            String[] srv = server.split(":");
            return sendData(srv[0], Integer.parseInt(srv[1]), data.put("packetType", type.toString()));

    }

    default PacketResult sendData(String host, int port, JSONObject data) {
        try {
            Socket socket;
            ObjectOutputStream oos;
            ObjectInputStream ois;
            //establish socket connection to server
            socket = new Socket(host, port);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            writeDebug("Sending data to host " + host + ":" + port + "->" + data);
            oos.writeObject(data.toString());
            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            writeDebug("Got reply from host " + host + ":" + port + "->" + message);
            //close resources
            ois.close();
            oos.close();
            return new PacketResult(new JSONObject(message), host, port, data.toString());
        } catch (Exception e) {
            JSONObject rep = new JSONObject();
            rep.put("error", true);
            rep.put("message", e.getMessage());
            return new PacketResult(rep, host, port, data.toString());
        }

    }

    default void writeDebug(String s) {
        ServerButler.connector.debug(s);
    }

}
