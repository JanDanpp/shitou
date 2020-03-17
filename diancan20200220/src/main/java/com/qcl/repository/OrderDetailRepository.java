package com.qcl.repository;

import com.qcl.bean.WxOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 编程小石头：2501902696（微信）
 */
public interface OrderDetailRepository extends JpaRepository<WxOrderDetail, Integer> {

    List<WxOrderDetail> findByOrderId(Integer orderId);
}
