package com.myththewolf.ServerButler.lib.event.player;

@FunctionalInterface
public interface DirectCommandInput {
    void onInput(String content);
}
