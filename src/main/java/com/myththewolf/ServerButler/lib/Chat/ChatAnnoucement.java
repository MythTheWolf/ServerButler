package com.myththewolf.ServerButler.lib.Chat;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import org.bukkit.Bukkit;
import org.joda.time.Period;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatAnnoucement implements SQLAble, Loggable {
    private String content = "<<no content given>>";

    private List<ChatChannel> destinations = new ArrayList<ChatChannel>();


    private String requiredPerm = null;

    private Period interval;
    private String id = null;

    private String taskID = null;

    public ChatAnnoucement(String ID) {
        try {
            taskID = null;
            ResultSet resultSet = prepareAndExecuteSelectExceptionally("SELECT * FROM `SB_Announcements` WHERE `ID` = ?", 1, ID);
            while (resultSet.next()) {
                this.content = resultSet.getString("content");
                StringUtils.deserializeArray(resultSet.getString("channels")).stream().map(ChatChannel::new)
                        .forEach(destinations::add);
                this.requiredPerm = resultSet.getString("permission");
                interval = TimeUtils.TIME_INPUT_FORMAT().parsePeriod(resultSet.getString("time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ChatChannel> getDestinations() {
        return destinations;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Period getInterval() {
        return interval;
    }

    public void setInterval(Period interval) {
        this.interval = interval;
    }

    public String getRequiredPerm() {
        return requiredPerm;
    }

    public void setRequiredPerm(String requiredPerm) {
        this.requiredPerm = requiredPerm;
    }

    public void createTask() {
        long ticks = (getInterval().getDays() * 20);
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(ServerButler.plugin, () -> getDestinations()
                .forEach(chatChannel -> chatChannel.push(this)), ticks, 2) + "";
    }

    public String getId() {
        return id;
    }

    public String getTaskID() {
        return taskID;
    }

    public void stopTask() {
        if (isRunning()) {
            Bukkit.getScheduler().cancelTask(Integer.parseInt(getTaskID()));
            taskID = null;
        }
    }

    public void addChannel(ChatChannel chatChannel) {
        if (!destinations.contains(chatChannel)) {
            destinations.add(chatChannel);
        }
    }

    public void removeChannel(ChatChannel chatChannel) {
        destinations.remove(chatChannel);
    }

    public void update() {
        if (getId() == null) {
            this.id = prepareAndExecuteUpdateExceptionally("INSERT INTO `SB_Announcements` (`ID`, `content`, `channels`, `permission`, `time`) VALUES (NULL, ?, ?, ?, ?)", 4, getContent(), StringUtils
                    .serializeArray(getDestinations().stream().map(ChatChannel::getID).collect(Collectors
                            .toList())), getRequiredPerm(), TimeUtils.TIME_INPUT_FORMAT().print(getInterval())) + "";
        } else {
            prepareAndExecuteUpdateExceptionally("UPDATE `SB_Announcements` SET `content` = ? , `channels` = ? , `permission` =  ? , `time` = ? WHERE `ID` = ? ", 4, getContent(), StringUtils
                    .serializeArray(getDestinations().stream().map(ChatChannel::getID).collect(Collectors
                            .toList())), getRequiredPerm(), TimeUtils.TIME_INPUT_FORMAT()
                    .print(getInterval()), getId());
        }
    }

    public void delete() {
        prepareAndExecuteUpdateExceptionally("DELETE FROM `SB_Announcements` WHERE `ID` = ?", 1, getId());
        DataCache.annoucementHashMap.remove(getId());
    }

    public boolean isRunning() {
        return (getTaskID() != null);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ChatAnnoucement && ((ChatAnnoucement) obj).getId().equals(getId());
    }
}
