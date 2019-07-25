package com.registry.controller;

import com.registry.constant.Path;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by boozer on 2019. 7. 15
 */
@RestController
public class DownloadController {

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    protected static final Logger logger = LoggerFactory.getLogger(DownloadController.class);

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
     * Kube-config File Download
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(Path.FILE_DOWNLOAD_KUBE_CONFIG)
    @ApiOperation(
            value = "kube-config file download",
            notes = "kube-config file download"
    )
    public void downloadKubeConfig(
            HttpServletResponse response,
            HttpServletRequest request,
            @RequestHeader(name = "Authorization") String authorization,
            @ApiParam(name = "clusterName",
                    defaultValue = "1",
                    required = true)
            @PathVariable("clusterName") String clusterName,
            @ApiParam(name = "inventoryName",
                    defaultValue = "1",
                    required = true)
            @PathVariable("inventoryName") String inventoryName
    ) throws Exception{


//        InventoryVO inventory = inventoryService.getInventory(clusterName, inventoryName);
//        nativeCommandService.createKubeConfig(clusterName, inventory);
//        String inventoryConfigFile = MessageFormat.format(nativeCommandPath.getInventoryConfigFile(), clusterName, inventoryName);
//
//        BufferedInputStream ins = null;
//        ServletOutputStream outs = null;
//        try {
//            File file = new File(inventoryConfigFile);
//            String downloadFilename = file.getName();
//            if (file.exists()) {
//                response.setContentType("application/octet-stream");
//
//                if (request.getHeader("User-Agent").indexOf("MSIE 5.5") != -1) {
//                    response.setHeader("Content-Type", "doesn/matter;");
//                    response.setHeader("Content-Disposition", "filename=" + downloadFilename);
//                } else {
//                    response.setHeader("Content-Type", "application/octet-stream;");
//                    response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);
//                }
//
//                response.setHeader("Content-Transfer-Encoding", "binary;");
//
//                response.setContentLength((int) file.length());
//
//                ins = new BufferedInputStream(new FileInputStream(file));
//                outs = response.getOutputStream();
//
//                int readInt = 0;
//                while ((readInt = ins.read()) != -1){
//                    outs.write(readInt);
//                }
//                outs.flush();
//
//                outs.close();
//                ins.close();
//
//            } else {
//                logger.error("Requested {} file not found!!", file.getName());
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        } finally {
//            if (ins != null)
//                try {
//                    ins.close();
//                } catch (Exception e) {
//                    logger.error(e.getMessage());
//                }
//            if (outs != null) {
//                try {
//                    outs.close();
//                } catch (Exception e) {
//                    logger.error(e.getMessage());
//                }
//            }
//        }

    }

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Inner Class
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
}
