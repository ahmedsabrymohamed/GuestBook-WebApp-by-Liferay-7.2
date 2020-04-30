package com.liferay.docs.guestbook.portlet.command;

import com.liferay.docs.guestbook.model.GuestbookEntry;
import com.liferay.docs.guestbook.portlet.constants.GuestbookPortletKeys;
import com.liferay.docs.guestbook.service.GuestbookEntryLocalService;
import com.liferay.docs.guestbook.service.GuestbookLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
	immediate = true,
	property = {
			"javax.portlet.name="+GuestbookPortletKeys.GUESTBOOK,
			"mvc.command.name=addEntry"
	},
	service = MVCActionCommand.class
)
public class AddEntryMVCActionCommand extends BaseMVCActionCommand {

	@Reference
	private GuestbookEntryLocalService _guestbookEntryLocalService;

	@Reference
	private GuestbookLocalService _guestbookLocalService;

	private static final Logger logger = LoggerFactory.getLogger(AddEntryMVCActionCommand.class);
	
	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		 ServiceContext serviceContext = ServiceContextFactory.getInstance(
		            GuestbookEntry.class.getName(), actionRequest);

		        String userName = ParamUtil.getString(actionRequest, "name");
		        String email = ParamUtil.getString(actionRequest, "email");
		        String message = ParamUtil.getString(actionRequest, "message");
		        long guestbookId = ParamUtil.getLong(actionRequest, "guestbookId");
		        long entryId = ParamUtil.getLong(actionRequest, "entryId");

		    if (entryId > 0) {

		        try {

		            _guestbookEntryLocalService.updateGuestbookEntry(
		                serviceContext.getUserId(), guestbookId, entryId, userName,
		                email, message, serviceContext);

		            actionResponse.getRenderParameters().setValue(
		                "guestbookId", Long.toString(guestbookId));
		            

		        }
		        catch (Exception e) {
		        	logger.error(e.getMessage(),e);

		            PortalUtil.copyRequestParameters(actionRequest, actionResponse);

		            actionResponse.getRenderParameters().setValue(
		                "mvcPath", "/guestbook/edit_entry.jsp");
		        }

		    }
		    else {

		        try {
		            _guestbookEntryLocalService.addGuestbookEntry(
		                serviceContext.getUserId(), guestbookId, userName, email,
		                message, serviceContext);

		            actionResponse.getRenderParameters().setValue(
		                "guestbookId", Long.toString(guestbookId));
		            SessionMessages.add(actionRequest, "entryAdded");

		        }
		        catch (Exception e) {
		        	logger.error(e.getMessage(),e);
		        	SessionErrors.add(actionRequest, e.getClass().getName());
		            PortalUtil.copyRequestParameters(actionRequest, actionResponse);

		            actionResponse.getRenderParameters().setValue(
		                "mvcPath", "/guestbook/edit_entry.jsp");
		        }
		    }
		
	}

}
