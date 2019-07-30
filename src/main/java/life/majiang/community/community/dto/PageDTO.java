package life.majiang.community.community.dto;

import life.majiang.community.community.model.Question;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageDTO<T> {
//pageDTO不只是为了question服务，还有其他类的分页列表需要展示，所以采用了泛型。

    private List<T>datas;
    private List<Integer>pages;//pages：当前页面下，分页栏内page范围的信息
    private boolean showFirstPage;
    private boolean showEndPage;
    private boolean showPrevious;
    private boolean showNext;
    private int page;
    private int totalPage;


    public void setPageInfo(Integer totalPage, Integer page) {
        //将除了 datas以外的数据set都设置在这个方法中。
        //因为不同的datas，他的list构成方法不同，应该在不同的service中另行处理。
        this.pages=new ArrayList<>();
        this.totalPage = totalPage;//totalpage意义：showEndPage 在html端需要 totalpage的值
        this.page = page;

        if (totalPage == 0) {
            showPrevious = false;
            showNext = false;
            showFirstPage = false;
            showEndPage = false;
        } else {
            //當前頁面左右各取3頁 加入pages
            for (int i = 3; i >= 0; i--) {
                if (page - i > 0) {
                    pages.add(page - i);
                }

            }

            for (int i = 1; i <= 3; i++) {
                if (page + i <= totalPage) {
                    pages.add(page + i);
                }
            }


            //是否展示上一页
            if (page == 1) {
                showPrevious = false;
            } else {
                showPrevious = true;
            }

            //是否展示下一页
            if (page == totalPage) {
                showNext = false;
            } else {
                showNext = true;
            }

            if (pages.contains(1)) {
                showFirstPage = false;
            } else {
                showFirstPage = true;
            }


            if (pages.contains(totalPage)) {
                showEndPage = false;
            } else {
                showEndPage = true;
            }
        }


    }



}
