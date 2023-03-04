package clone.gozik.entity;


public enum CompanyTypeEnum {

    BIG(Authority.BIG),
    MIDDLE(Authority.MIDDLE),
    PUBLIC(Authority.PUBLIC),
    ECT(Authority.ECT);

    private final String authority;

    CompanyTypeEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority(){
        return authority;
    }

    public static class Authority{
        public static final String BIG = "COMP_BIG";
        public static final String MIDDLE = "COMP_MIDDLE";
        public static final String PUBLIC = "COMP_PUBILC";
        public static final String ECT = "COMP_ECT";
    }

}
