package com.registry.service;

import com.registry.constant.Const;
import com.registry.exception.BadRequestException;
import com.registry.exception.ServiceUnavailableException;
import com.registry.exception.UnknownServerException;
import com.registry.repository.image.*;
import com.registry.repository.organization.Organization;
import com.registry.repository.user.*;
import com.registry.util.RestApiUtil;
import com.registry.util.SecurityUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by boozer on 2019. 7. 15
 */
@Service
public class ExternalAPIService extends AbstractService {

    protected static final Logger logger = LoggerFactory.getLogger(ExternalAPIService.class);

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    @Autowired
    private MapperFacade mapper;

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private RestApiUtil restApiUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepo;

    @Autowired
    private TagRepository tagRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserOrganizationRepository userOrgRepo;

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Constructor
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Getter & Setter Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /**
     * 빌더와 싱크
     */
    @Transactional
    public void syncWithBuilder() {
        boolean result = true;

        try {
            URI builderHost = this.getBuilderUri();
            if (builderHost == null) {
                return;
            }

            List<Image> syncImages = new ArrayList<>();
            String url = MessageFormat.format("{0}/v1/registry/repositories/", builderHost);
            logger.info("syncWithBuilder : {}", url);
            LinkedHashMap body = (LinkedHashMap) restApiUtil.excute(url, HttpMethod.GET, null, Object.class);
            List<Map<String, Object>> repositories = (List<Map<String, Object>>) body.get("repositories");
            repositories.stream().forEach(value -> {
                Image image = syncImage(value);
                if (image != null) {
                    syncImages.add(image);
                }
            });

            // 실제 존재하지 않는 Image 삭제 (image 만 만든 상태에서 싱크가 되어 버리면 image가 삭제되기 때문에 막음)
//            List<Image> allImages = imageRepo.getAllImages();
//            allImages.stream().forEach(value -> {
//                List<Image> img = syncImages.stream()
//                        .filter(v -> v.getNamespace().equals(value.getNamespace()) && v.getName().equals(value.getName()))
//                        .collect(Collectors.toList());
//                if (img == null || img.size() == 0) {
//                    imageService.deleteImage(value.getNamespace(), value.getName());
//                }
//            });
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = false;
        }

        if (!result) {
            throw new UnknownServerException("sync fail");
        }
    }

