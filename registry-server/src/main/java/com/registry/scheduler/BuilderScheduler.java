package com.registry.scheduler;

import com.registry.repository.image.Tag;
import com.registry.repository.image.TagRepository;
import com.registry.service.ExternalAPIService;
import com.registry.service.TagService;
import com.registry.util.RestApiUtil;
import org.apache.tomcat.jni.Local;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by boozer on 2019. 7. 15
 */
@Component
public class BuilderScheduler {
    private @Value("${builder.redis.key}") String builderKey;
    private @Value("${builder.redis.health-uri}") String builderHealthUri;
    private @Value("${builder.redis.connect-timeout}") int builderConnectTimeout;

    protected static final Logger logger = LoggerFactory.getLogger(BuilderScheduler.class);

    @Autowired
    private ExternalAPIService externalService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepo;

    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOperations;

    // builder의 image, tag 동기화 모듈 (30분 마다)
    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void syncWithBuilder() {
        externalService.syncWithBuilder();
    }

    // builder health check (30초 마다)
    @Scheduled(fixedDelay = 1000 * 30)
    public void checkBuilderList() {
        // redis 에서 builder server 목록 조회
        // "{\"builders\":[{\"port\":4293,\"host\":\"196.35.49.254\"}]}"
        String buildersJSON = valueOperations.get(builderKey);
        JSONArray builders;
        JSONArray connectedBuilders = new JSONArray();
        JSONObject connectedBuilderList = new JSONObject();

        JSONParser parser = new JSONParser();
        try {
            JSONObject buildersObj = (JSONObject) parser.parse(buildersJSON);
            builders = (JSONArray) buildersObj.get("builders");

            builders.stream().forEach(value -> {
                JSONObject object = (JSONObject) value;
                String host = String.valueOf(object.get("host"));
                Long port = (Long) object.get("port");
                String url = "http://" + host + ":" + port + builderHealthUri;

                logger.info("check builder health : {}", url);
                HttpURLConnection connection = null;
                try {
                    URL siteURL = new URL(url);
                    connection = (HttpURLConnection) siteURL.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(builderConnectTimeout);
                    connection.connect();

                    int code = connection.getResponseCode();
                    if (code == 200) {
                        connectedBuilders.add(value);
                    }
                    connection.disconnect();
                    logger.info("result code : {}", code);
                } catch (Exception e) {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    logger.error("{} {} : {}", e.getMessage(), host, port);
                }

            });

        } catch (ParseException e) {
            logger.error("json parsing error");
        } catch (Exception e) {
            logger.error("other exception");
        } finally {
            connectedBuilderList.put("builders", connectedBuilders);
            String server = connectedBuilderList.toJSONString();
            logger.info("live builders : {}", server);
            valueOperations.set(builderKey, server);
        }

    }

    // 만료일이 지난 tag 삭제 모듈 (일배치)
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanUpTag() {
        logger.info("cleanUpTag start");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime date = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);
        List<Tag> tags = tagRepo.getAllDeleteTagsByExpired(date);

        tags.stream().forEach(value -> {
            logger.info("namespace : {}", value.getImage().getNamespace());
            logger.info("image name : {}", value.getImage().getName());
            logger.info("tag name : {}", value.getName());
            logger.info("expiration : {}", value.getExpiration());

            try {
                LocalDateTime expiration = LocalDateTime.parse(value.getExpiration(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                tagService.deleteTag(value.getImage().getNamespace(), value.getImage().getName(), value.getName(), expiration);
            } catch (Exception e) {
                logger.error("Fail cleanUpTag");
                logger.error("namespace : {}", value.getImage().getNamespace());
                logger.error("image name : {}", value.getImage().getName());
                logger.error("tag name : {}", value.getName());
                logger.error(e.getMessage());
            }
        });
    }

}
