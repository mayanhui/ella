package com.adintellig.ella.service;

import java.util.ArrayList;
import java.util.List;

import com.adintellig.ella.dao.RegionDaoImpl;
import com.adintellig.ella.dao.RequestCountDaoImpl;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.TableRequestCount;

public class TableService {

	RequestCountDaoImpl rcDao = new RequestCountDaoImpl();
	RegionDaoImpl rdao = new RegionDaoImpl();

	public List<RequestCount> list() throws Exception {
		List<RequestCount> tables = rcDao.list();
		List<RequestCount> tempList = new ArrayList<RequestCount>();
		for (RequestCount rc : tables) {
			if (rc instanceof TableRequestCount) {
				TableRequestCount trc = (TableRequestCount) rc;
				String tableName = trc.getTableName();
				int regionCount = rdao.getRegionNumberByTableName(tableName);
				trc.setRegionCount(regionCount);
				tempList.add(trc);
			}
		}

		return tempList;
	}
}
