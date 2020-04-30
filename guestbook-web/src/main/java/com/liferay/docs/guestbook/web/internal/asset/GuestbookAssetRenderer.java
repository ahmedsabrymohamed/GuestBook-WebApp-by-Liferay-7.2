package com.liferay.docs.guestbook.web.internal.asset;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.docs.guestbook.constants.GuestbookPortletKeys;
import com.liferay.docs.guestbook.model.Guestbook;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuestbookAssetRenderer extends BaseJSPAssetRenderer<Guestbook> {

	private Guestbook _guestbook;
	private final ModelResourcePermission<Guestbook> _guestbookModelResourcePermission;   
	private static final Logger _log = LoggerFactory.getLogger(GuestbookAssetRenderer.class);
	public GuestbookAssetRenderer(Guestbook guestbook, ModelResourcePermission<Guestbook> modelResourcePermission) {

		_guestbook = guestbook;
		_guestbookModelResourcePermission = modelResourcePermission;
	}
	
	

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker) throws PortalException {
		try{
			_guestbookModelResourcePermission.contains(permissionChecker, _guestbook, ActionKeys.UPDATE);
		}catch (Exception e) {
			_log.error("error wihle cheking Permission UPDATE ",e);
		}
		return false;
	}



	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker) throws PortalException {
		try{
			_guestbookModelResourcePermission.contains(permissionChecker, _guestbook, ActionKeys.VIEW);
		}catch (Exception e) {
			_log.error("error wihle cheking Permission VIEW ",e);
		}
		return false;
	}



	@Override
	public Guestbook getAssetObject() {
		return _guestbook;
	}

	@Override
	public long getGroupId() {
		return _guestbook.getGroupId();
	}

	@Override
	public long getUserId() {
		return _guestbook.getUserId();
	}

	@Override
	public String getUserName() {
		return _guestbook.getUserName();
	}

	@Override
	public String getUuid() {
		return getUuid();
	}

	@Override
	public String getClassName() {
		return Guestbook.class.getName();
	}

	@Override
	public long getClassPK() {
		return _guestbook.getGuestbookId();
	}

	@Override
	public String getSummary(PortletRequest portletRequest, PortletResponse portletResponse) {
		return "NAME: "+_guestbook.getName();
	}

	@Override
	public String getTitle(Locale locale) {
		return _guestbook.getName();
	}

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest, String template) {
		   if (template.equals(TEMPLATE_FULL_CONTENT)) {
			   httpServletRequest.setAttribute("gb_guestbook", _guestbook);

			      return "/asset/guestbook/" + template + ".jsp";
			    } else {
			      return null;
			    }
	}



	@Override
	public boolean include(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			String template) throws Exception {
		httpServletRequest.setAttribute("GUESTBOOK", _guestbook);
		httpServletRequest.setAttribute("HtmlUtil", HtmlUtil.getHtml());
		httpServletRequest.setAttribute("StringUtil", new StringUtil());
		return super.include(httpServletRequest, httpServletResponse, template);
	}



	@Override
	public PortletURL getURLEdit(LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse) throws Exception {
		PortletURL portletURL = liferayPortletResponse.createLiferayPortletURL(
			    getControlPanelPlid(liferayPortletRequest), GuestbookPortletKeys.GUESTBOOK,
			    PortletRequest.RENDER_PHASE);
			portletURL.getRenderParameters().setValue("mvcPath", "/guestbook/edit_guestbook.jsp");
			portletURL.getRenderParameters().setValue("guestbookId", String.valueOf(_guestbook.getGuestbookId()));
			portletURL.getRenderParameters().setValue("showback", Boolean.FALSE.toString());

			return portletURL;
	}
	
	@Override
	public String getURLViewInContext(LiferayPortletRequest liferayPortletRequest,
	  LiferayPortletResponse liferayPortletResponse, String noSuchEntryRedirect) throws Exception {
	    try {
	      long plid = PortalUtil.getPlidFromPortletId(_guestbook.getGroupId(),
	          GuestbookPortletKeys.GUESTBOOK);

	      PortletURL portletURL;
	      if (plid == LayoutConstants.DEFAULT_PLID) {
	        portletURL = liferayPortletResponse.createLiferayPortletURL(getControlPanelPlid(liferayPortletRequest),
	            GuestbookPortletKeys.GUESTBOOK, PortletRequest.RENDER_PHASE);
	      } else {
	        portletURL = PortletURLFactoryUtil.create(liferayPortletRequest,
	            GuestbookPortletKeys.GUESTBOOK, plid, PortletRequest.RENDER_PHASE);
	      }

	      portletURL.getRenderParameters().setValue("mvcPath", "/guestbook/view.jsp");
	      portletURL.getRenderParameters().setValue("guestbookId", String.valueOf(_guestbook.getGuestbookId()));

	      String currentUrl = PortalUtil.getCurrentURL(liferayPortletRequest);

	      portletURL.getRenderParameters().setValue("redirect", currentUrl);

	      return portletURL.toString();

	    } catch (PortalException e) {

	        _log.error(e.getMessage(),e);

	    } catch (SystemException e) {

	    	_log.error(e.getMessage(),e);

	    }

	    return noSuchEntryRedirect;
	}
	
	
	@Override
	public String getURLView(LiferayPortletResponse liferayPortletResponse, 
	WindowState windowState) throws Exception {

	    return super.getURLView(liferayPortletResponse, windowState);
	}
	

}
