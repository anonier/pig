package com.pig4cloud.pig.admin.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.admin.api.entity.SysTenant;
import com.pig4cloud.pig.admin.api.vo.TenantPageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface SysTenantMapper extends BaseMapper<SysTenant> {

	@Select("<script>"
			+ "SELECT t.*, count(m.id) as machineNum from sys_tenant t left join gc_machine m on m.tenant_id = t.id AND m.status >= 0 "
			+ "WHERE t.status>= 0 "
			+ "<if test='vo.name != null'> "
			+ "and t.name like #{vo.name} "
			+ "</if>"
			+ "<if test='vo.status != null'> "
			+ "and t.status = #{vo.status} "
			+ "</if> "
			+ "GROUP BY t.id "
			+ "</script>")
	Page<SysTenant> tenantPage(@Param("vo") TenantPageVo vo);
}