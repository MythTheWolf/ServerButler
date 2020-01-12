package com.myththewolf.ServerButler.lib.webserver;

import org.json.JSONArray;

import javax.servlet.http.HttpServletRequest;


public interface JSONCallback {
    JSONArray processValidatedInput(HttpServletRequest request);
}
