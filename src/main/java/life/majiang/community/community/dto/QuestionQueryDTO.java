package life.majiang.community.community.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {
    private  String search;
    private  Integer offset;
    private  Integer size;

}
