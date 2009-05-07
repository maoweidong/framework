package nds.web.welcome;

import nds.control.web.UserWebImpl;
/**
 * ��ʾ�û��״ε�¼ʱ�Ļ�ӭҳ��(dialog)
 * @author yfzhu
 *
 */
public interface Manager {
	/**
	 * Welcome page
	 * @return null if no welcome page needed
	 */
	public String getWelcomePageURL(UserWebImpl user);
}
