package network;

public class ServerAPIConstants {
    public static final String SERVER_DOMAIN = "https://api.coverstar.tv";

    /**
     * 서버에서 리턴되는 에러코드
     *
     * @author sdk
     */
    public class SERVER_RETURN_CODE {
        public static final String SUCCESS = "0";  // 성공
        public static final String FAIL = "1";  // 실패
        public static final String SELECT_COMPANY = "3";  // 회사 선택 필요

        // 앱 내부 에러코드
        public static final String API_CALLBACK_FAIL = "4645";
        public static final String API_RESULT_ERROR = "4646";
        public static final String UNKNOWN_ERROR = "9999";
    }
}
