package cn.itcast.bos.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.itcast.bos.domain.base.Standard;

public interface StandardRepository extends JpaRepository<Standard, Integer> {

	// 根据收派标准名称来查询
	public List<Standard> findByName(String name);
	
	// @Query(value="from Standard where name = ?",nativeQuery=false)
	// nativeQuery的值为false，配置JPQL，为true则是SQL
	// public List<Standard> queryName(String name);
	
	// @Query(value="from Standard")
	// public List<Standard> 
	
	// 查询所有的数据
	
}
