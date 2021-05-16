package com.myththewolf.ServerButler.lib.logging;


import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import org.apache.commons.lang3.StringUtils;


public class ConsoleMessageQueueWorker extends Thread {

    public ConsoleMessageQueueWorker() {
        super("ServerButler console queue worker");
    }

    @Override
    public void run() {
        while (true) {
            try {
                // don't process, if we get disconnected, another measure to prevent UnknownHostException spam

                StringBuilder message = new StringBuilder();
                String line = ServerButler.getInstance().getConsoleMessageQueue().poll();
                while (line != null) {
                    if (message.length() + line.length() + 1 > 2000) {
                        DataCache.getConsoleChannel().pushRaw(message.toString());
                        message = new StringBuilder();
                    }
                    message.append(line).append("\n");

                    line = ServerButler.getInstance().getConsoleMessageQueue().poll();
                }

                if (StringUtils.isNotBlank(message.toString().replace("\n", "")))
                    DataCache.getConsoleChannel().pushRaw(message.toString());

                // make sure rate isn't less than every second because of rate limitations
                // even then, a console channel update /every second/ is pushing it
                int sleepTime = 5 * 1000;

                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                ServerButler.getInstance().getLogger().severe("Broke from Console Message Queue Worker thread: interrupted");
                return;
            }
        }
    }

}