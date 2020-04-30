package com.liferay.docs.guestbook.search;

import com.liferay.docs.guestbook.model.Guestbook;
import com.liferay.portal.kernel.exception.PortalException;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, property = "indexer.class.name=com.liferay.docs.guestbook.model.Guestbook", service = ModelDocumentContributor.class)
public class GuestbookModelDocumentContributor implements ModelDocumentContributor<Guestbook> {

	private static final Logger _log = LoggerFactory.getLogger(GuestbookModelDocumentContributor.class);

	@Override
	public void contribute(Document document, Guestbook guestbook) {
		try {
			document.addDate(Field.MODIFIED_DATE, guestbook.getModifiedDate());

			Locale defaultLocale = PortalUtil.getSiteDefaultLocale(guestbook.getGroupId());

			String localizedTitle = LocalizationUtil.getLocalizedName(Field.TITLE, defaultLocale.toString());

			document.addText(localizedTitle, guestbook.getName());
		} catch (PortalException pe) {

			_log.warn("Unable to index guestbook " + guestbook.getGuestbookId(), pe);

		}
	}

}