package com.qcl.repository;

import com.qcl.bean.Leimu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 编程小石头：2501902696（微信）
 */
public interface LeiMuRepository extends JpaRepository<Leimu, Integer> {

    List<Leimu> findByLeimuTypeIn(List<Integer> categoryTypeList);

    List<Leimu> findByLeimuType(Integer categoryType);

}
