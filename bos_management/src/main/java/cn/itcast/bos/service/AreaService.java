package cn.itcast.bos.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.Courier;

public interface AreaService {

	// 保存地区数据
	public void saveBatch(List<Area> areas);

	public Page<Area> findPageData(Specification<Area> specification,Pageable pageable);

}
