package com.example.demo.src.scholarship;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.scholarship.model.GetScholarshipMyfilter;
import com.example.demo.src.scholarship.model.GetScholarshipRes;
import com.example.demo.src.scholarship.model.PostScholarshipReq;
import com.example.demo.src.scholarship.model.PostScholarshipRes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/scholarships")

public class ScholarshipController {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired
    private final ScholarshipProvider scholarshipProvider;

    @Autowired
    private final ScholarshipService scholarshipService;

    public ScholarshipController(ScholarshipProvider scholarshipProvider, ScholarshipService scholarshipService) {

        this.scholarshipProvider = scholarshipProvider;
        this.scholarshipService = scholarshipService;
    }

    /**
     * 필터에 맞는 장학금들 조회 API
     * [GET] /scholarships?category=&filter=&order=
     */
    @ResponseBody   // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    //  JSON은 HTTP 통신 시, 데이터를 주고받을 때 많이 쓰이는 데이터 포맷.
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    // GET 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<List<GetScholarshipRes>> getScholarships(@RequestParam(required = false)String category, @RequestParam(required = false)String filter, @RequestParam(required = false)String order){
        //  @RequestParam은, 1개의 HTTP Request 파라미터를 받을 수 있는 어노테이션(?뒤의 값). default로 RequestParam은 반드시 값이 존재해야 하도록 설정되어 있지만, (전송 안되면 400 Error 유발)
        //  지금 예시와 같이 required 설정으로 필수 값에서 제외 시킬 수 있음
        //  defaultValue를 통해, 기본값(파라미터가 없는 경우, 해당 파라미터의 기본값 설정)을 지정할 수 있음

        //  카테고리 default 는 전체
        //          국가장학
        //          KRA와 함께하는 농어촌 희망재단 장학금
        //          교내 신입생 입학성적 우수장학금
        //          교내 재학생 장학금
        //          교외장학
        //          교내장학
        //          학비대출
        //          기타
        //          국가근로
        //          성적우수장학금
        //          특별감면장학금
        //          가계곤란자 장학금
        //          근로 장학금
        //  필터 	default 는 인기순
        //          날짜순
        //          인기순
        //          댓글순
        //  정렬	    default 는 desc
        //          desc
        //          asc

        try {
            List<GetScholarshipRes> getScholarshipsRes = scholarshipProvider.getScholarshipsByFilter(category, filter, order);
            return new BaseResponse<>(getScholarshipsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


     /**
     * 장학금 1개 조회 API
     * [GET] /scholarships/:scholarshipIdx
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{scholarshipIdx}") // (GET) 127.0.0.1:9000/scholarships/:scholarshipIdx
    public BaseResponse<GetScholarshipRes> getScholarship(@PathVariable("scholarshipIdx") long scholarshipIdx) {
        try {
            if (scholarshipProvider.checkScholarshipIdx(scholarshipIdx) == 0) {
                throw new BaseException(SCHOLARSHIP_EMPTY_SCHOLARSHIP_IDX);
            }
            scholarshipService.increaseScholarshipView(scholarshipIdx);
            GetScholarshipRes getScholarshipRes = scholarshipProvider.getScholarship(scholarshipIdx);
            return new BaseResponse<>(getScholarshipRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 장학금 추가 API
     * [POST] /scholarships/new-scholarship
     */
    @ResponseBody
    @PostMapping("/new-scholarship")
    public BaseResponse<PostScholarshipRes> createScholarship(@RequestBody PostScholarshipReq postScholarshipReq) {
        //장학금 이름을 입력하지 않으면 에러메시지
        if (postScholarshipReq.getScholarship_name() == null) {
            return new BaseResponse<>(POST_SCHOLARSHIP_EMPTY_NAME);
        }
        try {
            PostScholarshipRes postScholarshipRes = scholarshipService.createScholarship(postScholarshipReq);
            return new BaseResponse<>(postScholarshipRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * myfilter로 조회 API
     * */
    @ResponseBody
    @GetMapping("/myfilter")
    public BaseResponse<List<GetScholarshipRes>> getScholarshipMyfilter(@RequestBody GetScholarshipMyfilter getScholarshipMyfilter) {
        try {
            List<GetScholarshipRes> getScholarshipRes = scholarshipProvider.getScholarshipMyfilter(getScholarshipMyfilter);
            return new BaseResponse<>(getScholarshipRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}

