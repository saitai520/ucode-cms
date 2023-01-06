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

package xin.altitude.cms.framework.config;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import xin.altitude.cms.common.constant.CNTC;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 * @since 2019/07/05 14:18
 **/
// @Configuration
@ConditionalOnClass({Cache.class})
public class SpringCaffeineConfig extends AbstractCaffeineConfig {

    @Bean
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = new ArrayList<>();
        caches.add(new CaffeineCache(CNTC.CACHE_05SECS, expire(5, TimeUnit.SECONDS)));
        caches.add(new CaffeineCache(CNTC.CACHE_10SECS, expire(10, TimeUnit.SECONDS)));
        caches.add(new CaffeineCache(CNTC.CACHE_30SECS, expire(30, TimeUnit.SECONDS)));
        caches.add(new CaffeineCache(CNTC.CACHE_01MINS, expire(1, TimeUnit.MINUTES)));
        caches.add(new CaffeineCache(CNTC.CACHE_03MINS, expire(3, TimeUnit.MINUTES)));
        caches.add(new CaffeineCache(CNTC.CACHE_05MINS, expire(5, TimeUnit.MINUTES)));
        caches.add(new CaffeineCache(CNTC.CACHE_1HOURS, expire(1, TimeUnit.HOURS)));
        caches.add(new CaffeineCache(CNTC.CACHE_1DAYS, expire(1, TimeUnit.DAYS)));

        cacheManager.setCaches(caches);
        return cacheManager;
    }

}
