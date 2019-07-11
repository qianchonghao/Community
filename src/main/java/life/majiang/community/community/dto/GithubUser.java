package life.majiang.community.community.dto;

import lombok.Data;

@Data
public class GithubUser {
    private String name;
    private Long id;
    private String bio;
    private String login;
    private String avatarUrl;
    @Override
    public String toString() {
        return "GithubUser{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", bio='" + bio + '\'' +
                ", login='" + login + '\'' +
                '}';
    }


}
