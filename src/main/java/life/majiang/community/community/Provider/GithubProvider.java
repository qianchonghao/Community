package life.majiang.community.community.Provider;

import com.alibaba.fastjson.JSON;
import life.majiang.community.community.dto.AccessToeknDTO;
import life.majiang.community.community.dto.GithubUser;
import life.majiang.community.community.model.User;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/*
1.想通过GithubProvider 携带access_token&code 访问github
2.import okhttp3 目的： 提供前端 css theme jsp文件
3.import json(工具类) :通过String<-->JSON 实现信息传递

 */
@Component
public class GithubProvider {

        public String getAccessToken(AccessToeknDTO accessTokenDTO) {
            //实现 code --->访问 github exchange for --->返回access Token ps:实现一来一回！
            MediaType mediaType
                    = MediaType.get("application/json; charset=utf-8");
            //MediaType作用：给Json数据一种格式要求？
            // OkHttpClient client = new OkHttpClient();
            OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(50000, TimeUnit.MILLISECONDS)
                    .readTimeout(50000, TimeUnit.MILLISECONDS)
                    .build();//扩大相应时间
        /*
        实现accessTokenDTO -->JSon数据 -->RequestBody
        根据accessTokenDTO建立请求体
         */
            RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
            //requestBody(mediaType,JSONString) 返回requestBody型变量
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
                //toString 属于Object.class的方法 实现return getClass().getName() + "@" + Integer.toHexString(hashCode());
                //string()属于responseBody的方法，可以返回responseBody的内容
                String tokenStr = str.split("&")[0];
                String token = tokenStr.split("=")[1];

                return token;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }



        public GithubUser getUser(String accessToken) {//注意accessToken可以在OAuth内自己创造，用于了解use信息的格式，从而设置GithubUser的变量！！！！！！！！！
            //实现 access Token --->访问Github exchange for --->user 信息
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.github.com/user?access_token=" + accessToken)
                    .build();
            //request url格式 从何而来？ 根据推测：accessToken会作为一个请求参数
            // url格式分析错误：url内部请求参数多个单词构成名称时候，一般是下划线分割？

            try (Response response = client.newCall(request).execute()) {
                String str = response.body().string();//string 和toString()区别
                GithubUser githubUser = JSON.parseObject(str, GithubUser.class);
                //parse()参数 (String JSON，Class)
                //json.parseObject 将 Str 转换成 GithubUser的对象
                return githubUser;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

}
