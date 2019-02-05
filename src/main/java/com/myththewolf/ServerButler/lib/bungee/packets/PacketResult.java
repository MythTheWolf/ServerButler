package com.myththewolf.ServerButler.lib.bungee.packets;

import org.json.JSONObject;

public class PacketResult {
    private boolean error;
    private String dataSent;
    private String host;
    private int port;
    private String message;

    public PacketResult(JSONObject ret, String host, int port, String dataSent) {
        this.host = host;
        this.port = port;
        this.dataSent = dataSent;
        message = ret.getString("message");
        error = ret.getBoolean("error");
    }

    public boolean isError() {
        return error;
    }

    public String getDataSent() {
        return dataSent;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getMessage() {
        return message;
    }
}
