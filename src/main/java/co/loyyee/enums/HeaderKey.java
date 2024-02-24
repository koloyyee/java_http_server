package co.loyyee.enums;

public enum HeaderKey {
	ContentType("Content-Type"),
	ContentLength("Content-Length"),
	Connection("Connection"),
	SetCookie("Set-Cookie");

	public final String value;
	HeaderKey (String value) {
		this.value = value;
	}
}
