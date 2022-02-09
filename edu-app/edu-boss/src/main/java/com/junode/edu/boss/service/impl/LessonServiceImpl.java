package com.junode.edu.boss.service.impl;

import java.util.UUID;

import com.junode.edu.boss.service.ILessonService;
import com.junode.edu.common.constant.MQConstant;
import com.junode.edu.common.mq.dto.BaseMqDTO;
import com.junode.edu.common.result.ResultCode;
import com.junode.edu.common.util.ValidateUtils;
import com.junode.edu.course.api.LessonRemoteService;
import com.junode.edu.course.api.dto.LessonDTO;
import com.junode.edu.course.api.enums.CourseLessonStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.junode.edu.common.mq.RocketMqService;
import com.junode.edu.message.api.dto.LessonStatusReleaseDTO;

/**
 * @author: ma wei long
 * @date:   2020年6月29日 下午10:46:57
 */
@Service
public class LessonServiceImpl implements ILessonService {

	@Autowired
    private LessonRemoteService lessonRemoteService;
    @Autowired
	private RocketMqService rocketMqService;
	
	/**
	 * @author: ma wei long
	 * @date:   2020年6月29日 下午10:46:15   
	*/
	@Override
	public boolean saveOrUpdate(LessonDTO lessonDTO) {
		boolean isRelease = false;
		if(lessonDTO.getId() != null && lessonDTO.getStatus() != null && lessonDTO.getStatus().equals(CourseLessonStatus.RELEASE.getCode())) {
			//查看是否是上架操作
			LessonDTO lessonDB = lessonRemoteService.getById(lessonDTO.getId());
			ValidateUtils.notNull(lessonDB, ResultCode.ALERT_ERROR.getState(), "课时信息查询为空");
			isRelease = !lessonDB.getStatus().equals(CourseLessonStatus.RELEASE.getCode());
		}
		boolean res = lessonRemoteService.saveOrUpdate(lessonDTO);
		if(res && isRelease) {//发送消息通知
			rocketMqService.convertAndSend(MQConstant.Topic.LESSON_STATUS_RELEASE, new BaseMqDTO<LessonStatusReleaseDTO>(new LessonStatusReleaseDTO(lessonDTO.getId()),UUID.randomUUID().toString()));
		}
		return res;
	}
}