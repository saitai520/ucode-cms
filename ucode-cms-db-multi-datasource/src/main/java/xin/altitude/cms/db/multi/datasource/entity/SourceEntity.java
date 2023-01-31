package xin.altitude.cms.db.multi.datasource.entity;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.BeanUtils;
import xin.altitude.cms.common.util.EntityUtils;

import java.util.Properties;

/**
 * @author <a href="http://www.altitude.xin" target="_blank">Java知识图谱</a>
 * @author <a href="https://gitee.com/decsa/ucode-cms-vue" target="_blank">UCode CMS</a>
 * @author <a href="https://space.bilibili.com/1936685014" target="_blank">B站视频</a>
 **/
public class SourceEntity {
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 转换成属性对象
     */
    public HikariConfig toHikariConfig() {
        HikariConfig hikariConfig = new HikariConfig();
        BeanUtils.copyProperties(this, hikariConfig);
        hikariConfig.setJdbcUrl(this.getUrl());
        return hikariConfig;
    }
}
