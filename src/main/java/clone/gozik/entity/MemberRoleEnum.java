package clone.gozik.entity;

public enum MemberRoleEnum {

    MEMBER(Authority.MEMBER),
    COMPANY(Authority.COMPANY);

    private final String authority;

    MemberRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority(){
        return authority;
    }

    public static class Authority{
        public static final String MEMBER = "ROLE_MEMBER";
        public static final String COMPANY = "ROLE_COMPANY";
    }
}
