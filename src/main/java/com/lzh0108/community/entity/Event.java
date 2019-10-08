package com.lzh0108.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {

    // 主题
    private String topic;
    // 事件的发起者
    private int userId;
    // 触发的事件实体类型
    private int entityType;
    // 实体id
    private int entityId;
    // 持有实体的用户id
    private int entityUserId;
    // 其他属性，方面以后扩展
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityUserId=" + entityUserId +
                ", data=" + data +
                '}';
    }
}
