
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.commons.httpclient.auth.NTLMScheme;
import org.apache.commons.httpclient.auth.RFC2617Scheme;
import org.apache.commons.httpclient.methods.GetMethod;

public class InteractiveAuthentication {

	public InteractiveAuthentication() {
		super();
	}

	public String getTicket(String url) throws IOException {

		HttpClient client = new HttpClient();
		client.getParams().setParameter(CredentialsProvider.PROVIDER, new ConsoleAuthPrompter());
		GetMethod httpget = new GetMethod(url);
		httpget.setDoAuthentication(true);

		// aritz
		String response = null;
		String ticketURLResponse = null;

		try {
			// execute the GET
			int status = client.executeMethod(httpget);

			// aritz
			if (status != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + httpget.getStatusLine());
			}

			// print the status and response
			// System.out.println(httpget.getStatusLine().toString());
			// System.out.println(httpget.getResponseBodyAsString());

			response = new String(httpget.getResponseBodyAsString());
			System.out.println("response = " + response);

			int startindex = response.indexOf("") + 8;
			int endindex = response.indexOf("");

			ticketURLResponse = response.substring(startindex, endindex);
			System.out.println("ticket = " + ticketURLResponse);

		} finally {

			// release any connection resources used by the method
			httpget.releaseConnection();
		}
		return ticketURLResponse;
	}

	public class ConsoleAuthPrompter implements CredentialsProvider {

		private BufferedReader in = null;

		public ConsoleAuthPrompter() {
			super();
			this.in = new BufferedReader(new InputStreamReader(System.in));
		}

		private String readConsole() throws IOException {
			return this.in.readLine();
		}

		public Credentials getCredentials(final AuthScheme authscheme, final String host, int port, boolean proxy)
				throws CredentialsNotAvailableException {
			if (authscheme == null) {
				return null;
			}
			try {
				if (authscheme instanceof NTLMScheme) {
					System.out.println(host + ":" + port + " requires Windows authentication");
					System.out.print("Enter domain: ");
					String domain = readConsole();
					System.out.print("Enter username: ");
					String user = readConsole();
					System.out.print("Enter password: ");
					String password = readConsole();
					return new NTCredentials(user, password, host, domain);
				} else if (authscheme instanceof RFC2617Scheme) {
					System.out.println(host + ":" + port + " requires authentication with the realm '"
							+ authscheme.getRealm() + "'");
					System.out.print("Enter username: ");
					String user = readConsole();
					System.out.print("Enter password: ");
					String password = readConsole();
					return new UsernamePasswordCredentials(user, password);
				} else {
					throw new CredentialsNotAvailableException(
							"Unsupported authentication scheme: " + authscheme.getSchemeName());
				}
			} catch (IOException e) {
				throw new CredentialsNotAvailableException(e.getMessage(), e);
			}
		}
	}
}