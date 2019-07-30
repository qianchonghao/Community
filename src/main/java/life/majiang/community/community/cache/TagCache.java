package life.majiang.community.community.cache;

import life.majiang.community.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TagCache {
    public static List<TagDTO> get() {
        List<TagDTO> tagDTOS = new ArrayList<>();
        TagDTO program = new TagDTO();
        program.setCategoryName("开发语言");
        program.setTags(Arrays.asList("js", "java", "php", "html", "python"));
        tagDTOS.add(program);

        TagDTO frameWork = new TagDTO();
        frameWork.setCategoryName("平台框架");
        frameWork.setTags(Arrays.asList("spring", "flask", "laravel", "express", "django"));
        tagDTOS.add(frameWork);

        return tagDTOS;
    }

//    public static boolean isValid(String tags) {
//        String[] split = StringUtils.split(tags, ",");
//        List<TagDTO> tagDTOS = get();
//
//        List<String> tagList = tagDTOS.stream().flatMap(tagDTO ->
//                tagDTO.getTags().stream()
//        ).collect(Collectors.toList());//flatMap 应用于二层数组，返回Stream
//
//        String invaild = Arrays.stream(split).filter(t -> !tagList.contains(t)).collect(Collectors.joining(","));
//        //Arrays 常常用来数组--->List stream後跟方法无return 时候 默认返回<String>
//        //filter(t->t>5).collector(Collector.toList()); 返回仅包含元素大于5的List
//        if (!StringUtils.isBlank(invaild)) return false;
//        return true;
//    }
    public static boolean isValid(String tags){
            List<TagDTO> tagDTOS = get();
            List<String>tagList = tagDTOS.stream().map(tagDTO -> tagDTO.getTags()).
                    flatMap(strings -> strings.stream()).collect(Collectors.toList());
                    //map返回的是Stream<List<String>>
                    //flatMap返回stream<String>,各个数组不是分别映射成一个流，而是映射成流的内容。然后合并成一个流。
                    //前两者都是intermediate操作，collect是terminal操作，将stream转换为具体形式如：List等
        List<String>invaild = Arrays.asList(tags.split(",")).stream()
                .filter(string->!tagList.contains(string)).collect(Collectors.toList());
        if(invaild.size()>0) return false;
        return true;

    }
}
