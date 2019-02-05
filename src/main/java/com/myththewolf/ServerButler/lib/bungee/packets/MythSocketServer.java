package com.myththewolf.ServerButler.lib.bungee.packets;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MythSocketServer implements Loggable, Runnable {
    //static ServerSocket variable
    private ServerSocket server;
    //socket server port on which it will listen
    private int port;

    public MythSocketServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
        getLogger().info("START!");
        server = new ServerSocket(port);
            while (true) {
                debug(ConfigProperties.SERVER_NAME + ": Listening for socket events on port " + port);
                //creating socket and waiting for client connection
                Socket socket = server.accept();
                //read from socket to ObjectInputStream object
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //convert ObjectInputStream object to String
                String message = (String) ois.readObject();
                debug(ConfigProperties.SERVER_NAME + ": Got imbound socket packet: " + message);
                //create ObjectOutputStream object
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                //write object to Socket
                JSONObject parse = new JSONObject(message);
                JSONObject ret = new JSONObject();
                if (!parse.has("packetType") || !ServerButler.bungeePacketHandlers.containsKey(BungeePacketType.valueOf(parse.getString("packetType")))) {
                    ret.put("error", true);
                    String s = parse.has("packetType") ? parse.getString("packetType") : "[NONE]";
                    ret.put("message", "Unknown packet type: " + s);
                } else {
                    ret.put("error", false);
                    BungeePacketType type = BungeePacketType.valueOf(parse.getString("packetType"));
                    parse.remove("packetType");
                    ServerButler.bungeePacketHandlers.get(type).forEach(handler -> handler.onPacket(parse));
                }
                oos.writeObject(ret.toString(4));
                //close resources
                ois.close();
                oos.close();
                socket.close();
                //terminate the server if client sends exit request
                if (message.equalsIgnoreCase("exit")) break;
            }
            System.out.println("Shutting down Socket server!!");
            //close the ServerSocket object
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
