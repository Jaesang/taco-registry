package com.registry.constant;

/**
 * 공통 상수
 */
public class Const {

    public static class Role {
        public static final String ADMIN = "ADMIN";
        public static final String WRITE = "WRITE";
        public static final String READ = "READ";
    }

    public static class Build {
        public static class PHASE {
            public static final String COMPLETE = "complete";

            public static final String ERROR = "error";
            public static final String CANCELLED = "cancelled";
            public static final String WAITING = "waiting";
            public static final String PULLING = "pulling";
            public static final String PUSHING = "pushing";
            public static final String BUILDING = "building";
        }

        public static class LOG_TYPE {
            public static final String PHASE = "phase";
            public static final String COMMAND = "command";
            public static final String ERROR = "error";
        }
    }

    public static class UsageLog {

        public static final String CREATE_IMAGE = "create_image";

        public static final String DELETE_IMAGE = "delete_image";

        public static final String CHANGE_IMAGE_VISIBILITY = "change_image_visibility";

        public static final String SET_IMAGE_DESCRIPTION = "set_image_description";

        public static final String CHANGE_IMAGE_PERMISSION = "change_image_permission";

        public static final String DELETE_IMAGE_PERMISSION = "delete_image_permission";

        public static final String PUSH_IMAGE = "push_image";

        public static final String PULL_IMAGE = "pull_image";

        public static final String BUILD_DOCKERFILE = "build_dockerfile";

        public static final String CREATE_TAG = "create_tag";

        public static final String DELETE_TAG = "delete_tag";

        public static final String CHANGE_TAG_EXPIRATION = "change_tag_expiration";

        public static final String REVERT_TAG = "revert_tag";

        public static final String ORG_ADD_MEMBER = "org_add_member";

        public static final String ORG_REMOVE_MEMBER = "org_remove_member";

        public static final String ACCOUNT_CHANGE_PASSWORD = "account_change_password";
    }

}