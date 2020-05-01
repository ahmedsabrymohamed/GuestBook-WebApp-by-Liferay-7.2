package com.liferay.docs.guestbook.portlet.command;

import com.liferay.docs.guestbook.model.Guestbook;
import com.liferay.docs.guestbook.portlet.constants.GuestbookPortletKeys;
import com.liferay.docs.guestbook.service.GuestbookLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, property = { "javax.portlet.name=" + GuestbookPortletKeys.GUESTBOOK_ADMIN,
"mvc.command.name=updateGuestbook" }, service = MVCActionCommand.class)
public class UpdateMVCActionCommand extends BaseMVCActionCommand {

	private static final Logger logger = LoggerFactory.getLogger(UpdateMVCActionCommand.class);
	@Reference
	private GuestbookLocalService _guestbookLocalService;

	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		ServiceContext serviceContext = ServiceContextFactory.getInstance(Guestbook.class.getName(), actionRequest);

		String name = ParamUtil.getString(actionRequest, "name");
		long guestbookId = ParamUtil.getLong(actionRequest, "guestbookId");

		try {
			_guestbookLocalService.updateGuestbook(serviceContext.getUserId(), guestbookId, name, serviceContext);
			 SessionMessages.add(actionRequest, "guestbookUpdated");
		} catch (PortalException pe) {

			logger.error(pe.getMessage(), pe);
			SessionErrors.add(actionRequest, pe.getClass().getName());
			actionResponse.getRenderParameters().setValue("mvcPath", "/guestbook_admin/edit_guestbook.jsp");
		}

	}

}
