package com.example.demo.src.support_comment;

import com.example.demo.src.support_comment.model.DeleteSupportCommentReq;
import com.example.demo.src.support_comment.model.GetSupportCommentRes;
import com.example.demo.src.support_comment.model.PatchSupportCommentReq;
import com.example.demo.src.support_comment.model.PostSupportCommentReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SupportCommentDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 댓글 조회 API
     */
    public List<GetSupportCommentRes> getSupportComment(Integer support_idx) {
        String getSupportCommentQuery = "select * from Support_Comment join User on Support_Comment.user_idx = User.user_idx where support_idx = ? and support_comment_status = 'Y'";
        Integer getSupportCommentParams = support_idx;
        return this.jdbcTemplate.query(getSupportCommentQuery,
                (rs, rowNum) -> new GetSupportCommentRes(
                        rs.getInt("support_comment_idx"),
                        rs.getInt("support_idx"),
                        rs.getInt("user_idx"),
                        rs.getString("user_name"),
                        rs.getString("user_profileimage_url"),
                        rs.getString("support_comment_content"),
                        rs.getString("support_comment_updateAt")),
                getSupportCommentParams); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    /**
     * 댓글 작성 API
     */
    public Integer createSupportComment(PostSupportCommentReq postSupportCommentReq) {
        String createSupportCommentQuery = "insert into Support_Comment(support_idx, user_idx, support_comment_content) VALUES (?,?,?)"; // 실행될 동적 쿼리문
        Object[] createSupportCommentParams = new Object[]{postSupportCommentReq.getSupport_idx(), postSupportCommentReq.getUser_idx(), postSupportCommentReq.getSupport_comment_content()}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createSupportCommentQuery, createSupportCommentParams);

        String supportCommentCountUpQuery = "update Support set support_comment = support_comment + 1 where support_idx = ?";
        int supportCommentCountUpParams = postSupportCommentReq.getSupport_idx();
        this.jdbcTemplate.update(supportCommentCountUpQuery,supportCommentCountUpParams);

        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, Integer.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 BoardIdx번호를 반환한다.
    }


    /**
     * 댓글 수정 API
     */
    public int modifySupportComment(PatchSupportCommentReq patchSupportCommentReq) {
        String modifySupportCommentQuery = "update Support_Comment set support_comment_content = ? where support_comment_idx = ? ";
        Object[] modifySupportCommentParams = new Object[]{patchSupportCommentReq.getSupport_comment_content(),patchSupportCommentReq.getSupport_comment_idx()}; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifySupportCommentQuery, modifySupportCommentParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }


    /**
     * 댓글 삭제 API
     */
    public int deleteSupportComment(Integer support_idx, Integer support_comment_idx) {
        String supportCommentCountDownQuery = "update Support set support_comment = support_comment - 1 where support_idx = ?";
        this.jdbcTemplate.update(supportCommentCountDownQuery,support_idx);

        String deleteSupportCommentQuery = "update Support_Comment set support_comment_status = 'N' where support_comment_idx = ?";
        Object[] deleteSupportCommentParams = new Object[]{support_comment_idx};

        return this.jdbcTemplate.update(deleteSupportCommentQuery,deleteSupportCommentParams);

    }

}
