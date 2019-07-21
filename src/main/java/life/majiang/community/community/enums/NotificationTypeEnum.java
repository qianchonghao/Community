package life.majiang.community.community.enums;


public enum NotificationTypeEnum {
    REPLY_QUESTION(1,"回复了问题"),
    REPLY_COMMENT(2,"回复了评论"),
    LIKE_QUESTION(3,"点赞了问题")
    ;

    private int type;
    private String message;

    NotificationTypeEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
