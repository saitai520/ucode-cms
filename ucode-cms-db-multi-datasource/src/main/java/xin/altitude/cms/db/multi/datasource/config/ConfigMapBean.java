package xin.altitude.cms.db.multi.datasource.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import xin.altitude.cms.db.multi.datasource.entity.SourceEntity;

import java.util.Map;

@ConfigurationProperties(prefix = "spring.ucode")
public class ConfigMapBean {

    //参数名称要对上yml的名称
    public Map<String, SourceEntity> more;

    public Map<String, SourceEntity> getMore() {
        return more;
    }

    public void setMore(Map<String, SourceEntity> more) {
        this.more = more;
    }
}
