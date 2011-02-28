package com.util.GetSchedule;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * A Class to generate Cookies when login.
 */
public class CookiesManager {
	private String url;
	private String logUrl;
	private String userName;
	private String password;
    public DefaultHttpClient defaultHttpClient;
	public CookiesManager(String url,String logUrl,String userName,String password)
	{
		this.url = url;
		this.logUrl = logUrl;
		this.userName = userName;
		this.password = password;
		this.defaultHttpClient = new DefaultHttpClient();
	}
	public boolean GenerateCookies() throws Exception {
        //检查Cookies是否已经存在

        HttpGet httpget = new HttpGet(url);

        HttpResponse httpResponse = defaultHttpClient.execute(httpget);
        List<Cookie> cookies = defaultHttpClient.getCookieStore().getCookies();
        boolean isLogin = CheckCookies(cookies);
        //如果不存在，则POST密码等
        if(isLogin){
        	return true;
        }
        HttpPost httpost = new HttpPost(logUrl);

        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("user", userName));
        nvps.add(new BasicNameValuePair("password",password));
        nvps.add(new BasicNameValuePair("UserStyle", "student"));
        
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        ReleaseConnection(httpResponse);
        httpResponse = defaultHttpClient.execute(httpost);
        ReleaseConnection(httpResponse);
        if(CheckCookies(cookies))
		return true;
        return false;
    }
	private boolean CheckCookies(List<Cookie> cookies) {
		// TODO Auto-generated method stub
		for (Cookie s : cookies){
			if(s.getName().equals("xsxm") || s.getName().equals("xjztmc"))
				return true;
		}
		return false;
	}
	public static void ReleaseConnection(HttpResponse httpResponse) throws IOException{
		//release Resourse
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                // do something useful
            } finally {
                instream.close();
            }
        }
	}
	
}