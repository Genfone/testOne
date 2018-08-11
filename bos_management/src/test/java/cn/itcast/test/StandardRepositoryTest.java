package cn.itcast.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.itcast.bos.dao.StandardRepository;
import cn.itcast.bos.domain.base.Standard;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class StandardRepositoryTest {

	@Autowired
	private StandardRepository standardRepository;
	
	@Test
	public void test1(){
		System.out.println(standardRepository.findByName("20-40公斤"));
	}
	
	@Test
	public void test2(){
		List<Standard> list = standardRepository.findAll();
		
		for (Standard standard : list) {
			System.out.println(standard);
		}
	}
}
