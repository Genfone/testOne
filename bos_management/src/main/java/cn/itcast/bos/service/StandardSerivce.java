package cn.itcast.bos.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.itcast.bos.domain.base.Standard;



public interface StandardSerivce {

	public void save(Standard standard);

	// 分页查询派收标准
	public Page<Standard> findPageData(Pageable pageable);

	public List<Standard> findAll();
}