    /**
     * 빌더와 싱크
     */
    @Transactional
    public void syncWithBuilder(String namespace, String imageName) {
        boolean result = true;

        try {
            URI builderHost = this.getBuilderUri();
            if (builderHost == null) {
                return;
            }
            String url = MessageFormat.format("{0}/v1/registry/repositories/{1}/{2}", builderHost, namespace, imageName);
            logger.info("syncWithBuilder : {}", url);
            LinkedHashMap body = (LinkedHashMap) restApiUtil.excute(url, HttpMethod.GET, null, Object.class);
            if (body != null) {
                syncImage(body);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = false;
        }

        if (!result) {
            throw new UnknownServerException("sync fail");
        }
    }

    /**
     * builder URL 조회
     * loadBalancer(ribbon) 사용
     * @return
     */
    public URI getBuilderUri() {
        ServiceInstance serviceInstance=loadBalancer.choose("builder");

        if (serviceInstance != null) {
            return serviceInstance.getUri();
        } else {
            throw new ServiceUnavailableException("No Builder available.");
        }
    }

    /**
     * manifest 정보 조회
     * @param namespace
     * @param name
     * @return
     * @throws Exception
     */
    public Map<String, Object> getManifests(String namespace, String name, String tagName) throws Exception {
        logger.info("getManifests");
        logger.info("namespace : {}", namespace);
        logger.info("name : {}", name);
        logger.info("tagName : {}", tagName);

        String body = (String) restApiUtil.excute(MessageFormat.format("{0}/v1/registry/manifest-v1/{1}/{2}?tag={3}", this.getBuilderUri().toString(), namespace, name, tagName), HttpMethod.GET, null, String.class);
//        String body = "{\"schemaVersion\":1,\"name\":\"calico/node\",\"tag\":\"v3.9.0-0.dev-48-g7e20dfd\",\"architecture\":\"amd64\",\"fsLayers\":[{\"blobSum\":\"sha256:a3ed95caeb02ffe68cdd9fd84406680ae93d633cb16422d00e8a7c22955b46d4\"},{\"blobSum\":\"sha256:67031e282775e4ef54862dc581ac623bc5e634feba8f8519bcc495457013595d\"},{\"blobSum\":\"sha256:e152f73f59bf5238868c547f5cb32c422936fcf673d25298533cac0040ed7e2d\"},{\"blobSum\":\"sha256:95840eb1e35b2e0bd301231e41157b4aa03624d55d8fb170b906fdd751209ce1\"},{\"blobSum\":\"sha256:0277e8d7076eff96d9d1802188130259dfb19e8d5fb2adebcd5af89cfa285c1a\"},{\"blobSum\":\"sha256:3e154b09ff1565c595dd4be6f5b3b3629d0702f3b7b38e0d09b8528d42d16290\"},{\"blobSum\":\"sha256:f977fda89d65fa87a96006543ca8422b52e7d6c55d9a9edd9031868cf196fafb\"},{\"blobSum\":\"sha256:55ec10af57a5f6211749e4fe2910bb909deaa22f41a799663a2e1ab8ae55148e\"},{\"blobSum\":\"sha256:0a692e040419e188867d2b55ef3721b87ae9105780d22b7e62a18ed03d5de6c2\"},{\"blobSum\":\"sha256:652b3267df33b72ccb31aec8d6742745aa02a40f985c7479a0b47f7550fa395b\"},{\"blobSum\":\"sha256:a8f6af2b6891b97e3d70d7e1bb86e2d984898c03082107d7f8599e4e114b48bf\"},{\"blobSum\":\"sha256:a3ed95caeb02ffe68cdd9fd84406680ae93d633cb16422d00e8a7c22955b46d4\"},{\"blobSum\":\"sha256:a3ed95caeb02ffe68cdd9fd84406680ae93d633cb16422d00e8a7c22955b46d4\"},{\"blobSum\":\"sha256:a3ed95caeb02ffe68cdd9fd84406680ae93d633cb16422d00e8a7c22955b46d4\"},{\"blobSum\":\"sha256:1ab2bdfe97783562315f98f94c0769b1897a05f7b0395ca1520ebee08666703b\"}],\"history\":[{\"v1Compatibility\":{\"architecture\":\"amd64\",\"config\":{\"Hostname\":\"\",\"Domainname\":\"\",\"User\":\"\",\"AttachStdin\":false,\"AttachStdout\":false,\"AttachStderr\":false,\"Tty\":false,\"OpenStdin\":false,\"StdinOnce\":false,\"Env\":[\"PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin\"],\"Cmd\":[\"start_runit\"],\"ArgsEscaped\":true,\"Image\":\"sha256:bd8fc24ca86f5a6409a752241501c72c8eaece6f1920b0f63886b18650609c37\",\"Volumes\":null,\"WorkingDir\":\"\",\"Entrypoint\":null,\"OnBuild\":null,\"Labels\":{\"maintainer\":\"Casey Davenport <casey@tigera.io>\"}},\"container\":\"6fe7037991f38d27c0ff02c0436026e7bcabeda534c0a0f2958b8c5be1957214\",\"container_config\":{\"Hostname\":\"6fe7037991f3\",\"Domainname\":\"\",\"User\":\"\",\"AttachStdin\":false,\"AttachStdout\":false,\"AttachStderr\":false,\"Tty\":false,\"OpenStdin\":false,\"StdinOnce\":false,\"Env\":[\"PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin\"],\"Cmd\":[\"/bin/sh\",\"-c\",\"#(nop) \",\"CMD [\\\"start_runit\\\"]\"],\"ArgsEscaped\":true,\"Image\":\"sha256:bd8fc24ca86f5a6409a752241501c72c8eaece6f1920b0f63886b18650609c37\",\"Volumes\":null,\"WorkingDir\":\"\",\"Entrypoint\":null,\"OnBuild\":null,\"Labels\":{\"maintainer\":\"Casey Davenport <casey@tigera.io>\"}},\"created\":\"2019-08-15T23:23:54.743169968Z\",\"docker_version\":\"18.06.3-ce\",\"id\":\"a7ab2a9080377e37ba0fb3c15ceff5eb41ee34da69cd01cd35107fb145176865\",\"os\":\"linux\",\"parent\":\"78fd15a29ce4ab96610c3719b5e75ccd821e043113d843e6ed2a819c0a03e4ac\",\"throwaway\":true}},{\"v1Compatibility\":{\"id\":\"78fd15a29ce4ab96610c3719b5e75ccd821e043113d843e6ed2a819c0a03e4ac\",\"parent\":\"24d118a0bf7191ed329445850e0540597efe72ffb5a943bfb14550df312fea53\",\"created\":\"2019-08-15T23:23:54.597620781Z\",\"container_config\":{\"Cmd\":[\"/bin/sh -c #(nop) COPY file:5c63a50496f23fe649ec516d7a41a9f31ffa7d9cf89ca7a9e59c497cea7c3155 in /bin \"]}}},{\"v1Compatibility\":{\"id\":\"24d118a0bf7191ed329445850e0540597efe72ffb5a943bfb14550df312fea53\",\"parent\":\"4ea20e5f1d4bf483a3eec9d0435984b970ba7c0e524e1e885c38e5938e8250ec\",\"created\":\"2019-08-15T23:23:54.475304364Z\",\"container_config\":{\"Cmd\":[\"/bin/sh -c #(nop) COPY file:3212f5b4b311b0a8d84c8d5609444fd9b076f57b999d42d6e9b6656a138201dd in /bin/calico-node \"]}}},{\"v1Compatibility\":{\"id\":\"4ea20e5f1d4bf483a3eec9d0435984b970ba7c0e524e1e885c38e5938e8250ec\",\"parent\":\"53a819a6f3b0b93fe181bb87408a97280c1ab05635df730d091407d89c4f49fc\",\"created\":\"2019-08-15T23:19:55.76052212Z\",\"container_config\":{\"Cmd\":[\"/bin/sh -c #(nop) COPY dir:b102ba85fd99301e840612b8931bd5c52d93dc719029df40201e66fa0d01ffee in / \"]}}},{\"v1Compatibility\":{\"id\":\"53a819a6f3b0b93fe181bb87408a97280c1ab05635df730d091407d89c4f49fc\",\"parent\":\"b999593873cefc4388f1e1124ab0b11c358fb6da9b6136beef076f64d016fda2\",\"created\":\"2019-08-15T23:19:55.571318958Z\",\"container_config\":{\"Cmd\":[\"/bin/sh -c #(nop) COPY multi:59961e73a1b028258b2812207cb5adde706da1d3e7f948d7ce58df348a6812de in /bin/ \"]}}},{\"v1Compatibility\":{\"id\":\"b999593873cefc4388f1e1124ab0b11c358fb6da9b6136beef076f64d016fda2\",\"parent\":\"5b7128ed040d6715a4329aca2b32b8f6f93f29e660dd7da79db7f8a6e4ccdc81\",\"created\":\"2019-08-15T23:19:55.283408356Z\",\"container_config\":{\"Cmd\":[\"|1 ARCH=amd64 /bin/sh -c update-alternatives --set ip6tables /usr/sbin/ip6tables-legacy\"]}}},{\"v1Compatibility\":{\"id\":\"5b7128ed040d6715a4329aca2b32b8f6f93f29e660dd7da79db7f8a6e4ccdc81\",\"parent\":\"92dcaaeaf62bfa4a68baf3bb973f98a073ca47ad22a496244fa4f154a23fdf5a\",\"created\":\"2019-08-15T23:19:54.787796134Z\",\"container_config\":{\"Cmd\":[\"|1 ARCH=amd64 /bin/sh -c update-alternatives --set iptables /usr/sbin/iptables-legacy\"]}}},{\"v1Compatibility\":{\"id\":\"92dcaaeaf62bfa4a68baf3bb973f98a073ca47ad22a496244fa4f154a23fdf5a\",\"parent\":\"3d026ee6cfc759eee3a208b4fd3ad236eddbb32de4f62ea9f0fe4d06340898eb\",\"created\":\"2019-08-15T23:19:54.215471993Z\",\"container_config\":{\"Cmd\":[\"|1 ARCH=amd64 /bin/sh -c apt-get update && apt-get install -y -t buster     ipset     iptables     iproute2\"]}}},{\"v1Compatibility\":{\"id\":\"3d026ee6cfc759eee3a208b4fd3ad236eddbb32de4f62ea9f0fe4d06340898eb\",\"parent\":\"208d72b270781690a1eac7114290c7833a475c9578d582ff02092fc0f3a31d7b\",\"created\":\"2019-08-15T23:19:50.127574259Z\",\"container_config\":{\"Cmd\":[\"|1 ARCH=amd64 /bin/sh -c echo 'deb     http://ftp.de.debian.org/debian/    buster main contrib non-free' > /etc/apt/sources.list.d/buster.list\"]}}},{\"v1Compatibility\":{\"id\":\"208d72b270781690a1eac7114290c7833a475c9578d582ff02092fc0f3a31d7b\",\"parent\":\"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248\",\"created\":\"2019-08-15T23:19:49.603722705Z\",\"container_config\":{\"Cmd\":[\"|1 ARCH=amd64 /bin/sh -c echo 'APT::Default-Release \\\"stable\\\";' > /etc/apt/apt.conf.d/99defaultrelease\"]}}},{\"v1Compatibility\":{\"id\":\"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248\",\"parent\":\"adf31f8cb449a3d43757c59c7873d3b08117ecb29203cde26624c76cdfbec777\",\"created\":\"2019-08-15T23:19:49.065117775Z\",\"container_config\":{\"Cmd\":[\"|1 ARCH=amd64 /bin/sh -c apt-get update && apt-get install -y     ipset     iputils-arping     iputils-ping     iputils-tracepath     net-tools     conntrack     runit     kmod     netbase     procps     ca-certificates\"]}}},{\"v1Compatibility\":{\"id\":\"adf31f8cb449a3d43757c59c7873d3b08117ecb29203cde26624c76cdfbec777\",\"parent\":\"c85f6d16a3f04aa669cf5fbe7e412c3085cc42e8040ae88fda695df17754d441\",\"created\":\"2019-08-15T23:18:55.711576832Z\",\"container_config\":{\"Cmd\":[\"/bin/sh -c #(nop)  ARG ARCH=amd64\"]},\"throwaway\":true}},{\"v1Compatibility\":{\"id\":\"c85f6d16a3f04aa669cf5fbe7e412c3085cc42e8040ae88fda695df17754d441\",\"parent\":\"1b2d6113be099a3d117e01ee2afa01455bd93feff72c0e5c1769db95eb6a794e\",\"created\":\"2019-08-15T23:18:55.615487429Z\",\"container_config\":{\"Cmd\":[\"/bin/sh -c #(nop)  LABEL maintainer=Casey Davenport <casey@tigera.io>\"]},\"throwaway\":true}},{\"v1Compatibility\":{\"id\":\"1b2d6113be099a3d117e01ee2afa01455bd93feff72c0e5c1769db95eb6a794e\",\"parent\":\"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284\",\"created\":\"2019-08-14T00:22:12.498371222Z\",\"container_config\":{\"Cmd\":[\"/bin/sh -c #(nop)  CMD [\\\"bash\\\"]\"]},\"throwaway\":true}},{\"v1Compatibility\":{\"id\":\"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284\",\"created\":\"2019-08-14T00:22:12.288915269Z\",\"container_config\":{\"Cmd\":[\"/bin/sh -c #(nop) ADD file:330bfb91168adb4a9b1296c70209ed487d4c2705042a916d575f82b61ab16e61 in / \"]}}}],\"signatures\":[{\"header\":{\"jwk\":{\"crv\":\"P-256\",\"kid\":\"WC72:LZ4V:NXPI:VWAO:LQ54:SO3X:3EBE:IX7U:25HA:RFSD:CJDT:R7AK\",\"kty\":\"EC\",\"x\":\"kpj17fAbnhIN4OrfnSrKvMEbD0odQumE3t0POFxetes\",\"y\":\"9laTfaMBHOEnlUAoLuGbmFyZPVKMHTB6GqlItQUUMao\"},\"alg\":\"ES256\"},\"signature\":\"vRIi0hhcaFG_PtaQWaFCqcfGsRMw92XgG0hZOZKls16wAWVBGHHJLZg4d7jVu746n17lrgPCn0LXtC4Jv4kafQ\",\"protected\":\"eyJmb3JtYXRMZW5ndGgiOjg5MDcsImZvcm1hdFRhaWwiOiJDbjAiLCJ0aW1lIjoiMjAxOS0wOC0xNVQyMzo1ODo1MloifQ\"}]}";
        JSONObject obj = (JSONObject) new JSONParser().parse(body);
        JSONObject result = new JSONObject();
        JSONArray history = new JSONArray();
        result.put("history", history);

        try {
            JSONArray histories = (JSONArray) obj.get("history");

            for( int i = 0; i < histories.size(); i++) {
                JSONObject o = (JSONObject) histories.get(i);
                String v1Com = (String) o.get("v1Compatibility");
                JSONObject v1Compatibility = (JSONObject) new JSONParser().parse(v1Com);
                JSONObject container_config = (JSONObject) v1Compatibility.get("container_config");

                if (i == 0) {
                    result.put("id", v1Compatibility.get("id"));
                    result.put("command", container_config.get("Cmd"));
                } else {
                    JSONObject item = new JSONObject();
                    item.put("id", v1Compatibility.get("id"));
                    item.put("command", container_config.get("Cmd"));
                    history.add(item);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

        return result;
    }

    /**
     * security scan 정보 조회
     * @param namespace
     * @param name
     * @param tagName
     * @return
     * @throws Exception
     */
    public Map<String, Object> getSecurity(String namespace, String name, String tagName) throws Exception {
        logger.info("getSecurity");
        logger.info("namespace : {}", namespace);
        logger.info("name : {}", name);
        logger.info("tagName : {}", tagName);

        String body = (String) restApiUtil.excute(MessageFormat.format("{0}/v1/sescan/repository/{1}/{2}?tag={3}", this.getBuilderUri().toString(), namespace, name, tagName), HttpMethod.GET, null, String.class);
//        String body = "{\"code\": \"SUCCESS\", \"data\": {\"status\": \"scanned\", \"data\": {\"Layer\": {\"IndexedByVersion\": 3, \"NamespaceName\": \"debian:10\", \"ParentName\": \"78fd15a29ce4ab96610c3719b5e75ccd821e043113d843e6ed2a819c0a03e4ac.34d73eea-4ca3-408f-8c3a-f3e7c8e29f93\", \"Name\": \"a7ab2a9080377e37ba0fb3c15ceff5eb41ee34da69cd01cd35107fb145176865.86f0a285-6f29-47c4-a3ae-7e2c70cad0ba\", \"Features\": [{\"Name\": \"shadow\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"1:4.5-1.1\", \"Vulnerabilities\": [{\"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2013-4235\", \"Name\": \"CVE-2013-4235\", \"Severity\": \"Negligible\"}, {\"Severity\": \"Low\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2018-7169\", \"Description\": \"An issue was discovered in shadow 4.5. newgidmap (in shadow-utils) is setuid and allows an unprivileged user to be placed in a user namespace where setgroups(2) is permitted. This allows an attacker to remove themselves from a supplementary group, which may allow access to certain filesystem paths if the administrator has used \\\"group blacklisting\\\" (e.g., chmod g-rwx) to restrict access to paths. This flaw effectively reverts a security feature in the kernel (in particular, the /proc/self/setgroups knob) to prevent this sort of privilege escalation.\", \"Name\": \"CVE-2018-7169\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 5, \"Vectors\": \"AV:N/AC:L/Au:N/C:P/I:N\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2007-5686\", \"Description\": \"initscripts in rPath Linux 1 sets insecure permissions for the /var/log/btmp file, which allows local users to obtain sensitive information regarding authentication attempts.  NOTE: because sshd detects the insecure permissions and does not log certain events, this also prevents sshd from logging failed authentication attempts by remote attackers.\", \"Name\": \"CVE-2007-5686\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 4.9, \"Vectors\": \"AV:L/AC:L/Au:N/C:C/I:N\"}}}}]}, {\"Name\": \"openssl\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\", \"Version\": \"1.1.1c-1\", \"Vulnerabilities\": [{\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2010-0928\", \"Description\": \"OpenSSL 0.9.8i on the Gaisler Research LEON3 SoC on the Xilinx Virtex-II Pro FPGA uses a Fixed Width Exponentiation (FWE) algorithm for certain signature calculations, and does not verify the signature before providing it to a caller, which makes it easier for physically proximate attackers to determine the private key via a modified supply voltage for the microprocessor, related to a \\\"fault-based attack.\\\"\", \"Name\": \"CVE-2010-0928\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 4, \"Vectors\": \"AV:L/AC:H/Au:N/C:C/I:N\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2007-6755\", \"Description\": \"The NIST SP 800-90A default statement of the Dual Elliptic Curve Deterministic Random Bit Generation (Dual_EC_DRBG) algorithm contains point Q constants with a possible relationship to certain \\\"skeleton key\\\" values, which might allow context-dependent attackers to defeat cryptographic protection mechanisms by leveraging knowledge of those values.  NOTE: this is a preliminary CVE for Dual_EC_DRBG; future research may provide additional details about point Q and associated attacks, and could potentially lead to a RECAST or REJECT of this CVE.\", \"Name\": \"CVE-2007-6755\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 5.8, \"Vectors\": \"AV:N/AC:M/Au:N/C:P/I:P\"}}}}]}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"6.38-1.2\", \"Name\": \"ipset\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"0.61-1\", \"Name\": \"startpar\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.3.3\", \"Name\": \"dh-sysuser\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"5.6\", \"Name\": \"netbase\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.0.6-9.1\", \"Name\": \"bzip2\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2.8-2\", \"Name\": \"libsemanage\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"4.20.0-2\", \"Name\": \"iproute2\", \"AddedBy\": \"92dcaaeaf62bfa4a68baf3bb973f98a073ca47ad22a496244fa4f154a23fdf5a.c9ecab6f-7c19-41ae-abc8-d3197aa57e91\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"0.9.0-2\", \"Name\": \"nftables\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2:3.3.15-2\", \"Name\": \"procps\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"5.2.4-1\", \"Name\": \"xz-utils\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"Name\": \"libgcrypt20\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"1.8.4-5\", \"Vulnerabilities\": [{\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2018-6829\", \"Description\": \"cipher/elgamal.c in Libgcrypt through 1.8.2, when used to encrypt messages directly, improperly encodes plaintexts, which allows attackers to obtain sensitive information by reading ciphertext data (i.e., it does not have semantic security in face of a ciphertext-only attack). The Decisional Diffie-Hellman (DDH) assumption does not hold for Libgcrypt's ElGamal implementation.\", \"Name\": \"CVE-2018-6829\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 5, \"Vectors\": \"AV:N/AC:L/Au:N/C:P/I:N\"}}}}, {\"Severity\": \"Medium\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2019-12904\", \"Description\": \"In Libgcrypt 1.8.4, the C implementation of AES is vulnerable to a flush-and-reload side-channel attack because physical addresses are available to other processes. (The C implementation is used on platforms where an assembly-language implementation is unavailable.)\", \"Name\": \"CVE-2019-12904\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 4.3, \"Vectors\": \"AV:N/AC:M/Au:N/C:P/I:N\"}}}}]}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1:1.2.11.dfsg-1\", \"Name\": \"zlib\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1:1.4.5-2\", \"Name\": \"conntrack-tools\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1:2.25-2\", \"Name\": \"libcap2\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"Name\": \"apt\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"1.8.2\", \"Vulnerabilities\": [{\"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2011-3374\", \"Name\": \"CVE-2011-3374\", \"Severity\": \"Negligible\"}]}, {\"Name\": \"perl\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"5.28.1-6\", \"Vulnerabilities\": [{\"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2012-3878\", \"Name\": \"CVE-2012-3878\", \"Severity\": \"Negligible\"}, {\"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2011-4116\", \"Name\": \"CVE-2011-4116\", \"Severity\": \"Negligible\"}]}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"3.4.1-1\", \"Name\": \"nettle\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2.8-1\", \"Name\": \"libsepol\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2.8.6\", \"Name\": \"dh-runit\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2.1.2-25\", \"Name\": \"runit\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"3:20180629-2\", \"Name\": \"iputils\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"Name\": \"gcc-8\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"8.3.0-6\", \"Vulnerabilities\": [{\"Severity\": \"Medium\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2018-12886\", \"Description\": \"stack_protect_prologue in cfgexpand.c and stack_protect_epilogue in function.c in GNU Compiler Collection (GCC) 4.1 through 8 (under certain circumstances) generate instruction sequences when targeting ARM targets that spill the address of the stack protector guard, which allows an attacker to bypass the protection of -fstack-protector, -fstack-protector-all, -fstack-protector-strong, and -fstack-protector-explicit against stack overflow by controlling what the stack canary is compared against.\", \"Name\": \"CVE-2018-12886\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 6.8, \"Vectors\": \"AV:N/AC:M/Au:N/C:P/I:P\"}}}}]}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2019a-1\", \"Name\": \"tzdata\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"3.118\", \"Name\": \"adduser\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.9-3\", \"Name\": \"gzip\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2.0.5-1\", \"Name\": \"libidn2\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"20190110\", \"Name\": \"ca-certificates\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"0.176-1.1\", \"Name\": \"elfutils\", \"AddedBy\": \"92dcaaeaf62bfa4a68baf3bb973f98a073ca47ad22a496244fa4f154a23fdf5a.c9ecab6f-7c19-41ae-abc8-d3197aa57e91\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"0.249\", \"Name\": \"cdebconf\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"3.3-1\", \"Name\": \"grep\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"4.8.6.1\", \"Name\": \"debianutils\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.3.8+dfsg-3\", \"Name\": \"libzstd\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"10.2019051400\", \"Name\": \"lsb\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2019.1\", \"Name\": \"debian-archive-keyring\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"Name\": \"coreutils\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"8.30-3\", \"Vulnerabilities\": [{\"Severity\": \"Low\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2016-2781\", \"Description\": \"chroot in GNU coreutils, when used with --userspec, allows local users to escape to the parent session via a crafted TIOCSTI ioctl call, which pushes characters to the terminal's input buffer.\", \"Name\": \"CVE-2016-2781\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 2.1, \"Vectors\": \"AV:L/AC:L/Au:N/C:N/I:P\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2017-18018\", \"Description\": \"In GNU Coreutils through 8.29, chown-core.c in chown and chgrp does not prevent replacement of a plain file with a symlink during use of the POSIX \\\"-R -L\\\" options, which allows local users to modify the ownership of arbitrary files by leveraging a race condition.\", \"Name\": \"CVE-2017-18018\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 1.9, \"Vectors\": \"AV:L/AC:M/Au:N/C:N/I:P\"}}}}]}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.3.1-5\", \"Name\": \"pam\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"3.5.46\", \"Name\": \"base-passwd\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.8.3-1\", \"Name\": \"lz4\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.3.3-17\", \"Name\": \"mawk\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"23.2-1\", \"Name\": \"psmisc\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"Name\": \"gnutls28\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"3.6.7-4\", \"Vulnerabilities\": [{\"Severity\": \"Medium\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2011-3389\", \"Description\": \"The SSL protocol, as used in certain configurations in Microsoft Windows and Microsoft Internet Explorer, Mozilla Firefox, Google Chrome, Opera, and other products, encrypts data by using CBC mode with chained initialization vectors, which allows man-in-the-middle attackers to obtain plaintext HTTP headers via a blockwise chosen-boundary attack (BCBA) on an HTTPS session, in conjunction with JavaScript code that uses (1) the HTML5 WebSocket API, (2) the Java URLConnection API, or (3) the Silverlight WebClient API, aka a \\\"BEAST\\\" attack.\", \"Name\": \"CVE-2011-3389\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 4.3, \"Vectors\": \"AV:N/AC:M/Au:N/C:P/I:N\"}}}}]}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"10.3\", \"Name\": \"base-files\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2.2.12-1\", \"Name\": \"gnupg2\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"Name\": \"tar\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"1.30+dfsg-6\", \"Vulnerabilities\": [{\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2005-2541\", \"Description\": \"Tar 1.15.1 does not properly warn the user when extracting setuid or setgid files, which may allow local users or remote attackers to gain privileges.\", \"Name\": \"CVE-2005-2541\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 10, \"Vectors\": \"AV:N/AC:L/Au:N/C:C/I:C\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2019-9923\", \"Description\": \"pax_decode_header in sparse.c in GNU Tar before 1.32 had a NULL pointer dereference when parsing certain archives that have malformed extended headers.\", \"Name\": \"CVE-2019-9923\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 5, \"Vectors\": \"AV:N/AC:L/Au:N/C:N/I:N\"}}}}]}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"0.7.9-2\", \"Name\": \"libcap-ng\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2.12-1\", \"Name\": \"jansson\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.0.1-3\", \"Name\": \"libnfnetlink\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1:2.5.1-2\", \"Name\": \"linux-atm\", \"AddedBy\": \"92dcaaeaf62bfa4a68baf3bb973f98a073ca47ad22a496244fa4f154a23fdf5a.c9ecab6f-7c19-41ae-abc8-d3197aa57e91\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2.2.53-4\", \"Name\": \"acl\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"3.21\", \"Name\": \"hostname\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.1.2-2\", \"Name\": \"libnftnl\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2:6.1.2+dfsg-4\", \"Name\": \"gmp\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2.8-1\", \"Name\": \"libselinux\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.44.5-1\", \"Name\": \"e2fsprogs\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"3.2.1-9\", \"Name\": \"libffi\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"0.9.10-1\", \"Name\": \"libunistring\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"Name\": \"net-tools\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\", \"Version\": \"1.60+git20180626.aebd88e-1\", \"Vulnerabilities\": [{\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2002-1976\", \"Description\": \"ifconfig, when used on the Linux kernel 2.2 and later, does not report when the network interface is in promiscuous mode if it was put in promiscuous mode using PACKET_MR_PROMISC, which could allow attackers to sniff the network without detection, as demonstrated using libpcap.\", \"Name\": \"CVE-2002-1976\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 2.1, \"Vectors\": \"AV:L/AC:L/Au:N/C:P/I:N\"}}}}]}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"4.6.0+git+20190209-2\", \"Name\": \"findutils\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.35-1\", \"Name\": \"libgpg-error\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"0.23.15-2\", \"Name\": \"p11-kit\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2.33.1-0.1\", \"Name\": \"util-linux\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"4.7-1\", \"Name\": \"sed\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"Name\": \"libtasn1-6\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"4.13-3\", \"Vulnerabilities\": [{\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2018-1000654\", \"Description\": \"GNU Libtasn1-4.13 libtasn1-4.13 version libtasn1-4.13, libtasn1-4.12 contains a DoS, specifically CPU usage will reach 100% when running asn1Paser against the POC due to an issue in _asn1_expand_object_id(p_tree), after a long time, the program will be killed. This attack appears to be exploitable via parsing a crafted file.\", \"Name\": \"CVE-2018-1000654\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 7.1, \"Vectors\": \"AV:N/AC:M/Au:N/C:N/I:N\"}}}}]}, {\"Name\": \"iptables\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\", \"Version\": \"1.8.2-4\", \"Vulnerabilities\": [{\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2012-2663\", \"Description\": \"extensions/libxt_tcp.c in iptables through 1.4.21 does not match TCP SYN+FIN packets in --syn rules, which might allow remote attackers to bypass intended firewall restrictions via crafted packets.  NOTE: the CVE-2012-6638 fix makes this issue less relevant.\", \"Name\": \"CVE-2012-2663\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 7.5, \"Vectors\": \"AV:N/AC:L/Au:N/C:P/I:P\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2019-11360\", \"Description\": \"A buffer overflow in iptables-restore in netfilter iptables 1.8.2 allows an attacker to (at least) crash the program or potentially gain code execution via a specially crafted iptables-save file. This is related to add_param_to_argv in xshared.c.\", \"Name\": \"CVE-2019-11360\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 4.3, \"Vectors\": \"AV:N/AC:M/Au:N/C:N/I:N\"}}}}]}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1:2.8.4-3\", \"Name\": \"audit\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1:3.7-3\", \"Name\": \"diffutils\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"5.3.28+dfsg1-0.5\", \"Name\": \"db5.3\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.56+nmu1\", \"Name\": \"init-system-helpers\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.19.7\", \"Name\": \"dpkg\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"5.0-4\", \"Name\": \"bash\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"2.93-8\", \"Name\": \"sysvinit\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"6.1+20181013-2\", \"Name\": \"ncurses\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.20.7-5\", \"Name\": \"gpm\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"Name\": \"glibc\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"2.28-10\", \"Vulnerabilities\": [{\"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2018-8086\", \"Name\": \"CVE-2018-8086\", \"Severity\": \"Unknown\"}, {\"Severity\": \"High\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2017-8804\", \"Description\": \"The xdr_bytes and xdr_string functions in the GNU C Library (aka glibc or libc6) 2.25 mishandle failures of buffer deserialization, which allows remote attackers to cause a denial of service (virtual memory allocation, or memory consumption if an overcommit setting is not used) via a crafted UDP packet to port 111, a related issue to CVE-2017-8779.\", \"Name\": \"CVE-2017-8804\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 7.8, \"Vectors\": \"AV:N/AC:L/Au:N/C:N/I:N\"}}}}, {\"Severity\": \"Low\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2016-10228\", \"Description\": \"The iconv program in the GNU C Library (aka glibc or libc6) 2.25 and earlier, when invoked with the -c option, enters an infinite loop when processing invalid multi-byte input sequences, leading to a denial of service.\", \"Name\": \"CVE-2016-10228\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 4.3, \"Vectors\": \"AV:N/AC:M/Au:N/C:N/I:N\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2019-1010024\", \"Description\": \"GNU Libc current is affected by: Mitigation bypass. The impact is: Attacker may bypass ASLR using cache of thread stack and heap. The component is: glibc.\", \"Name\": \"CVE-2019-1010024\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 5, \"Vectors\": \"AV:N/AC:L/Au:N/C:P/I:N\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2010-4052\", \"Description\": \"Stack consumption vulnerability in the regcomp implementation in the GNU C Library (aka glibc or libc6) through 2.11.3, and 2.12.x through 2.12.2, allows context-dependent attackers to cause a denial of service (resource exhaustion) via a regular expression containing adjacent repetition operators, as demonstrated by a {10,}{10,}{10,}{10,} sequence in the proftpd.gnu.c exploit for ProFTPD.\", \"Name\": \"CVE-2010-4052\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 5, \"Vectors\": \"AV:N/AC:L/Au:N/C:N/I:N\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2010-4756\", \"Description\": \"The glob implementation in the GNU C Library (aka glibc or libc6) allows remote authenticated users to cause a denial of service (CPU and memory consumption) via crafted glob expressions that do not match any pathnames, as demonstrated by glob expressions in STAT commands to an FTP daemon, a different vulnerability than CVE-2010-2632.\", \"Name\": \"CVE-2010-4756\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 4, \"Vectors\": \"AV:N/AC:L/Au:S/C:N/I:N\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2010-4051\", \"Description\": \"The regcomp implementation in the GNU C Library (aka glibc or libc6) through 2.11.3, and 2.12.x through 2.12.2, allows context-dependent attackers to cause a denial of service (application crash) via a regular expression containing adjacent bounded repetitions that bypass the intended RE_DUP_MAX limitation, as demonstrated by a {10,}{10,}{10,}{10,}{10,} sequence in the proftpd.gnu.c exploit for ProFTPD, related to a \\\"RE_DUP_MAX overflow.\\\"\", \"Name\": \"CVE-2010-4051\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 5, \"Vectors\": \"AV:N/AC:L/Au:N/C:N/I:N\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2018-20796\", \"Description\": \"In the GNU C Library (aka glibc or libc6) through 2.29, check_dst_limits_calc_pos_1 in posix/regexec.c has Uncontrolled Recursion, as demonstrated by '(\\\\227|)(\\\\\\\\1\\\\\\\\1|t1|\\\\\\\\\\\\2537)+' in grep.\", \"Name\": \"CVE-2018-20796\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 5, \"Vectors\": \"AV:N/AC:L/Au:N/C:N/I:N\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2019-1010022\", \"Description\": \"GNU Libc current is affected by: Mitigation bypass. The impact is: Attacker may bypass stack guard protection. The component is: nptl. The attack vector is: Exploit stack buffer overflow vulnerability and use this bypass vulnerability to bypass stack guard.\", \"Name\": \"CVE-2019-1010022\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 7.5, \"Vectors\": \"AV:N/AC:L/Au:N/C:P/I:P\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2019-1010023\", \"Description\": \"GNU Libc current is affected by: Re-mapping current loaded libray with malicious ELF file. The impact is: In worst case attacker may evaluate privileges. The component is: libld. The attack vector is: Attacker sends 2 ELF files to victim and asks to run ldd on it. ldd execute code.\", \"Name\": \"CVE-2019-1010023\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 6.8, \"Vectors\": \"AV:N/AC:M/Au:N/C:P/I:P\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2019-1010025\", \"Description\": \"** DISPUTED ** GNU Libc current is affected by: Mitigation bypass. The impact is: Attacker may guess the heap addresses of pthread_created thread. The component is: glibc. NOTE: the vendor's position is \\\"ASLR bypass itself is not a vulnerability.\\\"\", \"Name\": \"CVE-2019-1010025\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 5, \"Vectors\": \"AV:N/AC:L/Au:N/C:P/I:N\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2019-9192\", \"Description\": \"** DISPUTED ** In the GNU C Library (aka glibc or libc6) through 2.29, check_dst_limits_calc_pos_1 in posix/regexec.c has Uncontrolled Recursion, as demonstrated by '(|)(\\\\\\\\1\\\\\\\\1)*' in grep, a different issue than CVE-2018-20796. NOTE: the software maintainer disputes that this is a vulnerability because the behavior occurs only with a crafted pattern.\", \"Name\": \"CVE-2019-9192\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 5, \"Vectors\": \"AV:N/AC:L/Au:N/C:N/I:N\"}}}}]}, {\"Name\": \"pcre3\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"2:8.39-12\", \"Vulnerabilities\": [{\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2017-16231\", \"Description\": \"** DISPUTED ** In PCRE 8.41, after compiling, a pcretest load test PoC produces a crash overflow in the function match() in pcre_exec.c because of a self-recursive call. NOTE: third parties dispute the relevance of this report, noting that there are options that can be used to limit the amount of stack that is used.\", \"Name\": \"CVE-2017-16231\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 2.1, \"Vectors\": \"AV:L/AC:L/Au:N/C:N/I:N\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2017-11164\", \"Description\": \"In PCRE 8.41, the OP_KETRMAX feature in the match function in pcre_exec.c allows stack exhaustion (uncontrolled recursion) when processing a crafted regular expression.\", \"Name\": \"CVE-2017-11164\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 7.8, \"Vectors\": \"AV:N/AC:L/Au:N/C:N/I:N\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2017-7246\", \"Description\": \"Stack-based buffer overflow in the pcre32_copy_substring function in pcre_get.c in libpcre1 in PCRE 8.40 allows remote attackers to cause a denial of service (WRITE of size 268) or possibly have unspecified other impact via a crafted file.\", \"Name\": \"CVE-2017-7246\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 6.8, \"Vectors\": \"AV:N/AC:M/Au:N/C:P/I:P\"}}}}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2017-7245\", \"Description\": \"Stack-based buffer overflow in the pcre32_copy_substring function in pcre_get.c in libpcre1 in PCRE 8.40 allows remote attackers to cause a denial of service (WRITE of size 4) or possibly have unspecified other impact via a crafted file.\", \"Name\": \"CVE-2017-7245\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 6.8, \"Vectors\": \"AV:N/AC:M/Au:N/C:P/I:P\"}}}}]}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"0.5.10.2-5\", \"Name\": \"dash\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"26-1\", \"Name\": \"kmod\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.0.7-1\", \"Name\": \"libnetfilter-conntrack\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"7.0-5\", \"Name\": \"readline\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"Name\": \"systemd\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"241-5\", \"Vulnerabilities\": [{\"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2019-9619\", \"Name\": \"CVE-2019-9619\", \"Severity\": \"Unknown\"}, {\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2013-4392\", \"Description\": \"systemd, when updating file permissions, allows local users to change the permissions and SELinux security contexts for arbitrary files via a symlink attack on unspecified files.\", \"Name\": \"CVE-2013-4392\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 3.3, \"Vectors\": \"AV:L/AC:M/Au:N/C:P/I:P\"}}}}, {\"Severity\": \"Medium\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2018-20839\", \"Description\": \"systemd 242 changes the VT1 mode upon a logout, which allows attackers to read cleartext passwords in certain circumstances, such as watching a shutdown, or using Ctrl-Alt-F1 and Ctrl-Alt-F2. This occurs because the KDGKBMODE (aka current keyboard mode) check is mishandled.\", \"Name\": \"CVE-2018-20839\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 5, \"Vectors\": \"AV:N/AC:L/Au:N/C:P/I:N\"}}}}, {\"Severity\": \"Medium\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2019-3843\", \"Description\": \"It was discovered that a systemd service that uses DynamicUser property can create a SUID/SGID binary that would be allowed to run as the transient service UID/GID even after the service is terminated. A local attacker may use this flaw to access resources that will be owned by a potentially different service in the future, when the UID/GID will be recycled.\", \"Name\": \"CVE-2019-3843\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 4.6, \"Vectors\": \"AV:L/AC:L/Au:N/C:P/I:P\"}}}}, {\"Severity\": \"Medium\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2019-3844\", \"Description\": \"It was discovered that a systemd service that uses DynamicUser property can get new privileges through the execution of SUID binaries, which would allow to create binaries owned by the service transient group with the setgid bit set. A local attacker may use this flaw to access resources that will be owned by a potentially different service in the future, when the GID will be recycled.\", \"Name\": \"CVE-2019-3844\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 4.6, \"Vectors\": \"AV:L/AC:L/Au:N/C:P/I:P\"}}}}]}, {\"Name\": \"libseccomp\", \"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\", \"Version\": \"2.3.3-4\", \"Vulnerabilities\": [{\"Severity\": \"Negligible\", \"NamespaceName\": \"debian:10\", \"Link\": \"https://security-tracker.debian.org/tracker/CVE-2019-9893\", \"Description\": \"libseccomp before 2.4.0 did not correctly generate 64-bit syscall argument comparisons using the arithmetic operators (LT, GT, LE, GE), which might able to lead to bypassing seccomp filters and potential privilege escalations.\", \"Name\": \"CVE-2019-9893\", \"Metadata\": {\"NVD\": {\"CVSSv2\": {\"Score\": 7.5, \"Vectors\": \"AV:N/AC:L/Au:N/C:P/I:P\"}}}}]}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.18.0-2\", \"Name\": \"insserv\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.0.4-2\", \"Name\": \"libmnl\", \"AddedBy\": \"154647f8b2d232b385564bbb271841dc21d26235b7f6b357219ff336ad76b248.f2a12f75-c3fe-4c6e-b7e2-079d21471ab4\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1.5.71\", \"Name\": \"debconf\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}, {\"VersionFormat\": \"dpkg\", \"NamespaceName\": \"debian:10\", \"Version\": \"1:2.4.48-4\", \"Name\": \"attr\", \"AddedBy\": \"3a30726d764ee173eb7d4f842883113064d1928d393f38b46f860df689a20284.49937db9-8edc-4f9d-aacd-65bcb24a2472\"}]}}}}";
        JSONObject result = new JSONObject();

        try {
            result = (JSONObject) new JSONParser().parse(body);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

        return result;
    }

    /**
     * build 요청
     * @param build
     * @return
     */
    public Map<String, Object> createBuild(Build build, boolean noCache, Build sourceBuild) {
        logger.info("createBuild");
        logger.info("namespace : {}", build.getImage().getNamespace());
        logger.info("name : {}", build.getImage().getName());
        logger.info("build id : {}", build.getId());
        logger.info("sourceBuild id : {}", sourceBuild != null ? sourceBuild.getId() : null);

        String url;
        Map<String, Object> params = new HashedMap();
        params.put("build", build.getId());
        params.put("name", MessageFormat.format("{0}/{1}", build.getImage().getNamespace(), build.getImage().getName()));
        params.put("push", true);
        params.put("useCache", !noCache);
        if (!StringUtils.isEmpty(build.getDockerfile())) {
            params.put("contents", build.getDockerfile());
            url = MessageFormat.format("{0}/v1/docker/build/file", this.getBuilderUri().toString());
        } else if (!StringUtils.isEmpty(build.getGitPath())) {
            params.put("gitRepo", build.getGitPath());
            params.put("userId", build.getGitUsername());
            params.put("userPw", build.getGitPassword());
            url = MessageFormat.format("{0}/v1/docker/build/git", this.getBuilderUri().toString());
        } else {
            params.put("path", build.getMinioPath());
            params.put("userId", SecurityUtil.getUser());
            if (sourceBuild != null) {
                // copy as (minio)
                params.put("srcPath", sourceBuild.getMinioPath());
                params.put("srcUserId", sourceBuild.getCreatedBy().getUsername());

                logger.info("sourceBuild path : {}", sourceBuild.getMinioPath());
                logger.info("sourceBuild userId : {}", sourceBuild.getCreatedBy().getUsername());
                url = MessageFormat.format("{0}/v1/docker/build/minio-copy-as", this.getBuilderUri().toString());
            } else {
                url = MessageFormat.format("{0}/v1/docker/build/minio", this.getBuilderUri().toString());
            }
        }

        Map<String, Object> result = new HashedMap();
        try {
            result = (LinkedHashMap) restApiUtil.excute(url, HttpMethod.POST, params, Object.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

        if (!"SUCCESS".equals(result.get("code"))) {
            throw new BadRequestException("error");
        }

        return (Map<String, Object>) result.get("data");
    }

    /**
     * tag 생성
     * @param tag
     * @param oldTagName
     * @return
     */
    public Map<String, Object> createTag(Tag tag, String oldTagName) {
        logger.info("createBuild");
        logger.info("namespace : {}", tag.getImage().getNamespace());
        logger.info("name : {}", tag.getImage().getName());
        logger.info("tag name : {}", tag.getName());
        logger.info("old tag name : {}", oldTagName);

        Map<String, Object> params = new HashedMap();
        params.put("build", tag.getBuildId());
        params.put("name", MessageFormat.format("{0}/{1}", tag.getImage().getNamespace(), tag.getImage().getName()));
        params.put("newTag", tag.getName());
        params.put("oldTag", oldTagName);

        Map<String, Object> result = new HashedMap();
        try {
            result = (LinkedHashMap) restApiUtil.excute(
                    MessageFormat.format("{0}/v1/docker/tag", this.getBuilderUri().toString()),
                    HttpMethod.PATCH, params, Object.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

        if (!"SUCCESS".equals(result.get("code"))) {
            throw new BadRequestException("error");
        }

        return (Map<String, Object>) result.get("data");
    }

    /**
     * tag 삭제
     * @param tag
     * @return
     */
    public Map<String, Object> deleteTag(Tag tag) {
        logger.info("deleteTag");
        logger.info("namespace : {}", tag.getImage().getNamespace());
        logger.info("name : {}", tag.getImage().getName());
        logger.info("tag name : {}", tag.getName());

        Map<String, Object> result = new HashedMap();
        try {
            result = (LinkedHashMap) restApiUtil.excute(
                    MessageFormat.format("{0}/v1/registry/repositories/{1}/{2}?tag={3}", this.getBuilderUri().toString(), tag.getImage().getNamespace(), tag.getImage().getName(), tag.getName()),
                    HttpMethod.DELETE, null, Object.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

        if (!"SUCCESS".equals(result.get("code"))) {
            throw new BadRequestException("error");
        }

        return (Map<String, Object>) result.get("data");
    }

    /**
     * delete image
     * @param image
     * @return
     */
    public void deleteImage(Image image) {
        logger.info("deleteTag");
        logger.info("namespace : {}", image.getNamespace());
        logger.info("name : {}", image.getName());

        Map<String, Object> result = new HashedMap();
        try {
            result = (LinkedHashMap) restApiUtil.excute(
                    MessageFormat.format("{0}/v1/registry/repositories/{1}/{2}", this.getBuilderUri().toString(), image.getNamespace(), image.getName()),
                    HttpMethod.DELETE, null, Object.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

//        if (!"SUCCESS".equals(result.get("code"))) {
//            throw new BadRequestException("error");
//        }
    }

    /**
     * update minio
     * @return
     */
    public Map<String, Object> updateMinio(Boolean enable, String password) {
        logger.info("updateMinio enable : {}", enable);

        Map<String, Object> result = new HashedMap();
        try {
            Map<String, Object> params = new HashedMap();
            params.put("userId", SecurityUtil.getUser());

            if (enable) {
                byte[] targetBytes;
                byte[] encodedBytes;
                Base64.Encoder encoder = Base64.getEncoder();
                targetBytes = password.getBytes();
                encodedBytes = encoder.encode(targetBytes);
                params.put("userPw", new String(encodedBytes));

                result = (LinkedHashMap) restApiUtil.excute(
                        MessageFormat.format("{0}/v1/minio", this.getBuilderUri().toString()),
                        HttpMethod.POST, params, Object.class);
            } else {
                result = (LinkedHashMap) restApiUtil.excute(
                        MessageFormat.format("{0}/v1/minio", this.getBuilderUri().toString()),
                        HttpMethod.DELETE, params, Object.class);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

        if (!"SUCCESS".equals(result.get("code"))) {
            throw new BadRequestException("error");
        }

        return result;
    }

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Harbor
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/



    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /**
     * sync Image
     * @param image
     * @return
     */
    private Image syncImage(Map<String, Object> image) {
        String name = (String) image.get("name");
        String[] names = name.split("/");
        if (names.length != 2) {
            // org/image or user/image 형태의 이름이 아니면 싱크하지 않음
            return null;
        }

        String namespace = names[0];
        String imageName = names[1];

        logger.info("syncImage");
        logger.info("namespace : {}", namespace);
        logger.info("name : {}", imageName);

        Organization org = organizationService.getOrg(namespace);
        User user = userService.getUser(namespace);

        if (org == null && user == null) {
            // org / user 둘중 하나라도 정보가 있어야 싱크
            return null;
        }

        Image img = imageService.getImage(namespace, imageName);
        if (img == null) {

            img = new Image();
            img.setNamespace(namespace);
            img.setName(imageName);
            img.setDelYn(false);

            if (org != null) {
                img.setIsOrganization(true);
            } else {
                img.setIsOrganization(false);
            }

            img.setIsPublic(true);
            imageRepo.save(img);

            if (org != null) {

                List<Role> members = new ArrayList<>();
                List<UserOrganization> userOrgs = userOrgRepo.getUserOrgs(org.getId());
                Image i = img;
                userOrgs.stream().forEach(value -> {
                    Role role = new Role();
                    role.setUser(value.getUser());
                    role.setImage(i);
                    role.setName(Const.Role.ADMIN);
                    role.setIsStarred(false);
                    members.add(role);
                });

                roleRepo.saveAll(members);
            } else {
                Role role = new Role();
                role.setImage(img);
                role.setUser(user);
                role.setName(Const.Role.ADMIN);
                role.setIsStarred(false);
                roleRepo.save(role);
            }
        }

        // sync tag
        syncTags(img, (List<Map<String, Object>>) image.get("tags"));

        return img;
    }

    /**
     * sync tag
     * @param image
     * @param tags
     */
    private void syncTags(Image image, List<Map<String, Object>> tags) {
        List<Tag> syncTags = new ArrayList<>();
        tags.stream().forEach(value -> {
            String name = (String) value.get("name");
            String digest = (String) value.get("digest");

            Tag tag = tagRepo.getTagByTagName(image.getId(), name);
            if (tag == null) {
                logger.info("syncTags : create tag");
                logger.info("tag name : {}", name);

                tag = new Tag();
                tag.setImage(image);
                tag.setName(name);
                tag.setManifestDigest(digest);
                tag.setDockerImageId(UUID.randomUUID().toString());
                tag.setStartTime(LocalDateTime.now());
                tagRepo.save(tag);
            }

            syncTags.add(tag);
        });

        // 실제 존재하지 않는 Tag 삭제
        List<Tag> allTags = tagRepo.getTags(image.getId());
        allTags.stream().forEach(value -> {
            List<Tag> tag = syncTags.stream()
                    .filter(v -> v.getName().equals(value.getName()))
                    .collect(Collectors.toList());
            if (tag == null || tag.size() == 0) {
                logger.info("syncTags : delete tag");
                logger.info("tag name : {}", value.getName());

                LocalDateTime now = LocalDateTime.now();
                value.setExpiration(now);
                value.setEndTime(now);
                tagRepo.save(value);
            }
        });
    }

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Inner Class
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
}
