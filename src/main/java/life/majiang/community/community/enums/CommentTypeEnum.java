package life.majiang.community.community.enums;

public enum CommentTypeEnum {
    QUESTION(1),
    COMMMENT(2);
    private Integer type;

     CommentTypeEnum(Integer type) {
        this.type = type;
    }

    public static boolean isExist(Integer type) {
        for(CommentTypeEnum commentTypeEnum:CommentTypeEnum.values()){
            if(commentTypeEnum.getType()==type) return true;
        }
        return false;
    }

    public Integer getType() {
        return type;
    }
}
//enum 枚举类 要写 value属性 Constructor get方法
//然后就是枚举列表 ｛k1（v1）,k2(v2),k3(v3)}