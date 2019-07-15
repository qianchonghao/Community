package life.majiang.community.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageDTO {
    private List<QuestionDTO> questionDTOList;
    private Boolean showFirstPage;
    private Boolean showEndPage;
    private Boolean showPrevious;
    private Boolean showNext;
    private int page;
    private List<Integer> pages = new ArrayList<Integer>();
    private Integer totalPage;


    public void setPageInfo(Integer totalPage, Integer page) {

        this.totalPage=totalPage;
        this.page = page;

        if(totalPage==0){
            showPrevious = false;
            showNext = false;
            showFirstPage = false;
            showEndPage = false;
        }else{
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
