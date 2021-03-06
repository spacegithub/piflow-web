package com.nature.component.template.service.impl;

import com.nature.base.util.LoggerUtil;
import com.nature.base.util.SessionUserUtil;
import com.nature.base.util.Utils;
import com.nature.base.vo.UserVo;
import com.nature.component.template.model.FlowTemplateModel;
import com.nature.component.template.model.PropertyTemplateModel;
import com.nature.component.template.model.StopTemplateModel;
import com.nature.component.template.service.IFlowAndStopsTemplateVoService;
import com.nature.component.flow.model.Flow;
import com.nature.component.flow.model.Property;
import com.nature.component.flow.model.Stops;
import com.nature.component.flow.model.Template;
import com.nature.mapper.FlowMapper;
import com.nature.mapper.PropertyMapper;
import com.nature.mapper.StopsMapper;
import com.nature.mapper.template.FlowAndStopsTemplateVoMapper;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FlowAndStopsTemplateVoServiceImpl implements IFlowAndStopsTemplateVoService {

    Logger logger = LoggerUtil.getLogger();
    
    
    @Autowired
    private FlowAndStopsTemplateVoMapper flowAndStopsTemplateVoMapper;
    
    @Autowired
    private StopsMapper stopsMapper;
    
    @Autowired
    private PropertyMapper propertyMapper;

	@Override
	public int addStops(StopTemplateModel stops) {
		return flowAndStopsTemplateVoMapper.addStops(stops);
	}

	@Override
	public int addFlow(FlowTemplateModel flow) {
		return flowAndStopsTemplateVoMapper.addFlow(flow);
	}

	@Override
	public int addPropertyList(List<PropertyTemplateModel> propertyList) {
		return flowAndStopsTemplateVoMapper.addPropertyList(propertyList);
	}

	@Override
	public int deleteStopTemByTemplateId(String templateId) {
		return flowAndStopsTemplateVoMapper.deleteStopTemByTemplateId(templateId);
	}

	@Override
	public int deleteStopPropertyTemByStopId(String stopId) {
		return flowAndStopsTemplateVoMapper.deleteStopPropertyTemByStopId(stopId);
	}

	@Override
	public List<StopTemplateModel> getStopsListByTemPlateId(String templateId) {
		return flowAndStopsTemplateVoMapper.getStopsListByTemPlateId(templateId);
	}

	@Override
	public List<PropertyTemplateModel> getPropertyListByStopsId(String stopsId) {
		return flowAndStopsTemplateVoMapper.getPropertyListByStopsId(stopsId);
	}

	@Override
	@Transient
	public void addTemplateStopsToFlow(Template template,Flow flow,int maxPageId) {
		UserVo user = SessionUserUtil.getCurrentUser();
	    String username = (null != user) ? user.getUsername() : "-1";
		int addPropertyList = 0;
		List<Property> list = new ArrayList<Property>();
		// 获取stop信息
		List<StopTemplateModel> stopsList = template.getStopsList();
		// 开始遍历保存stop和属性信息
		 if (null != stopsList && stopsList.size() > 0) {
			for (StopTemplateModel stopsVo : stopsList) {
				Stops stop = new Stops();
				BeanUtils.copyProperties(stopsVo, stop);
				//pageId最大值开始增加
				stop.setPageId((Integer.parseInt(stopsVo.getPageId())+maxPageId)+"");
				stop.setId(Utils.getUUID32());
				stop.setCrtUser(username);
				stop.setLastUpdateUser(username);
				stop.setFlow(flow);
				stop.setCheckpoint(stopsVo.getIsCheckpoint());
				int addStops = stopsMapper.addStops(stop);
				logger.info("addStops影响行数"+addStops);
				if (addStops > 0) {
					if (null != stopsVo.getProperties()) {
						List<PropertyTemplateModel> properties = stopsVo.getProperties();
						if (null != properties && properties.size() > 0) {
							for (PropertyTemplateModel property : properties) {
								Property propertyModel = new Property();
								BeanUtils.copyProperties(property, propertyModel);
								propertyModel.setStops(stop);
								propertyModel.setId(Utils.getUUID32());
								propertyModel.setCrtUser(username);
								propertyModel.setLastUpdateUser(username);
								list.add(propertyModel);
							}
						}
					}
				}else {
	                logger.error("新建保存失败propertyModel", new Exception("新建保存失败propertyModel"));
	            }
				
			
			}
			if (null != list && list.size() > 0) {
				System.out.println(list.size()+"属性");
				addPropertyList = propertyMapper.addPropertyList(list);
				logger.info("addStops影响行数"+addPropertyList);
			}
		}
	}

	@Override
	@Transient
	public void addStopsList(List<Stops> stopsList,Template template) {
		 UserVo user = SessionUserUtil.getCurrentUser();
         String username = (null != user) ? user.getUsername() : "-1";
		List<PropertyTemplateModel> list = new ArrayList<PropertyTemplateModel>();
		//保存stop，属性信息
		 if (null != stopsList && stopsList.size() > 0) {
			for (Stops stops : stopsList) {
				StopTemplateModel stopTemplate = new StopTemplateModel();
				BeanUtils.copyProperties(stops, stopTemplate);
				stopTemplate.setTemplate(template);
				stopTemplate.setId(Utils.getUUID32());
				stopTemplate.setCrtDttm(new Date());
				stopTemplate.setCrtUser(username);
				stopTemplate.setIsCheckpoint(stops.getCheckpoint());
				flowAndStopsTemplateVoMapper.addStops(stopTemplate);
				if (null != stops.getProperties()) {
					List<Property> properties = stops.getProperties();
					if (null != properties && properties.size() > 0) {
						for (Property property : properties) {
							PropertyTemplateModel propertyVo = new PropertyTemplateModel();
							BeanUtils.copyProperties(property, propertyVo);
							propertyVo.setStopsVo(stopTemplate);
							propertyVo.setId(Utils.getUUID32());
							propertyVo.setCrtDttm(new Date());
							propertyVo.setCrtUser(username);
							list.add(propertyVo);
						}
					}
				}
			}
			if (null != list && list.size() > 0) {
				flowAndStopsTemplateVoMapper.addPropertyList(list);
			}
		}
	}
}
