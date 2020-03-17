package com.qcl.repository;

import com.qcl.bean.AdminInfo;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 编程小石头：2501902696（微信）
 */
public interface AdminRepository extends JpaRepository<AdminInfo, Integer> {
    //    List<AdminInfo> findByPhoneOrUsername(String phone);
//    void findByUsername();
    AdminInfo findByPhoneOrUsername(String phone, String userName);

    AdminInfo findByAdminId(Integer adminId);
}
