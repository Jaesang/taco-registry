package com.registry.constant;

/**
 * 공통 상수
 *
 * @author LEE
 */
public class CommonConstant {

	public class CommonKey
    {
        public static final String RESULT_CODE    = "code";    // 반환 코드
        public static final String RESULT_MESSAGE = "message"; // 반환 메시지
        public static final String RESULT_DATA    = "data";    // 반환 데이터
    }

	public class CommonCode
    {
        //////////////////////////////////////////////////
        // 공통
        //////////////////////////////////////////////////
        public static final String SUCCESS 					         = "0000";		// 성공
        public static final String FAIL 					         = "0001";		// 실패
        public static final String FAIL_INVENTORY 					         = "0099";		// 실패

        //////////////////////////////////////////////////
        // Execution
        //////////////////////////////////////////////////
        public static final String STATUS_RUNNING   = "RUNNING";		// 실행중
        public static final String STATUS_SUCCESS   = "SUCCESS";		// 성공
        public static final String STATUS_FAIL      = "FAIL";		    // 실패

        public static final String TYPE_CLUSTER     = "cluster";		    // cluster
        public static final String TYPE_UPGRADE     = "upgrade-cluster";	// upgrade-cluster
        public static final String TYPE_SCALE       = "scale";		        // scale
        public static final String TYPE_RESET       = "reset";		        // reset
        public static final String TYPE_MONITORING  = "monitoring";		    // monitoring
        public static final String TYPE_RESET_MONITORING     = "reset-monitoring";		// reset-monitoring

        //////////////////////////////////////////////////
        // Monitoring
        //////////////////////////////////////////////////
        public static final String MONITORING_TYPE_KIBANA   = "kibana";		// kibana
        public static final String MONITORING_TYPE_GRAFANA  = "grafana";	// grafana
    }

	public class CommonMessage
    {
        //////////////////////////////////////////////////
        // 공통
        //////////////////////////////////////////////////
        public static final String SUCCESS 			                 = "SUCCESS";							    // 성공
        public static final String FAIL 			                 = "서버 오류로 요청하신 페이지를 표시할 수 없습니다. 잠시 후 다시 시도해주세요.";	// 실패
        public static final String RUNNING_CLUSTER                   = "이미 실행 중인 클러스터 입니다. 완료된 이후에 실행 가능 합니다.";
        public static final String EXIST_UNKNOWN_HOSTS               = "Unknown host exist";
        public static final String NOT_EXIST_EXECUTION_FILE          = "해당 실행 파일이 존재하지 않습니다.";
        public static final String EXIST_CLUSTER                     = "같은 이름의 클러스터가 존재 합니다.";
        public static final String EXIST_INVENTORY                   = "같은 이름의 인벤토리가 존재 합니다.";
        public static final String DO_NOT_COMPLETE_CLUSTER_INIT      = "클러스터 초기화가 완료되지 않았습니다.";
    }

}
