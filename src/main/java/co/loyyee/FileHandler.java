package co.loyyee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static co.loyyee.enums.HeaderKey.ContentType;

/**
 * <h3>Step 5</h3>
 *  Responding with a text/html file
 * {@link javax.servlet.http.HttpSession| the HttpSession class}
 * */
public class FileHandler implements Handler {

	public void handle(Request request, Response response) throws IOException {
			try (BufferedReader in = new BufferedReader(new FileReader("src/main/resources/static/" + request.getPath().substring(1)))) {

				/** Use StringBuilder for fast parsing html file.*/
				StringBuilder stringBuilder = new StringBuilder();
					String str;
					while( (str = in.readLine()) != null) {
						stringBuilder.append(str);
					}
				response.setResponseCode(200, "OK");
					switch(request.getPath()) {
						case "html" -> response.addHeader(ContentType.value, "text/html");
						case "css" -> response.addHeader(ContentType.value, "text/css");
					}

				response.addBody(stringBuilder.toString());
			} catch (IOException e) {

				StringBuilder stringBuilder = new StringBuilder();
				BufferedReader in = new BufferedReader(new FileReader("src/main/resources/static/404.html"));
				String str;
				while( (str = in.readLine()) != null) {
					stringBuilder.append(str);
				}
				response.setResponseCode(404, "Not Found");
				response.addHeader(ContentType.value, "text/html");
				response.addBody(stringBuilder.toString());
			}
		}
	}
