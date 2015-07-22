////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.service;

import java.util.List;
import java.util.Map;

import com.denimgroup.threadfix.CollectionUtils;
import com.denimgroup.threadfix.data.dao.GenericSeverityDao;
import com.denimgroup.threadfix.data.entities.ChannelType;
import com.denimgroup.threadfix.data.entities.GenericSeverity;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.denimgroup.threadfix.data.dao.ChannelSeverityDao;
import com.denimgroup.threadfix.data.dao.ChannelTypeDao;
import com.denimgroup.threadfix.data.entities.ChannelSeverity;

@Service
@Transactional(readOnly = false) // used to be true
public class ChannelSeverityServiceImpl implements ChannelSeverityService {

	private final SanitizedLogger log = new SanitizedLogger(ChannelSeverityServiceImpl.class);

	private ChannelSeverityDao channelSeverityDao;
	private ChannelTypeDao channelTypeDao;
	@Autowired
	private GenericSeverityDao genericSeverityDao;

	@Autowired
	public ChannelSeverityServiceImpl(ChannelTypeDao channelTypeDao,
			ChannelSeverityDao channelSeverityDao) {
		this.channelSeverityDao = channelSeverityDao;
		this.channelTypeDao = channelTypeDao;
	}

	@Override
	public List<ChannelSeverity> loadByChannel(String channelTypeName) {
		return channelSeverityDao.retrieveByChannel(
				channelTypeDao.retrieveByName(channelTypeName));
	}
	
	@Override
	public ChannelSeverity loadById(int id) {
		return channelSeverityDao.retrieveById(id);
	}

	@Override
	public List<Object> loadAllByChannel() {

		List<Object> list = CollectionUtils.list();
		for (ChannelType channelType: channelTypeDao.retrieveAll()) {
			Map<String, Object> map = CollectionUtils.map();
			map.put("channelType", channelType);
			map.put("channelSeverities", channelSeverityDao.retrieveByChannel(channelType));
			list.add(map);
		}

		return list;
	}

	@Override
	public String updateChannelSeverityMappings(List<ChannelSeverity> channelSeverities) {

		for (ChannelSeverity channelSeverity: channelSeverities) {
			GenericSeverity genericSeverity = genericSeverityDao.retrieveById(channelSeverity.getSeverityMap().getGenericSeverity().getId());
			if (genericSeverity != null) {
				ChannelSeverity dbChannelSeverity = channelSeverityDao.retrieveById(channelSeverity.getId());
				dbChannelSeverity.getSeverityMap().setGenericSeverity(genericSeverity);
				channelSeverityDao.saveOrUpdate(dbChannelSeverity);
			}
		}

		return null;
	}

}
