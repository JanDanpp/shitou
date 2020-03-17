package com.qcl.repository;

import com.qcl.bean.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 微信：2501902696
 * desc:
 */
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByOpenid(String openid);

    Comment findByRootId(Integer rootId);

    //收到的评论
    @Query("select t.name from Comment t where t.commentId = 68 ")
    List<Comment> shop(String openid);



}
