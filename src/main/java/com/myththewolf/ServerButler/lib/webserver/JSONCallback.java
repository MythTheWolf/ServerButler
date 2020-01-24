package com.myththewolf.ServerButler.lib.webserver;

import javax.servlet.http.HttpServletRequest;


public interface JSONCallback {
    Object processValidatedInput(HttpServletRequest request);
}
