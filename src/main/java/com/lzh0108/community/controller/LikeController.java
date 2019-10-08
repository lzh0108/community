package com.lzh0108.community.controller;

import com.lzh0108.community.entity.Event;
import com.lzh0108.community.entity.User;
import com.lzh0108.community.event.EventProducer;
import com.lzh0108.community.service.LikeService;
import com.lzh0108.community.util.CommunityConstant;
import com.lzh0108.community.util.CommunityUtil;
import com.lzh0108.community.util.HostHolder;
import com.lzh0108.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {

        User user = hostHolder.getUser();

        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // 点赞数量
        long lieCount = likeService.findEntityLikeCount(entityType, entityId);

        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        // 返回的结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", lieCount);
        map.put("likeStatus", likeStatus);

        // 触发点赞事件
        // 点赞时通知，取消点赞时不通知
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        // 只有对帖子点赞时，才需计算帖子分数
        if (entityType == ENTITY_TYPE_POST) {
            // 计算帖子分数，将其加入缓存队列
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }
        return CommunityUtil.getJSONString(0, null, map);

    }

}
