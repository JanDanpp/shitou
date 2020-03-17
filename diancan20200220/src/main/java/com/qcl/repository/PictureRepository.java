package com.qcl.repository;

import com.qcl.bean.PictureInfo;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 编程小石头：2501902696（微信）
 */
public interface PictureRepository extends JpaRepository<PictureInfo, Integer> {
    PictureInfo findByPicId(Integer picId);
}
