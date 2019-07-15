package life.majiang.community.community.Exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{
    //实际上是枚举的列表
    QUESTION_NOT_FOUND("你找的问题不在了，要不换个试试");

    private String message;


    CustomizeErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
