package fi.bitrite.android.ws.persistence;

import java.util.List;

import fi.bitrite.android.ws.model.Host;
import fi.bitrite.android.ws.model.HostBriefInfo;

public interface StarredHostDao {

	public void insert(int id, String name, Host host);

	public Host get(int id);

	public List<HostBriefInfo> getAllBrief();

	public void delete(int id);

	public boolean isHostStarred(int id, String name);

	public void open();
	
	public void close();
}
