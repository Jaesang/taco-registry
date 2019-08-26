package com.registry.scheduler;

import com.registry.service.ExternalAPIService;
import com.registry.util.RestApiUtil;
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

import javax.annotation.Resource;
import java.net.HttpURLConnection;
import java.net.URL;

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

    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOperations;

    @Autowired
    private RestApiUtil restApiUtil;

    // 실행에 대한 결과 등과 같은 정보 동기화 모듈 (10분 마다)
    @Scheduled(fixedDelay = 1000 * 60 * 10)
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

                try {
                    URL siteURL = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(builderConnectTimeout);
                    connection.connect();

                    int code = connection.getResponseCode();
                    if (code == 200) {
                        connectedBuilders.add(value);
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    logger.error(e.getMessage() + " " + host + " : " + port);
                }

            });

            connectedBuilderList.put("builders", connectedBuilders);
            String server = connectedBuilderList.toJSONString();
            valueOperations.set(builderKey, server);

        } catch (ParseException e) {
            logger.error("json parsing error");
        } catch (Exception e) {
            logger.error("other exception");
        }

    }
}
