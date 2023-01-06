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

package xin.altitude.cms.merge;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import xin.altitude.cms.common.util.EntityUtils;
import xin.altitude.cms.common.util.PlusUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


/**
 * 基于MybatisPlus请求合并/拆分服务类
 *
 * @param <M> BaseMapper子类
 * @param <T> DO实体类泛型
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 1.5.8
 */
public class QueueServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
    /**
     * 并发安全队列，多个线程同时添加数据时保证线程安全
     */
    private final ConcurrentLinkedQueue<AsynFuture<T>> taskQueue = new ConcurrentLinkedQueue<>();

    /**
     * 无参数构造器
     */
    public QueueServiceImpl() {
    }

    /**
     * 构建任务实例
     *
     * @param maxRequestSize 单次最大合并请求数量
     * @param queue          任务队列
     * @return 任务实例
     */
    private Runnable createTask(int maxRequestSize, Queue<AsynFuture<T>> queue) {
        return () -> {
            // 如果排队队列没有任务 则快速返回 不执行数据库访问
            if (queue.size() > 0) {
                TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
                Class<?> keyType = tableInfo.getKeyType();

                List<AsynFuture<T>> requests = MergeHelper.pollFuture(queue, maxRequestSize);
                Set<Serializable> ids = EntityUtils.toSet(requests, AsynFuture::getId);
                List<T> list = this.listByIds(ids);
                Map<Serializable, T> map = EntityUtils.toMap(list, e -> PlusUtils.pkVal(tableInfo, e), e -> e);
                requests.forEach((e) -> e.getFuture().complete(MergeHelper.fetchMapValue(map, keyType, e.getId())));
            }
        };
    }

    /**
     * <p>重写了父类{@code getById}</p>
     * <p>通过主键查询实体</p>
     * <p>此方法是对外提供服务的唯一通道</p>
     * <p>屏蔽了请求合并/拆分的具体细节 用户无感知编程</p>
     *
     * @param id 主键ID
     * @return 泛型实体实例
     * @see com.baomidou.mybatisplus.extension.service.IService#getById(Serializable)
     */
    @Override
    public T getById(Serializable id) {
        CompletableFuture<T> future = new CompletableFuture<>();
        taskQueue.add(new AsynFuture<>(id, future));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    /**
     * <p>修改服务类的配置行为</p>
     * <p>子类重写{@link QueueServiceImpl#createReqConfig}方法</p>
     */
    @PostConstruct
    public void init() {
        ReqConfig config = createReqConfig();

        Runnable task = createTask(config.getMaxReqSize(), taskQueue);

        if (config.getThreadPool() != null) {
            config.getThreadPool().scheduleAtFixedRate(task, 0L, config.getReqInterval(), TimeUnit.MILLISECONDS);
        } else {
            ThreadFactory threadFactory = config.getThreadFactory();
            ScheduledExecutorService threadPool = new ScheduledThreadPoolExecutor(config.getCorePoolSize(), threadFactory);
            threadPool.scheduleAtFixedRate(task, 0L, config.getReqInterval(), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * <p>父类调用</p>
     * <p>子类重写修改参数行为</p>
     *
     * @return {@link ReqConfig}实例
     */
    protected ReqConfig createReqConfig() {
        return new ReqConfig();
    }


}
