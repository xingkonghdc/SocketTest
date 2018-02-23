package hdc.com.sockettest;

import android.text.TextUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class DeviceInfoUtil {
	private static String mIpAddress = "";

	//--获取ip地址
	public static String getIpAddress() {
		if(TextUtils.isEmpty(mIpAddress)){
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							mIpAddress = inetAddress.getHostAddress().toString();
						}
					}
				}
			} catch (SocketException ex) {
				ex.printStackTrace();
			}
		}
		return mIpAddress;
	}
}
