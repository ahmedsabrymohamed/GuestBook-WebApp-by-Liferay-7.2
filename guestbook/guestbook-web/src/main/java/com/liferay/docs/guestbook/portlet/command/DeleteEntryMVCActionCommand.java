package com.liferay.docs.guestbook.portlet.command;

import com.liferay.docs.guestbook.portlet.constants.GuestbookPortletKeys;
import com.liferay.docs.guestbook.service.GuestbookEntryLocalService;
import com.liferay.docs.guestbook.service.GuestbookLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate =true , property= {
		"javax.portlet.name="+GuestbookPortletKeys.GUESTBOOK,
		"mvc.command.name=deleteEntry"
}, service = MVCActionCommand.class)
public class DeleteEntryMVCActionCommand extends BaseMVCActionCommand {
	@Reference
	private GuestbookEntryLocalService _guestbookEntryLocalService;

	@Reference
	private GuestbookLocalService _guestbookLocalService;
	
	private static final Logger logger = LoggerFactory.getLogger(DeleteEntryMVCActionCommand.class);
	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		   long entryId = ParamUtil.getLong(actionRequest, "entryId");
	        long guestbookId = ParamUtil.getLong(actionRequest, "guestbookId");

	        try {

	            actionResponse.getRenderParameters().setValue(
	                "guestbookId", Long.toString(guestbookId));

	            _guestbookEntryLocalService.deleteGuestbookEntry(entryId);
	            SessionMessages.add(actionRequest, "entryDeleted");
	        }

	        catch (Exception e) {
	           logger.error(e.getMessage(),e);
	           SessionErrors.add(actionRequest, e.getClass().getName());
	        }

	}

}
