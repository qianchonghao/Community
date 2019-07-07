package life.majiang.community.community.Provider;
import com.alibaba.fastjson.JSON;
import life.majiang.community.community.dto.AccessToeknDTO;
import life.majiang.community.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;
import java.io.IOException;

/*
1.想通过GithubProvider 携带access_token&code 访问github
2.import okhttp3 和 json(工具类) 来达到 获取返回信息？

 */
@Component
public class GithubProvider {

    public String getAccessToken(AccessToeknDTO accessTokenDTO){
        //实现 code --->访问 github exchange for --->返回access Token ps:实现一来一回！
        MediaType mediaType
                = MediaType.get("application/json; charset=utf-8");
        //MediaType作用：给Json数据一种格式要求？
        OkHttpClient client = new OkHttpClient();
        /*
        实现accessTokenDTO -->JSon数据 -->RequestBody
        根据accessTokenDTO建立请求体
         */
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));

        Request request = new Request.Builder()
                    .url("https://github.com/login/oauth/access_token")
                    .post(body)
                    .build();
        try (Response response = client.newCall(request).execute()) {
            /*
            1.OKHttpClient client 中 ，client.newCall 返回一个Response对象
            2.execute 执行访问GitHub ，Response携带 accessToken （String 类型）

             */
            String str = response.body().string();
            String tokenStr = str.split("&")[0];
            String token = tokenStr.split("=")[1];
            return token;

             } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    public GithubUser getUser(String accessToken){
        //实现 access Token --->访问Github exchange for --->user 信息
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String str = response.body().string();
            GithubUser githubUser=JSON.parseObject(str,GithubUser.class);
            //json.parseObject 将 Str 转换成 GithubUser的对象
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
