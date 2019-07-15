package life.majiang.community.community.dto;

import life.majiang.community.community.model.User;
import lombok.Data;

@Data
public class QuestionDTO {
    private Integer id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator;
    private Integer likeCount;
    private Integer viewCount;
    private Integer commentCount;
    private User user;



}
