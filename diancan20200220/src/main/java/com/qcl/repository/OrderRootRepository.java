package com.qcl.repository;

import com.qcl.bean.WxOrderRoot;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 编程小石头：2501902696（微信）
 */
public interface OrderRootRepository extends JpaRepository<WxOrderRoot, Integer> {

    Page<WxOrderRoot> findByBuyerOpenid(String buyerOpenid, Pageable pageable);

    List<WxOrderRoot> findByBuyerOpenidAndOrderStatus(String buyerOpenid, Integer orderStatus);

    //Spring Boot Data Jpa 自定义查询
    //查询状态为1和6的数据。 1为待接单  6为已接单
    @Query("select wroot from WxOrderRoot wroot where wroot.orderStatus in (1,6)")
    List<WxOrderRoot> findBySquare();

//    WxOrderRoot
    List<WxOrderRoot> findByBuyerOpenid(String buyerOpenid);

    //查询接单人的openid
    @Query("select ws from WxOrderRoot ws where ws.orderStatus = 4 and ws.commentaa = ?1")
    List<WxOrderRoot> findByCommentaa(String commentaa);

    List<WxOrderRoot> findAll(Specification specification);
}
