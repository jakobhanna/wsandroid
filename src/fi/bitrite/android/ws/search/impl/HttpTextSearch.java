package fi.bitrite.android.ws.search.impl;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import fi.bitrite.android.ws.auth.http.HttpAuthenticationService;
import fi.bitrite.android.ws.auth.http.HttpSessionContainer;
import fi.bitrite.android.ws.model.HostBriefInfo;
import fi.bitrite.android.ws.search.Search;
import fi.bitrite.android.ws.util.http.HttpException;
import fi.bitrite.android.ws.util.http.HttpUtils;

public class HttpTextSearch implements Search {

	private static final String WARMSHOWERS_LIST_SEARCH_URL = "http://www.warmshowers.org/search/wsuser/";

	private HttpAuthenticationService authenticationService;

	private HttpSessionContainer sessionContainer;

	private String text;

	private boolean authenticationPerformed;

	public HttpTextSearch(String text, HttpAuthenticationService authenticationService,
			HttpSessionContainer sessionContainer) {
		this.text = text;
		this.authenticationService = authenticationService;
		this.sessionContainer = sessionContainer;
	}

	/*
	 * Scrapes the standard WarmShowers list search page.
	 */
	public List<HostBriefInfo> doSearch() {
		authenticationPerformed = false;
		String html = getSearchResultHtml();
		HttpTextSearchResultScraper scraper = new HttpTextSearchResultScraper(html);
		List<HostBriefInfo> hosts = scraper.getHosts();
		return hosts;
	}

	protected String getSearchResultHtml() {
		HttpClient client = HttpUtils.getDefaultClient();
		String html = null;
		int responseCode;

		try {
			String searchUrl = HttpUtils.encodeUrl(WARMSHOWERS_LIST_SEARCH_URL + text);
			HttpGet get = new HttpGet(searchUrl);
			HttpContext context = sessionContainer.getSessionContext();

			HttpResponse response = client.execute(get, context);
			HttpEntity entity = response.getEntity();
			responseCode = response.getStatusLine().getStatusCode();

			html = EntityUtils.toString(entity, "UTF-8");
		}

		catch (Exception e) {
			throw new HttpException(e);
		}

		finally {
			client.getConnectionManager().shutdown();
		}

		if (responseCode == HttpStatus.SC_FORBIDDEN) {
			if (!authenticationPerformed) {
				authenticationService.authenticate();
				authenticationPerformed = true;
				html = getSearchResultHtml();
			} else {
				throw new HttpException("Couldn't authenticate user");
			}
		}

		return html;
	}

}