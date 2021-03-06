package com.nature.provider;

import com.nature.base.util.DateUtils;
import com.nature.base.util.SessionUserUtil;
import com.nature.base.util.Utils;
import com.nature.base.vo.UserVo;
import com.nature.component.flow.model.Property;
import com.nature.component.flow.model.Stops;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PropertyMapperProvider {

    /**
	 * 插入list<Property> 注意拼sql的方法必须用map接 Param内容为键值
	 * 
     * @param map (内容： 键为propertyList,值为List<Property>)
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public String addPropertyList(Map map) {
        List<Property> propertyList = (List<Property>) map.get("propertyList");
        StringBuffer sqlStrBuffer = new StringBuffer();
        if (null != propertyList && propertyList.size() > 0) {
            sqlStrBuffer.append("insert into ");
            sqlStrBuffer.append("flow_stops_property ");
            sqlStrBuffer.append("(");
            sqlStrBuffer.append("id,");
            sqlStrBuffer.append("crt_dttm,");
            sqlStrBuffer.append("crt_user,");
            sqlStrBuffer.append("last_update_dttm,");
            sqlStrBuffer.append("last_update_user,");
            sqlStrBuffer.append("version,");
            sqlStrBuffer.append("enable_flag,");
            sqlStrBuffer.append("name,");
            sqlStrBuffer.append("display_name,");
            sqlStrBuffer.append("description,");
            sqlStrBuffer.append("custom_value,");
            sqlStrBuffer.append("allowable_values,");
            sqlStrBuffer.append("property_required,");
            sqlStrBuffer.append("property_sensitive,");
            sqlStrBuffer.append("fk_stops_id,");
            sqlStrBuffer.append("is_select");
            sqlStrBuffer.append(") ");
            sqlStrBuffer.append("values");
            int i = 0;
            for (Property property : propertyList) {
                i++;
                String id = property.getId();
                Date crtDttm = property.getCrtDttm();
                String crtUser = property.getCrtUser();
                Date lastUpdateDttm = property.getLastUpdateDttm();
                String lastUpdateUser = property.getLastUpdateUser();
                Long version = property.getVersion();
                Boolean enableFlag = property.getEnableFlag();
                String name = property.getName();
                String displayName = property.getDisplayName();
                String description = property.getDescription().equals("null") ? null : property.getDescription();
                String customValue = property.getCustomValue();
                String allowableValues = property.getAllowableValues();
                Boolean required = property.getRequired();
                Boolean sensitive = property.getSensitive();
                Stops stops = property.getStops();
                Boolean isSelect = property.getIsSelect();
                // 拼接时位置顺序不能错
                sqlStrBuffer.append("(");
                sqlStrBuffer.append(Utils.addSqlStr(Utils.replaceString(id)) + ",");
                sqlStrBuffer.append(Utils.addSqlStr((crtDttm == null ? "" : DateUtils.dateTimesToStr(crtDttm))) + ",");
                sqlStrBuffer.append(Utils.addSqlStr((Utils.replaceString(crtUser))) + ",");
                sqlStrBuffer.append(Utils.addSqlStr((lastUpdateDttm == null ? "" : DateUtils.dateTimesToStr(lastUpdateDttm))) + ",");
                sqlStrBuffer.append(Utils.addSqlStr((Utils.replaceString(lastUpdateUser))) + ",");
                sqlStrBuffer.append((version == null ? "0" : version) + ",");
                sqlStrBuffer.append((enableFlag == null ? "1" : (enableFlag ? 1 : 0)) + ",");
                sqlStrBuffer.append(Utils.addSqlStr((Utils.replaceString(name))) + ",");
                sqlStrBuffer.append(Utils.addSqlStr((Utils.replaceString(displayName))) + ",");
                sqlStrBuffer.append(Utils.addSqlStr((Utils.replaceString(description))) + ",");
                sqlStrBuffer.append(Utils.addSqlStr((Utils.replaceString(customValue))) + ",");
                sqlStrBuffer.append(Utils.addSqlStr((Utils.replaceString(allowableValues))) + ",");
                sqlStrBuffer.append((required ? 1 : 0) + ",");
                sqlStrBuffer.append((sensitive ? 1 : 0) + ",");
                sqlStrBuffer.append(Utils.addSqlStr((stops == null ? "" : stops.getId()))  + ",");
                sqlStrBuffer.append((isSelect ? 1 : 0));
                if (i != propertyList.size()) {
                    sqlStrBuffer.append("),");
                } else {
                    sqlStrBuffer.append(")");
                }
            }
            sqlStrBuffer.append(";");
        }
        String sqlStr = sqlStrBuffer.toString();
        return sqlStr;
    }

    /**
     * 修改Property
     * @param property
     * @return
     */
    public String updateStopsProperty(Property property) {
        String sqlStr = "";
        if (null != property) {
            String id = property.getId();
            String lastUpdateUser = property.getLastUpdateUser();
            Date lastUpdateDttm = property.getLastUpdateDttm();
            Long version = property.getVersion();
            Boolean enableFlag = property.getEnableFlag();
            String description = property.getDescription();
            String name = property.getName();
            String allowable_values = property.getAllowableValues();
            String custom_value = property.getCustomValue();
            String display_name = property.getDisplayName();
            Boolean required = property.getRequired();
            Boolean sensitive = property.getSensitive();
            Stops stops = property.getStops();
            SQL sql = new SQL();

            // INSERT_INTO括号中为数据库表名
            sql.UPDATE("flow_stops_property");
            // SET中的第一个字符串为数据库中表对应的字段名
            // 除数字类型的字段外其他类型必须加单引号

            //先处理修改必填字段
            if (null == lastUpdateDttm) {
                lastUpdateDttm = new Date();
            }
            if (StringUtils.isBlank(lastUpdateUser)) {
                lastUpdateUser = "-1";
            }
            if (null == version) {
                version = 0L;
            }
            String lastUpdateDttmStr = DateUtils.dateTimesToStr(lastUpdateDttm);
            sql.SET("LAST_UPDATE_DTTM = " + Utils.addSqlStr(lastUpdateDttmStr));
            sql.SET("LAST_UPDATE_USER = " + Utils.addSqlStr(lastUpdateUser));
            sql.SET("VERSION = " + (version + 1));
            if (null != enableFlag) {
                sql.SET("ENABLE_FLAG = " + (enableFlag ? 1 : 0));
            }

            // 处理其他字段
            if (null != enableFlag) {
                sql.SET("ENABLE_FLAG = " + (enableFlag ? 1 : 0));
            }
            if (null != description) {
                sql.SET("description = " + Utils.addSqlStr(Utils.replaceString(description)));
            }
            if (null != name) {
                sql.SET("NAME = " + Utils.addSqlStr(Utils.replaceString(name)));
            }
            if (null != allowable_values) {
                sql.SET("allowable_values = " + Utils.addSqlStr(allowable_values));
            }
            if (null != custom_value) {
                sql.SET("custom_value = " + Utils.addSqlStr(custom_value));
            }
            if (null != display_name) {
                sql.SET("display_name = " + Utils.addSqlStr(display_name));
            }
            if (null != required) {
                int requiredNum = required ? 1 : 0;
                sql.SET("property_required = " + requiredNum);
            }
            if (null != sensitive) {
                int sensitiveNum = sensitive ? 1 : 0;
                sql.SET("property_sensitive = " + sensitiveNum);
            }
            if (null != stops) {
                String stopsId = stops.getId();
                if (StringUtils.isNotBlank(stopsId)) {
                    sql.SET("fk_stops_id = " + Utils.addSqlStr(stopsId));
                }
            }
            sql.WHERE("version = " + version);
            sql.WHERE("id = " + Utils.addSqlStr(id));
            sqlStr = sql.toString();
            if (StringUtils.isBlank(id)) {
                sqlStr = "";
            }
        }
        return sqlStr;
    }

    /**
     * 逻辑删除
     *
     * @param id
     * @return
     */
    public String updateEnableFlagByStopId(String id) {
    	 UserVo user = SessionUserUtil.getCurrentUser();
         String username = (null != user) ? user.getUsername() : "-1";
         String sqlStr = "select 0";
        if (StringUtils.isNotBlank(id)) {
            SQL sql = new SQL();
            sql.UPDATE("flow_stops_property");
            sql.SET("ENABLE_FLAG = 0");
            sql.SET("last_update_user = " + Utils.addSqlStr(username) );
            sql.SET("last_update_dttm = " + Utils.addSqlStr(DateUtils.dateTimesToStr(new Date())) );
            sql.WHERE("ENABLE_FLAG = 1");
            sql.WHERE("ID = " + Utils.addSqlStrAndReplace(id));

            sqlStr = sql.toString();
        }
        return sqlStr;
    }
    
    /**
     * 修改stop属性
     * @param id
     * @return
     */
    public String updatePropertyCustomValue(String content,String id) {
      	 UserVo user = SessionUserUtil.getCurrentUser();
           String username = (null != user) ? user.getUsername() : "-1";
           String sqlStr = "select 0";
          if (StringUtils.isNotBlank(id)) {
              SQL sql = new SQL();
              sql.UPDATE("flow_stops_property");
              sql.SET("custom_value = " + Utils.addSqlStr(content));
              sql.SET("last_update_user = " + Utils.addSqlStr(username) );
              sql.SET("last_update_dttm = " + Utils.addSqlStr(DateUtils.dateTimesToStr(new Date())) );
              sql.SET("version = " + 1 );
              sql.WHERE("ENABLE_FLAG = 1");
              sql.WHERE("id = " + Utils.addSqlStrAndReplace(id));
              sqlStr = sql.toString();
          }
          return sqlStr;
      }

}
