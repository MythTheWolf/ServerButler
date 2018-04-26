package com.myththewolf.ServerButler.lib.event.player;

@FunctionalInterface
public interface DirectCommandInput {
    /**
     * Run when the user commits text
     * @param content
     */
    void onInput(String content);
}
