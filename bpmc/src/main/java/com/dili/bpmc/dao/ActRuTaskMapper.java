package com.dili.bpmc.dao;

import java.util.List;

import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.dto.TaskIdentityDto;

/**
 * @Author: WangMi
 * @Date: 2019/12/18 9:58
 * @Description:
 */
public interface ActRuTaskMapper {

	/**
	 * 查询运行时任务列表
	 * 
	 * @return
	 */
	List<TaskMapping> list(TaskDto taskDto);

	/**
	 * 查询用户的候选任务
	 * 
	 * @param userId
	 * @return
	 */
	int taskCandidateUserCount(String userId);

	/**
	 * 根据流程id批量查询任务候选组、候选人、办理人
	 * 
	 * @param processIntanceIds
	 * @return
	 */
	List<TaskIdentityDto> listTaskIdentityByProcessInstanceIds(List<String> processInstanceIds);
}
