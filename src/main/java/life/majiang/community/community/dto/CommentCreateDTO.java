package life.majiang.community.community.dto;

import lombok.Data;

@Data
public class CommentCreateDTO {
    private long parentId;
    private String content;
    private Integer type;
    //该DTO是在client页面端传输数据到db时，使用的。



}
