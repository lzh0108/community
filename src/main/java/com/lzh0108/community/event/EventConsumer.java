package com.lzh0108.community.event;

import com.alibaba.fastjson.JSONObject;
import com.lzh0108.community.entity.DiscussPost;
import com.lzh0108.community.entity.Event;
import com.lzh0108.community.entity.Message;
import com.lzh0108.community.service.DiscussPostService;
import com.lzh0108.community.service.ElasticsearchService;
import com.lzh0108.community.service.MessageService;
import com.lzh0108.community.util.CommunityConstant;
import com.lzh0108.community.util.CommunityUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Value("${wk.image.command}")
    private String wkImageCommand;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    // 用户凭证
    @Value("${qiniu.key.access}")
    private String accessKey;

    // 加密秘钥
    @Value("${qiniu.key.secret}")
    private String secretKey;

    // share空间名字
    @Value("${qiniu.bucket.share.name}")
    private String shareBucketName;

    // 可以执行定时任务的spring线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    // 发送系统通知，消费评论、关注、点赞事件
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {

        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));

        messageService.addMessage(message);

    }

    // 消費发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return;
        }

        DiscussPost discussPost = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(discussPost);

    }

    // 消費删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return;
        }

        elasticsearchService.deleteDiscussPost(event.getEntityId());

    }

    // 消费分享事件
    @KafkaListener(topics = TOPIC_SHARE)
    public void handleShareMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误！");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        // 完整命令
        String cmd = wkImageCommand + " --quality 75 " + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;

        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("生成长图成功：" + cmd);
        } catch (IOException e) {
            logger.error("生成长图失败：" + e.getMessage());
        }

        // Runtime.getRuntime().exec(cmd);语句的执行需要时间
        // 使用定时器来等待命令的完成，启动定时器，来监视该图片，一旦生成了，则上传至七牛云
        UploadTask task = new UploadTask(fileName, suffix);
        // 每隔500毫秒执行一次，future里面封装了任务的状态，也可以用来停止定时器
        Future future = taskScheduler.scheduleAtFixedRate(task, 500);
        task.setFuture(future);

    }

    // 任务失败有两种情况，一是本地生成图片失败（命令无法执行成功），二是上传到七牛云失败（七牛云服务器挂掉）

    class UploadTask implements Runnable {

        // 文件名称
        private String fileName;
        // 文件后缀
        private String suffix;
        // 启动任务的返回值，可以用来停止定时器
        private Future future;
        // 开始时间
        private long startTime;
        // 上传次数
        private int uploadTimes;

        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        @Override
        public void run() {
            // 生成图片失败（过了30秒还没有生成图片成功）
            if (System.currentTimeMillis() - startTime > 30000) {
                logger.error("执行时间过长，终止任务：" + fileName);
                // 停止定时器
                future.cancel(true);
                return;
            }
            // 上传失败
            if (uploadTimes >= 3) {
                logger.error("上传次数过多，终止任务：" + fileName);
                // 停止定时器
                future.cancel(true);
                return;
            }

            String path = wkImageStorage + "/" + fileName + suffix;
            File file = new File(path);
            if (file.exists()) {
                logger.info(String.format("开始第%d上传[%s].", ++uploadTimes, fileName));

                // 设置响应信息
                StringMap policy = new StringMap();
                policy.put("returnBody", CommunityUtil.getJSONString(0));
                // 生产上传凭证
                Auth auth = Auth.create(accessKey, secretKey);
                // 上传的空间的名字，文件名，过期时间（这里为3600s），响应信息
                String uploadToken = auth.uploadToken(shareBucketName, fileName, 3600, policy);

                // 指定上传的机房
                UploadManager manager = new UploadManager(new Configuration(Region.region2()));

                try {
                    // 上传文件
                    Response response = manager.put(
                            path, fileName, uploadToken, null, "image/" + suffix.substring(suffix.lastIndexOf(".") + 1), false);
                    // 处理响应结果
                    JSONObject json = JSONObject.parseObject(response.bodyString());
                    // 如果返回的数据，要么是空，要么没有code，要么code不对
                    if (json == null || json.get("code") == null || !json.get("code").toString().equals("0")) {
                        logger.info(String.format("第%d次上传失败[%s].", uploadTimes, fileName));
                    } else {// 否则，则说明任务成功
                        logger.info(String.format("第%d次上传成功[%s].", uploadTimes, fileName));
                        // 停止定时器
                        future.cancel(true);
                    }
                } catch (QiniuException e) {
                    logger.info(String.format("第%d次上传失败[%s].", uploadTimes, fileName));
                }
            } else {
                logger.info("等待图片生成[" + fileName + "].");
            }
        }
    }

}
