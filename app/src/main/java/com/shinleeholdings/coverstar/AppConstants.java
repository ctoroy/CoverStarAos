package com.shinleeholdings.coverstar;

/**
 * 상수값 정리
 *
 * @author sdk
 */
public class AppConstants {

    public static final String COMMON_TIME_FORMAT = "yyyyMMddHHmmss";
    public static final String CHATTING_TIME_FORMAT = "yyyyMMddHHmmssSSS";
//    2022 06 10 06 15 56 671
    public static final String APP_NAME = "CoverStar";
    public static final String PAY_URL_REAL = "https://coverstar.tv/livepay/index.php";
    public static final String PAY_URL_DEV = "https://coverstar.tv/pay/index.php";

    // TODO 약관들 URL 설정 필요
    public static final String USE_RULE_URL = "https://coverstar.tv/pay/index.php";
    public static final String PRIVATE_RULE_URL = "https://coverstar.tv/pay/index.php";

    public static final class EXTRA {
        public static final String USER_DATA = "USER_DATA";
        public static final String PHONE_CERT_MODE = "PHONE_CERT_MODE";
        public static final String MODE = "MODE";
        public static final String MESSAGE = "MESSAGE";
        public static final String IS_JOIN = "IS_JOIN";
        public static final String CONTEST_INFO_ID = "CONTEST_INFO_ID";
        public static final String CONTEST_DATA = "CONTEST_DATA";
        public static final String CONTEST_URL = "CONTEST_URL";
        public static final String WEBVIEW_URL = "WEBVIEW_URL";
        public static final String HINT_TEXT = "HINT_TEXT";
        public static final String CHAT_ID= "CHAT_ID";

        public static final String PUSH_KEY = "PUSH_KEY";
        public static final String PUSH_TYPE = "PUSH_TYPE";
    }

    public static final class REQUEST_CODE {
        public static final int RULE_DETAIL_USE_RULE = 12312;
        public static final int RULE_DETAIL_PRIVATE_RULE = 12313;
        public static final int INPUT_COMMENT = 12314;
        public static final int INPUT_REPLY = 12315;
        public static final int PAYMENT = 12316;
    }
}
