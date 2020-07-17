package com.robot.host.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot.host.base.entry.SceneInfoEntity;
import com.robot.host.base.mapper.SceneInfoMapper;
import com.robot.host.base.service.SceneInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("sceneInfoService")
@Slf4j
public class SceneInfoServiceImpl extends ServiceImpl<SceneInfoMapper, SceneInfoEntity> implements SceneInfoService {

}
