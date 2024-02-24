package co.loyyee;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *  Responding with a text/html file
 * {@link javax.servlet.http.HttpSession| the HttpSession class}
 * */
public class FileHandler implements  Handler{
	private static Logger log = Logger.getLogger("co.loyyee.FileHandler");
	@Override
	public void handle(Request request, Response response) throws IOException {
		// TODO: why substring(1)?
		try {
			FileInputStream file = new FileInputStream(request.getPath().substring(1));
			response.setResponseCode(200, "OK");
			response.addHeader("Content-type", "text/html");
			StringBuffer buf = new StringBuffer();
			// TODO: how to read it file faster? Stream API?
			int c;
			while ((c = file.read()) != -1) {
				buf.append((char) c);  // casting from int (file.read()) to char
			}
			response.addBody(buf.toString());
		} catch (FileNotFoundException e) {
			response.setResponseCode(404, "File Not Found");
		}
	}
}
