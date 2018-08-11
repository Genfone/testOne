package cn.itcast.bos.web.action.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
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

import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.service.CourierService;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@ParentPackage("json-default")
@Namespace("/")
@Actions
@Controller
@Scope("prototype")
public class CourierAction extends ActionSupport implements
		ModelDriven<Courier> {

	// 模型驱动
	private Courier courier = new Courier();

	@Override
	public Courier getModel() {
		return courier;
	}

	// 注入service层
	@Autowired
	private CourierService courierService;

	// 添加快递员的方法
	@Action(value = "courier_save", results = { @Result(name = "success", location = "./pages/base/courier.html", type = "redirect") })
	public String save() {
		courierService.save(courier);
		return SUCCESS;
	}

	private int page;
	private int rows;

	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	// 无条件分页查询方法
	@Action(value = "courier_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {

		Specification<Courier> specification = new Specification<Courier>() {

			/**
			 * 构造条件查询方法，如果返回值为null，代表无条件查询 Root 参数获取条件表达式，name=?,age=?
			 * CriteriaQuery 构造简单查询条件返回，提供where CriteriaBuilder 参数
			 * ，构造Predicate对象，条件对象，构造复杂查询效果
			 * 
			 */
			@Override
			public Predicate toPredicate(Root<Courier> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				// 当前查询跟对象Root 的Courier
				List<Predicate> list = new ArrayList<Predicate>();

				// 单表查询（查询当前对象对应的数据表）
				if (StringUtils.isNoneBlank(courier.getCourierNum())) {
					// 进行快递员工号查询
					// courierNum = ?
					Predicate p1 = cb.equal(
							root.get("courierNum").as(String.class),
							courier.getCourierNum());

					list.add(p1);
				}

				if (StringUtils.isNoneBlank(courier.getCompany())) {
					// 对公司进行模糊查询
					// conpany like % %
					Predicate p2 = cb.like(
							root.get("company").as(String.class),
							"%" + courier.getCompany() + "%");

					list.add(p2);
				}

				if (StringUtils.isNoneBlank(courier.getType())) {
					// 快递员类型查询，等值查询 type=？
					Predicate p3 = cb.equal(root.get("type").as(String.class),
							courier.getType());

					list.add(p3);
				}

				// 对收派标准的查询，这涉及到多表联合查询
				// 使用Courier(Root)，关联到Standard
				Join<Object, Object> standardRoot = root.join("standard",
						JoinType.INNER);// 内连接

				if (courier.getStandard() != null
						&& StringUtils.isNoneBlank(courier.getStandard()
								.getName())) {
					// 派收标准进行模糊查询
					Predicate p4 = cb.like(
							standardRoot.get("name").as(String.class), "%"
									+ courier.getStandard().getName() + "%");

					list.add(p4);
				}

				return cb.and(list.toArray(new Predicate[0]));
			}
		};

		// 封装pageable对象
		Pageable pageable = new PageRequest(page - 1, rows);
		// 调用业务层返回page
		Page<Courier> pageData = courierService.findPageData(specification,
				pageable);
		// 把数据装在集合中
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", pageData.getTotalElements());
		result.put("rows", pageData.getContent());
		// 把结果压入值栈顶
		ActionContext.getContext().getValueStack().push(result);

		return SUCCESS;
	}

	// 属性驱动
	private String ids;

	public void setIds(String ids) {
		this.ids = ids;
	}

	// 作废快递员的方法
	@Action(value = "courier_delbatch", results = { @Result(name = "success", type = "redirect", location = "./pages/base/courier.html") })
	public String delBatch() {
		String[] idArray = ids.split(",");
		// 调用业务层进行删除操作
		courierService.delBatch(idArray);

		return SUCCESS;
	}

}
