/*
 *
 * Copyright (c) 2020-2022, Java知识图谱 (http://www.altitude.xin).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package xin.altitude.cms.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import xin.altitude.cms.common.constant.Constants;
import xin.altitude.cms.common.util.ConvertUtils;
import xin.altitude.cms.common.util.StringUtil;
import xin.altitude.cms.framework.constant.UserConstants;
import xin.altitude.cms.framework.exception.ServiceException;
import xin.altitude.cms.system.domain.SysConfig;
import xin.altitude.cms.system.mapper.SysConfigMapper;
import xin.altitude.cms.system.service.ISysConfigService;

import java.util.List;

/**
 * 参数配置 服务层实现
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
// @Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {
    // @Autowired
    // private SysConfigMapper configMapper;

    // @Autowired
    // private RedisCache redisCache;

    /**
     * 项目启动时，初始化参数到缓存
     */
    // @PostConstruct
    public void init() {
        loadingConfigCache();
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    // @DataSource(DataSourceType.MASTER)
    public SysConfig selectConfigById(Long configId) {
        SysConfig config = new SysConfig();
        config.setConfigId(configId);
        // return configMapper.selectConfig(config);
        return getById(configId);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        // String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
        // if (StringUtils.isNotEmpty(configValue)) {
        //     return configValue;
        // }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        // SysConfig retConfig = configMapper.selectConfig(config);
        SysConfig retConfig = getOne(Wrappers.lambdaQuery(config));
        if (StringUtil.isNotNull(retConfig)) {
            // redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtil.EMPTY;
    }

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaOnOff() {
        String captchaOnOff = selectConfigByKey("sys.account.captchaOnOff");
        if (StringUtil.isEmpty(captchaOnOff)) {
            return true;
        }
        return ConvertUtils.toBool(captchaOnOff);
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        // return configMapper.selectConfigList(config);
        return list(Wrappers.lambdaQuery(config));
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config) {
        // int row = configMapper.insertConfig(config);
        boolean row = save(config);
        if (row) {
            // redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row ? 1 : 0;
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config) {

        // int row = configMapper.updateConfig(config);
        boolean row = updateById(config);
        if (row) {
            // redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row ? 1 : 0;
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds) {
        for (Long configId : configIds) {
            SysConfig config = this.getById(configId);
            if (StringUtil.equals(UserConstants.YES, config.getConfigType())) {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            removeById(configId);
            // configMapper.deleteConfigById(configId);
            // redisCache.remove(getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        // List<SysConfig> configsList = configMapper.selectConfigList(new SysConfig());
        List<SysConfig> configsList = list(Wrappers.lambdaQuery());
        // for (SysConfig config : configsList) {
        //     redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        // }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        // Collection<String> keys = redisCache.keys(Constants.SYS_CONFIG_KEY + "*");
        // redisCache.remove(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String checkConfigKeyUnique(SysConfig config) {
        Long configId = StringUtil.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        // SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        SysConfig info = getOne(Wrappers.lambdaQuery(SysConfig.class).eq(SysConfig::getConfigKey, config.getConfigKey()));
        if (StringUtil.isNotNull(info) && info.getConfigId().longValue() != configId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return Constants.SYS_CONFIG_KEY + configKey;
    }
}
