/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.docs.guestbook.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetLinkConstants;
import com.liferay.docs.guestbook.exception.GuestbookEntryEmailException;
import com.liferay.docs.guestbook.exception.GuestbookEntryMessageException;
import com.liferay.docs.guestbook.exception.GuestbookEntryNameException;
import com.liferay.docs.guestbook.model.GuestbookEntry;
import com.liferay.docs.guestbook.service.base.GuestbookEntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The implementation of the guestbook entry local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * <code>com.liferay.docs.guestbook.service.GuestbookEntryLocalService</code>
 * interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author liferay
 * @see GuestbookEntryLocalServiceBaseImpl
 */
@Component(property = "model.class.name=com.liferay.docs.guestbook.model.GuestbookEntry", service = AopService.class)
public class GuestbookEntryLocalServiceImpl extends GuestbookEntryLocalServiceBaseImpl {

	private static final Logger logger = LoggerFactory.getLogger(GuestbookEntryLocalServiceImpl.class);

	@Indexable(type = IndexableType.REINDEX)
	public GuestbookEntry addGuestbookEntry(long userId, long guestbookId, String name, String email, String message,
			ServiceContext serviceContext) throws PortalException {

		long groupId = serviceContext.getScopeGroupId();

		User user = userLocalService.getUserById(userId);

		Date now = new Date();

		validate(name, email, message);

		long entryId = counterLocalService.increment();

		GuestbookEntry entry = guestbookEntryPersistence.create(entryId);

		entry.setUuid(serviceContext.getUuid());
		entry.setUserId(userId);
		entry.setGroupId(groupId);
		entry.setCompanyId(user.getCompanyId());
		entry.setUserName(user.getFullName());
		entry.setCreateDate(serviceContext.getCreateDate(now));
		entry.setModifiedDate(serviceContext.getModifiedDate(now));
		entry.setExpandoBridgeAttributes(serviceContext);
		entry.setGuestbookId(guestbookId);
		entry.setName(name);
		entry.setEmail(email);
		entry.setMessage(message);

		guestbookEntryPersistence.update(entry);

		resourceLocalService.addResources(user.getCompanyId(), groupId, userId, GuestbookEntry.class.getName(), entryId,
				false, true, true);

		AssetEntry assetEntry = assetEntryLocalService.updateEntry(userId, groupId, entry.getCreateDate(),
				entry.getModifiedDate(), GuestbookEntry.class.getName(), entryId, entry.getUuid(), 0,
				serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(), true, true, null, null, null,
				null, ContentTypes.TEXT_HTML, entry.getMessage(), null, null, null, null, 0, 0, null);

		assetLinkLocalService.updateLinks(userId, assetEntry.getEntryId(), serviceContext.getAssetLinkEntryIds(),
				AssetLinkConstants.TYPE_RELATED);

		return entry;
	}

	@Indexable(type = IndexableType.REINDEX)
	public GuestbookEntry updateGuestbookEntry(long userId, long guestbookId, long entryId, String name, String email,
			String message, ServiceContext serviceContext) throws PortalException, SystemException {

		Date now = new Date();

		validate(name, email, message);

		GuestbookEntry entry = guestbookEntryPersistence.findByPrimaryKey(entryId);

		User user = userLocalService.getUserById(userId);

		entry.setUserId(userId);
		entry.setUserName(user.getFullName());
		entry.setModifiedDate(serviceContext.getModifiedDate(now));
		entry.setName(name);
		entry.setEmail(email);
		entry.setMessage(message);
		entry.setExpandoBridgeAttributes(serviceContext);

		guestbookEntryPersistence.update(entry);

		AssetEntry assetEntry = assetEntryLocalService.updateEntry(userId, serviceContext.getScopeGroupId(),
				entry.getCreateDate(), entry.getModifiedDate(), GuestbookEntry.class.getName(), entryId,
				entry.getUuid(), 0, serviceContext.getAssetCategoryIds(), serviceContext.getAssetTagNames(), true, true,
				entry.getCreateDate(), null, null, null, ContentTypes.TEXT_HTML, entry.getMessage(), null, null, null,
				null, 0, 0, serviceContext.getAssetPriority());

		assetLinkLocalService.updateLinks(userId, assetEntry.getEntryId(), serviceContext.getAssetLinkEntryIds(),
				AssetLinkConstants.TYPE_RELATED);

		return entry;
	}

	@Indexable(type = IndexableType.DELETE)
	public GuestbookEntry deleteGuestbookEntry(GuestbookEntry entry) {

		guestbookEntryPersistence.remove(entry);
		try {
			resourceLocalService.deleteResource(entry.getCompanyId(), GuestbookEntry.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL, entry.getEntryId());
		} catch (PortalException e) {

			logger.error(e.getMessage(), e);
		}

		AssetEntry assetEntry = assetEntryLocalService.fetchEntry(GuestbookEntry.class.getName(), entry.getEntryId());

		assetLinkLocalService.deleteLinks(assetEntry.getEntryId());

		try {
			assetEntryLocalService.deleteEntry(assetEntry);
		} catch (PortalException e) {
			logger.error("Error while deleting assetEntry",e);
		}
		return entry;
	}

	public GuestbookEntry deleteGuestbookEntry(long entryId) throws PortalException {

		GuestbookEntry entry = guestbookEntryPersistence.findByPrimaryKey(entryId);

		return deleteGuestbookEntry(entry);
	}

	public List<GuestbookEntry> getGuestbookEntries(long groupId, long guestbookId) {
		return guestbookEntryPersistence.findByG_G(groupId, guestbookId);
	}

	public List<GuestbookEntry> getGuestbookEntries(long groupId, long guestbookId, int start, int end)
			throws SystemException {

		return guestbookEntryPersistence.findByG_G(groupId, guestbookId, start, end);
	}

	public List<GuestbookEntry> getGuestbookEntries(long groupId, long guestbookId, int start, int end,
			OrderByComparator<GuestbookEntry> obc) {

		return guestbookEntryPersistence.findByG_G(groupId, guestbookId, start, end, obc);
	}

	public GuestbookEntry getGuestbookEntry(long entryId) throws PortalException {
		return guestbookEntryPersistence.findByPrimaryKey(entryId);
	}

	public int getGuestbookEntriesCount(long groupId, long guestbookId) {
		return guestbookEntryPersistence.countByG_G(groupId, guestbookId);
	}

	protected void validate(String name, String email, String entry) throws PortalException {

		if (Validator.isNull(name)) {
			throw new GuestbookEntryNameException();
		}

		if (!Validator.isEmailAddress(email)) {
			throw new GuestbookEntryEmailException();
		}

		if (Validator.isNull(entry)) {
			throw new GuestbookEntryMessageException();
		}
	}
}