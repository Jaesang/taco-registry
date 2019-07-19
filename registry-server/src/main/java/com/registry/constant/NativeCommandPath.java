package com.registry.constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NativeCommandPath {

    @Autowired
    private ApplicationConstant applicationConstant;

    // default
    public String getDefaultDir() {
        return applicationConstant.getAnsible().get("dir");
    }

    // cluster 경로
    public String getClusterDir() {
        return getDefaultDir() + "/{0}";
    }

    // kubespray 경로
    public String getKubeSprayDir() {
        return getClusterDir() + "/kubespray";
    }

    // oreo-kargo 경로
    public String getOreoKargoDir() {
        return getClusterDir() + "/oreo-kargo";
    }

    // inventory 경로
    public String getInventoryDir() {
        return getClusterDir() + "/inventory";
    }

    // inventory 파일
    public String getInventoryFile() {
        return getInventoryDir() + "/{1}.cfg";
    }

    // inventory 별 저장 config
    public String getInventoryConfigFile() {
        return getInventoryDir() + "/{1}.config";
    }

    // inventory 별 저장 config
    public String getInventoryPrivateKeyFile() {
        return getInventoryDir() + "/{1}.pem";
    }

    // execution log 경로
    public String getExecutionLogDir() {
        return getClusterDir() + "/log";
    }

    // execution 로그 파일
    public String getExecutionLogFile() {
        return getExecutionLogDir() + "/log.log";
    }
}
