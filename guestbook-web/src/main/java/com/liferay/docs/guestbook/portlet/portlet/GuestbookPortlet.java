package com.liferay.docs.guestbook.portlet.portlet;

import com.liferay.docs.guestbook.model.Guestbook;
import com.liferay.docs.guestbook.portlet.constants.GuestbookPortletKeys;
import com.liferay.docs.guestbook.service.GuestbookEntryLocalService;
import com.liferay.docs.guestbook.service.GuestbookLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.IOException;
import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author ahmed
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.social",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=Guestbook",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/guestbook/view.jsp",
		"javax.portlet.name=" + GuestbookPortletKeys.GUESTBOOK,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = Portlet.class
)
public class GuestbookPortlet extends MVCPortlet {

	@Reference
	private GuestbookEntryLocalService _guestbookEntryLocalService;

	@Reference
	private GuestbookLocalService _guestbookLocalService;
	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		ServiceContext serviceContext;
		try {
			serviceContext = ServiceContextFactory.getInstance(Guestbook.class.getName(), renderRequest);
		

		long groupId = serviceContext.getScopeGroupId();

		long guestbookId = ParamUtil.getLong(renderRequest, "guestbookId");

		List<Guestbook> guestbooks = _guestbookLocalService.getGuestbooks(groupId);

		if (guestbooks.isEmpty()) {
			Guestbook guestbook = _guestbookLocalService.addGuestbook(serviceContext.getUserId(), "Main",
					serviceContext);

			guestbookId = guestbook.getGuestbookId();
		}

		if (guestbookId == 0) {
			guestbookId = guestbooks.get(0).getGuestbookId();
		}

		renderRequest.setAttribute("guestbookId", guestbookId);
		} catch (PortalException e) {
		
			e.printStackTrace();
		}
		super.render(renderRequest, renderResponse);
	}

	

	
}