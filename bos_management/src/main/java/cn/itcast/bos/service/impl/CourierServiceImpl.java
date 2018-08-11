package cn.itcast.bos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.CourierRepository;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.service.CourierService;

@Service
@Transactional
public class CourierServiceImpl implements CourierService {

	// 注入dao层
	@Autowired
	private CourierRepository courierRepository;
	
	@Override
	public void save(Courier courier) {
		courierRepository.save(courier);

	}

	@Override
	public Page<Courier> findPageData(Specification<Courier> specification,Pageable pageable) {
		return courierRepository.findAll(specification,pageable);
	}

	@Override
	public void delBatch(String[] idArray) {
		for (String string : idArray) {
			Integer id = Integer.parseInt(string);
			// 通过id进行修改
			courierRepository.updateDelTag(id);
		}
		
	}

}
