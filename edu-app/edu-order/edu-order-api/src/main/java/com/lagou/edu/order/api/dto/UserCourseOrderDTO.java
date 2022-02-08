package com.lagou.edu.order.api.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:(商品订单表)   
 * @author: ma wei long
 * @date:   2020年6月18日 下午4:47:50
*/
//@Data
@EqualsAndHashCode(callSuper = false)
public class UserCourseOrderDTO implements Serializable {

    /**
	 */
	private static final long serialVersionUID = -4920910964792305692L;
	private Integer id;//id
    private String orderNo;//订单号 唯一
    private Integer userId;//用户id
    private Integer courseId;//课程id，根据订单中的课程类型来选择
    private Integer sourceType;//订单来源类型: 1 用户下单购买 2 后台添加专栏
    private Integer status;//当前状态: 0已创建 10已支付 20已完成 30已取消 40已过期 
    private Integer isDel;//是否删除
    private Date createTime;//创建时间
    private Date update_time;//更新时间
    private Integer activityCourseId;//活动课程id

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getActivityCourseId() {
        return activityCourseId;
    }

    public void setActivityCourseId(Integer activityCourseId) {
        this.activityCourseId = activityCourseId;
    }
}