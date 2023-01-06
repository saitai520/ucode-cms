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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import xin.altitude.cms.common.util.EntityUtils;
import xin.altitude.cms.framework.core.domain.SysDictData;
import xin.altitude.cms.system.mapper.SysDictDataMapper;
import xin.altitude.cms.system.service.ISysDictDataService;

import java.util.List;

/**
 * 字典 业务层处理
 *
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 */
// @Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {
    // @Autowired
    // private SysDictDataMapper dictDataMapper;

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        // return dictDataMapper.selectDictDataList(dictData);
        return list(Wrappers.lambdaQuery(dictData));
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        // return dictDataMapper.selectDictLabel(dictType, dictValue);
        LambdaQueryWrapper<SysDictData> wrapper = Wrappers.lambdaQuery(SysDictData.class).select(SysDictData::getDictLabel).eq(SysDictData::getDictType, dictType).eq(SysDictData::getDictValue, dictValue);
        return EntityUtils.toObj(getOne(wrapper), SysDictData::getDictLabel);
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        // return dictDataMapper.selectDictDataById(dictCode);
        return getById(dictCode);
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes) {
        for (Long dictCode : dictCodes) {
            SysDictData data = selectDictDataById(dictCode);
            // dictDataMapper.deleteDictDataById(dictCode);
            removeById(dictCode);
            // List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            List<SysDictData> dictDatas = list(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getDictType, data.getDictType()));
            // DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
    }

    /**
     * 新增保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public boolean insertDictData(SysDictData data) {
        // int row = dictDataMapper.insertDictData(data);
        boolean row = save(data);
        if (row) {
            // List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            List<SysDictData> dictDatas = list(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getDictType, data.getDictType()));
            // DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 修改保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public boolean updateDictData(SysDictData data) {
        // int row = dictDataMapper.updateDictData(data);
        boolean row = updateById(data);
        if (row) {
            // List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            List<SysDictData> dictDatas = list(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getDictType, data.getDictType()));
            // DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }
}
