package co.loyyee;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @link <a href="https://commandlinefanatic.com/cgi-bin/showarticle.cgi?article=art078">Joshua's explanation </a>
 *
 * <p>These last three are security options to <strong>guard against XSS and CSRF attacks.</strong>
 * <br>
 * <p><em>Secure and HttpOnly </em>indicate that the cookie
 * should only be returned when the connection
 * is an HTTPS connection or when the request is made by the browser</p>
 * <br>
 * <p><em>SameSite</em> can be set to Strict or Lax to indicate
 * whether or not the cookie should only be sent if the request originated from the cookie's own site
 *</p>
 *
 * */
public class Cookie {
	final private String name;
	final private String value;
	private Date expires;
	private Integer maxAge;
	private String domain;
	private String path;
	private boolean secure;
	private boolean httpOnly;
	private String sameSite;

	public Cookie(String name, String value, Date expires, Integer maxAge, String domain, String path, boolean secure, boolean httpOnly, String sameSite) {
		this.name = name;
		this.value = value;
		this.expires = expires;
		this.maxAge = maxAge;
		this.domain = domain;
		this.path = path;
		this.secure = secure;
		this.httpOnly = httpOnly;
		this.sameSite = sameSite;
	}
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(name + "=" + value) ;
		if(expires != null ) {
			SimpleDateFormat fmt = new SimpleDateFormat("EEE, dd MM yyyy HH:mm:ss");
			stringBuilder.append("; Expires=" + fmt.format(expires));
		}
		if(maxAge != null) {
			stringBuilder.append("; Max-Age=" + maxAge);
		}
		if(domain != null) {
			stringBuilder.append("; Domain=" + domain);
		}
		if(path!= null) {
			stringBuilder.append("; Path=" + path);
		}
		if(secure) {
			stringBuilder.append("; Secure" );
		}
		if(httpOnly) {
			stringBuilder.append("; HttpOnly" );
		}
		if(sameSite != null ) {
			stringBuilder.append("; SameSite=" + sameSite );
		}
		return stringBuilder.toString();
	}
}
