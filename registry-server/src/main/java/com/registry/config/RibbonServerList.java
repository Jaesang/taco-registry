package com.registry.config;

import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by exntu on 12/08/2019.
 */
public class RibbonServerList implements ServerList<Server> {

    protected static final Logger logger = LoggerFactory.getLogger(RibbonServerList.class);

    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOperations;

    @Override
    public final List<Server> getUpdatedListOfServers() {
        // ribbon.ServerListRefreshInterval 에 정의된 interval 값에 따라 서버 목록을 업데이트한다

        // redis 에서 builder server 목록 조회
        // "{\"builders\":[{\"port\":4293,\"host\":\"196.35.49.254\"}]}"
        String buildersJSON = valueOperations.get("builderList");
        JSONArray builders;

        JSONParser parser = new JSONParser();
        List<Server> servers = new ArrayList<>();
        try {
            JSONObject buildersObj = (JSONObject) parser.parse(buildersJSON);
            builders = (JSONArray) buildersObj.get("builders");
            servers = (List<Server>) builders.stream().map(value -> {
                JSONObject object = (JSONObject) value;
                String host = String.valueOf(object.get("host"));
                Long port = (Long) object.get("port");
                return new Server(host, port.intValue());
            }).collect(Collectors.toList());

        } catch (ParseException e) {
            logger.error("json parsing error");
        } catch (Exception e) {
            logger.error("other exception");
        }

        return servers;
    }

    @Override
    public final List<Server> getInitialListOfServers() {
        return getUpdatedListOfServers();
    }
}
