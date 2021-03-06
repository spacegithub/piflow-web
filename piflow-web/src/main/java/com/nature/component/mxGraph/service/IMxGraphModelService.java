package com.nature.component.mxGraph.service;

import com.nature.base.vo.StatefulRtnBase;
import com.nature.component.mxGraph.model.MxGraphModel;
public interface IMxGraphModelService {

	public StatefulRtnBase addMxGraphModel(MxGraphModel mxGraphModel);
	
	public int deleteMxGraphModelById(String id);

}
