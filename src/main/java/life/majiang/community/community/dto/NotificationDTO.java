package life.majiang.community.community.dto;

import life.majiang.community.community.model.User;
import lombok.Data;

@Data
public class NotificationDTO {

    private Long notifier;
    private Long receiver;
    private Long outerid;




    private Long id;
    private Long gmtCreate;
    private Integer status;
    private User user;
    private String outrTile;
    private Integer type;


}
