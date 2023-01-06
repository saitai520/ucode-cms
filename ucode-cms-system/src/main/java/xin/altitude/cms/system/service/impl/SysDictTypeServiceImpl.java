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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import xin.altitude.cms.common.util.StringUtil;
import xin.altitude.cms.framework.constant.UserConstants;
import xin.altitude.cms.framework.core.domain.SysDictData;
import xin.altitude.cms.framework.core.domain.SysDictType;
import xin.altitude.cms.framework.exception.ServiceException;
import xin.altitude.cms.system.mapper.SysDictTypeMapper;
import xin.altitude.cms.system.service.ISysDictDataService;
import xin.altitude.cms.system.service.ISysDictTypeService;

import java.util.List;

/**
 * 字典 业务层处理
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
// @Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService {
    // @Autowired
    // private SysDictTypeMapper dictTypeMapper;

    // @Autowired
    // private SysDictDataMapper dictDataMapper;
    @Autowired
    private ISysDictDataService sysDictDataService;

    /**
     * 项目启动时，初始化字典到缓存
     */
    // @PostConstruct
    public void init() {
        loadingDictCache();
    }

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        // return dictTypeMapper.selectDictTypeList(dictType);
        return list(Wrappers.lambdaQuery(dictType));
    }

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeAll() {
        // return dictTypeMapper.selectDictTypeAll();
        return list(Wrappers.lambdaQuery());
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        // List<SysDictData> dictDatas = DictUtils.getDictCache(dictType);
        // if (StringUtils.isNotEmpty(dictDatas)) {
        //     return dictDatas;
        // }
        // dictDatas = dictDataMapper.selectDictDataByType(dictType);
        return sysDictDataService.list(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getDictType, dictType));
        // if (StringUtils.isNotEmpty(dictDatas)) {
        //     DictUtils.setDictCache(dictType, dictDatas);
        //     return dictDatas;
        // }
        // return null;
    }

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        // return dictTypeMapper.selectDictTypeById(dictId);
        return getById(dictId);
    }

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        // return dictTypeMapper.selectDictTypeByType(dictType);
        return getOne(Wrappers.lambdaQuery(SysDictType.class).eq(SysDictType::getDictType, dictType));
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictType dictType = selectDictTypeById(dictId);
            // int count = dictDataMapper.countDictDataByType(dictType.getDictType());
            long count = sysDictDataService.count(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getDictType, dictType.getDictType()));
            if (count > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
            // dictTypeMapper.deleteDictTypeById(dictId);
            removeById(dictId);
            // DictUtils.removeDictCache(dictType.getDictType());
        }
    }

    /**
     * 加载字典缓存数据
     */
    @Override
    public void loadingDictCache() {
        // List<SysDictType> dictTypeList = dictTypeMapper.selectDictTypeAll();
        List<SysDictType> dictTypeList = list();
        for (SysDictType dictType : dictTypeList) {
            // List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictType.getDictType());
            List<SysDictData> dictDatas = sysDictDataService.list(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getDictType, dictType.getDictType()));
            // DictUtils.setDictCache(dictType.getDictType(), dictDatas);
        }
    }

    /**
     * 清空字典缓存数据
     */
    @Override
    public void clearDictCache() {
        // DictUtils.clearDictCache();
    }

    /**
     * 重置字典缓存数据
     */
    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    /**
     * 新增保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
    @Override
    public boolean insertDictType(SysDictType dict) {
        // int row = dictTypeMapper.insertDictType(dict);
        boolean row = save(dict);
        if (row) {
            // DictUtils.setDictCache(dict.getDictType(), null);
        }
        return row;
    }

    /**
     * 修改保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
    @Override
    @Transactional
    public boolean updateDictType(SysDictType dict) {
        // SysDictType oldDict = dictTypeMapper.selectDictTypeById(dict.getDictId());
        SysDictType oldDict = getById(dict.getDictId());
        // dictDataMapper.updateDictDataType(oldDict.getDictType(), dict.getDictType());
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictType(dict.getDictType());
        sysDictDataService.update(sysDictData, Wrappers.lambdaUpdate(SysDictData.class).eq(SysDictData::getDictType, oldDict.getDictType()));
        boolean row = updateById(dict);
        if (row) {
            // List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dict.getDictType());
            List<SysDictData> dictDatas = sysDictDataService.list(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getDictType, dict.getDictType()));
            // DictUtils.setDictCache(dict.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public String checkDictTypeUnique(SysDictType dict) {
        Long dictId = StringUtil.isNull(dict.getDictId()) ? -1L : dict.getDictId();
        // SysDictType dictType = dictTypeMapper.checkDictTypeUnique(dict.getDictType());
        SysDictType dictType = getOne(Wrappers.lambdaQuery(SysDictType.class).eq(SysDictType::getDictType, dict.getDictType()));
        if (StringUtil.isNotNull(dictType) && dictType.getDictId().longValue() != dictId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}
