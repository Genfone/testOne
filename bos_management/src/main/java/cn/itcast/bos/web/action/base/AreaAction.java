package cn.itcast.bos.web.action.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.service.AreaService;
import cn.itcast.bos.utils.PinYin4jUtils;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class AreaAction extends ActionSupport implements ModelDriven<Area> {

	// 模型驱动
	private Area area = new Area();
	
	@Override
	public Area getModel() {
		return area;
	}
	
	// 属性驱动,接受上传文件
	private File file;

	public void setFile(File file) {
		this.file = file;
	}
	
	// 注入业务层
	@Autowired
	private AreaService areaService;
	
	// 批量地区数据的导入方法
	@Action(value="area_batchImport")
	public String batchImport() throws IOException {
		List<Area> areas = new ArrayList<Area>();
		// 编写解析代码逻辑
		// 基于.xls格式解析的HSSF
		// 1.加载excel文件对象
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(file));
		// 2.读写一个sheet
		HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
		// 3.读取sheet中的每一行
		for (Row row : sheet) {
			// 一行数据对应一个区域对象
			if(row.getRowNum() == 0){
				// 第一行跳过
				continue;
			}
			// 空行也跳过
			if(row.getCell(0) == null || StringUtils.isBlank(row.getCell(0).getStringCellValue())){
				continue;
			}
			Area area2 = new Area();
			area2.setId(row.getCell(0).getStringCellValue());// 获取id
			area2.setProvince(row.getCell(1).getStringCellValue());// 获取省份
			area2.setCity(row.getCell(2).getStringCellValue());// 获取市
			area2.setDistrict(row.getCell(3).getStringCellValue());// 获取区域
			area2.setPostcode(row.getCell(4).getStringCellValue());// 获取邮编
			
			// 基于pinyin4j生成城市编码和城市简码
			String province = area2.getProvince();
			String city = area2.getCity();
			String district = area2.getDistrict();
			province = province.substring(0, province.length()-1);
			city = city.substring(0, city.length()-1);
			district = district.substring(0, district.length()-1);
			// 简码
			String[] headByString = PinYin4jUtils.getHeadByString(province+city+district);
			StringBuffer stringBuffer = new StringBuffer();
			for (String headStr : headByString) {
				stringBuffer.append(headStr);
			}
			String shortCode = stringBuffer.toString();
			area2.setShortcode(shortCode);
			// 城市编码
			String cityCode = PinYin4jUtils.hanziToPinyin(city,"");
			area2.setCitycode(cityCode);
			
			areas.add(area2);
		}
		// System.out.println(areas);
		// 调用业务成，将数据保存到数据库
		areaService.saveBatch(areas);
		System.out.println("上传成功");
		return NONE;
	}
	
	// 属性驱动
	private int page;
	private int rows;
	
	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	// 地区条件分页查询方法
	@Action(value="area_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		
		Specification<Area> specification = new Specification<Area>() {

			@Override
			public Predicate toPredicate(Root<Area> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				// 当前查询根对象Root的Area
				List<Predicate> list = new ArrayList<Predicate>();
				// 单表查询
				if(StringUtils.isNotBlank(area.getProvince())){
					// 进行省份模糊查询 province like
					Predicate p1 = cb.like(root.get("province").as(String.class), "%"+area.getProvince()+"%");
					
					list.add(p1);
				}
				// 根据市模糊查询
				if(StringUtils.isNotBlank(area.getCity())){
					// city like
					Predicate p2 = cb.like(root.get("city").as(String.class), "%"+area.getCity()+"%");
					
					list.add(p2);
				}
				// 根据地区模糊查询
				if(StringUtils.isNotBlank(area.getDistrict())){
					// district like
					Predicate p3 = cb.like(root.get("district").as(String.class), "%"+area.getDistrict()+"%");
				
					list.add(p3);
				}
				
				return cb.and(list.toArray(new Predicate[0]));
			}
		};
		
		// 封装pageable对象
		Pageable pageable = new PageRequest(page - 1, rows);
		// 调用业务层返回page
		Page<Area> pageData = areaService.findPageData(specification,pageable);
		// 把数据装在集合中
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", pageData.getTotalElements());
		result.put("rows", pageData.getContent());
		// 把结果压入值栈顶
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}

	
}
