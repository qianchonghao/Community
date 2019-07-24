package life.majiang.community.community.Exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{
    //实际上是枚举的列表
    QUESTION_NOT_FOUND(2001,"你找的问题不在了，要不换个试试"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中问题进行评论"),
    NO_LOGIN(2003,"当前操作需要登录，请登录后重试"),
    SYSTEM_ERROR(2004,"服务器异常"),
    TYPE_PARAM_WRONG(2005,"評論类型不存在"),
    COMMENT_NOT_FOUND(2006,"要回复的评论不存在"),
    CONTENT_IS_EMPTY(2007,"输入内容不能为空"),
    READ_NOTIFICATION_FAILED(2008,"非法读取他人信息"),
    NOTIFICATION_NOT_FOUND(2009,"通知消息不存在"),
    File_UPLOAD_FAILED(2010,"消息上传失败")
    ;

    private String message;
    private Integer code;

     CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
//Ctrl +F6实现 参数顺序替换
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }


}
