package com.pig4cloud.pig.admin.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 租户表
 *
 * @author paul
 * @date 2024-03-04 18:03:06
 */
@Data
@TableName("sys_tenant")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "租户表")
public class SysTenant extends Model<SysTenant> {

	/**
	 * 管理员账号
	 */
	private String adminAccount;

	/**
	* 租户ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="租户ID")
    private Long id;

	/**
	* 租户名称
	*/
    @Schema(description="租户名称")
    private String name;

	/**
	* logo地址
	*/
    @Schema(description="logo地址")
    private String logo;

	/**
	 * 域名地址
	 */
	@Schema(description="域名地址")
	private String url;

	/**
	 * 模块
	 */
	@Schema(description="模块")
	private String module;

	/**
	 * 硬件数量
	 */
	@Schema(description="硬件数量")
	private Integer machineNum;

	/**
	 * 授权数量
	 */
	@Schema(description="授权数量")
	private Integer authorizeNum;

	/**
	* 地区
	*/
    @Schema(description="地区")
    private String area;

	/**
	* 地址
	*/
    @Schema(description="地址")
    private String address;

	/**
	* 简介
	*/
    @Schema(description="简介")
    private String introduction;

	/**
	* 联系人
	*/
    @Schema(description="联系人")
    private String contact;

	/**
	* 联系电话
	*/
    @Schema(description="联系电话")
    private String contactPhone;

	/**
	 * 状态 -1删除 0禁用 1启用
	 */
	@Schema(description="状态")
	private Integer status;

	/**
	* 创建人
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建人")
    private String createBy;

	/**
	* 创建时间
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建时间")
    private LocalDateTime createTime;

	/**
	* 修改人
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="修改人")
    private String updateBy;

	/**
	* 修改时间
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="修改时间")
    private LocalDateTime updateTime;
}