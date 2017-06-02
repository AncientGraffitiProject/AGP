package edu.wlu.graffiti.filters;

import java.io.IOException;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.URLEncoder;
import com.oreilly.servlet.multipart.*;
import com.oreilly.servlet.multipart.Part;

import org.apache.log4j.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Servlet Filter implementation class LogFilter
 */
@WebFilter("/LogFilter")
public class LogFilter implements Filter {

	// the name of the log file
	private String fileName;
	// the complete name of the log file
	private String logFileName;
	// the maximum size of an uploaded file as a string
	private String stringFileSize;
	// the maximum size of an uploaded file as a integer
	// default is 100M
	private int intFileSize = 1024 * 1024 * 100;
	// the directory to save the log file to
	private String saveDir;
	// the logger
	private Logger log;
	// the format of the date and time
	private SimpleDateFormat sdf = new SimpleDateFormat();
	// the last time that the log file was rolled over
	private long lastRollOver;
	// the roll-over interval
	private long interval = 7L * 24L * 3600L * 1000L;

	private static String ENCODING = "UTF-8";

	/**
	 * Default constructor.
	 */
	public LogFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {

		// get the log file name from the web.xml file
		if ((fileName = filterConfig.getInitParameter("LogFile")) == null)
			fileName = "t3st-log";

		// get the temporary directory to store the uploaded files from the
		// web.xml file
		if ((saveDir = filterConfig.getInitParameter("SaveDir")) == null)
			saveDir = "/tmp/";

		// set the time of the last roll-over
		long lastRollOver = System.currentTimeMillis();

		// convert the time into a string
		sdf.applyLocalizedPattern("yyyyMMddHHmm");
		String now = sdf.format(new Date(lastRollOver));

		// create the name of the file used to store the logging info
		logFileName = new String(saveDir + "/" + fileName + "." + now);

		// get the maximum file size from the web.xml file
		// and convert the size in the web.xml file to an integer
		if ((stringFileSize = filterConfig
				.getInitParameter("UploadFileLogLimit")) != null)
			intFileSize = 1024 * 1024 * Integer.parseInt(stringFileSize);

		// create the log file
		createLogFile();

	}// end method init

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		fileName = null;
		logFileName = null;
		log = null;
		stringFileSize = null;
		saveDir = null;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (request.getCharacterEncoding() == null) {
			request.setCharacterEncoding(ENCODING);
		}

		// get the date and time that the request was received
		long currentTime = System.currentTimeMillis();

		// determine is roll over is needed
		if (currentTime >= lastRollOver + interval) {

			// convert the time into a string
			sdf.applyLocalizedPattern("yyyyMMddHHmm");
			String now = sdf.format(new Date(currentTime));

			// create the name of the file used to store the logging info
			logFileName = saveDir + "/" + fileName + "." + now;

			// set the time the file was last rolled over
			lastRollOver = currentTime;

			// create the log file
			createLogFile();

		}// end if

		// the information to be logged
		StringBuffer logInfo = new StringBuffer();

		// get the IP address of the request
		String ipAddress = request.getRemoteAddr();

		// add the IP address to the buffer
		logInfo.append(ipAddress + " ");

		// convert the time the request object was received into a string
		sdf.applyLocalizedPattern("dd/MMM/yyyy:HH:mm:ss");
		String timeReceived = "[" + sdf.format(new Date(currentTime)) + "]";

		// add the date and time that the request was received to the buffer
		logInfo.append(timeReceived + " ");

		// get the HTTP method of the request
		String method = ((HttpServletRequest) request).getMethod();

		// add the method to the buffer
		logInfo.append(method + " ");

		// get the URL from the request
		String uri = ((HttpServletRequest) request).getRequestURI();

		if (method.equals("GET")) {

			String query = ((HttpServletRequest) request).getQueryString();

			if (query != null)
				uri += "?" + query;

		}// end if

		// add the URL to the buffer
		logInfo.append(uri + " ");

		// get the names of the parameters from the request
		Enumeration paramNames = request.getParameterNames();

		// if the method type is POST, encode the parameters
		if (method.equals("POST")) {

			logInfo.append("--post-data=\"");

			if (paramNames.hasMoreElements())
				encodePostParameters(logInfo, paramNames, request);

			logInfo.append("\"] ");

		}// end if

		// get the cookies from the request
		Cookie[] cookies = ((HttpServletRequest) request).getCookies();

		// if there is at least one cookie
		if (cookies != null && cookies.length > 0)
			encodeCookies(logInfo, cookies);

		logInfo.append("] ");

		// get the referer from the request
		String referer = ((HttpServletRequest) request).getHeader("Referer");

		// add the referer to the buffer
		if (referer != null)
			logInfo.append(referer + " ");

		// get the content type of the request
		String contentType = request.getContentType();

		// prevents caching of static pages
		((HttpServletResponse) response)
				.setHeader("Cache-Control", "max-age=0");

		// if the request contains a file
		if ((contentType != null)
				&& (contentType.contains("multipart/form-data"))) {

			// wrap the request to save the input stream
			FilterRequestWrapper wrapper = new FilterRequestWrapper(request);

			try {

				// get the input stream from the request
				ServletInputStream stream = wrapper.getInputStream();

				// mark the start of the stream
				stream.mark(intFileSize + 1);

				// parse the wrapper request
				MultipartParser parser = new MultipartParser(
						(HttpServletRequest) wrapper, intFileSize);
				Part part;

				// get the date and time for the directory
				sdf.applyLocalizedPattern("dd.MMM.yyyy.HH.mm.ss");
				String dirTime = sdf.format(new Date(currentTime));

				// create the directory name to save the uploaded file in
				String directoryName = ipAddress + "." + dirTime;

				// create the complete directory name
				String compDirName = saveDir + "/" + directoryName;

				// create the directory
				new File(compDirName).mkdir();

				// a buffer to hold the upload parameters
				StringBuffer params = new StringBuffer("--params=\"");

				// get the parts of the parsed request
				while ((part = parser.readNextPart()) != null) {

					// if the part is a file
					if (part.isFile()) {

						// cast the part to a FilePart object
						FilePart filePart = (FilePart) part;

						// get the name used for the file on the upload page
						String formName = filePart.getName();

						// get the name of the file
						String uploadedFileName = filePart.getFileName();

						String dirNfile = null;

						if (uploadedFileName != null) {

							dirNfile = compDirName + "/" + uploadedFileName;

							// create and write to a FileOutputStream
							FileOutputStream fileOut = new FileOutputStream(
									dirNfile);
							long flength = filePart.writeTo(fileOut);
							fileOut.flush();
							fileOut.close();

							uploadedFileName = uploadedFileName.replaceAll("'",
									"\\'");

							uploadedFileName = URLEncoder.encode(
									uploadedFileName, "UTF-8");

						}// end if
						else {

							dirNfile = "";

						}// end else

						logInfo.append("--file=\"&" + formName + "=" + dirNfile
								+ "\" ");

					}// end if
					else {

						// cast the part to a ParamPart object
						ParamPart parPart = (ParamPart) part;

						// get the name of the part
						String paramName = URLEncoder.encode(parPart.getName(),
								"UTF-8");

						// get the value of the part
						String paramValue = URLEncoder.encode(
								parPart.getStringValue(), "UTF-8");

						// add the parameter pair to the list
						params.append("&" + paramName + "=" + paramValue);

					}// end else

				}// end while

				params.append("\"");

				// log parameters
				logInfo.append(params);

				// reset the input stream of the wrapped request
				stream.reset();

			}// end try
			catch (Exception e) {

				e.printStackTrace();

			}// end catch

			// log the buffer
			log.info(logInfo);

			// pass the wrapped request down the filter chain
			chain.doFilter((ServletRequest) wrapper, response);

		}// end if
		else {

			// log the buffer
			log.info(logInfo);

			// pass the request down the filter chain
			chain.doFilter(request, response);

		}// end else

	}

	private void createLogFile() {

		try {

			// create a new File object from logFileName
			File logFile = new File(logFileName);
			
			System.out.println(logFile.getCanonicalPath());

			// if the File object does not exist
			if (!logFile.exists()) {

				// try to create a new file
				logFile.createNewFile();

				// get a logger for this class
				log = Logger.getLogger(LogFilter.class);
				// set the level of the logger
				log.setLevel((Level) Level.INFO);

				log.removeAllAppenders();

				// add a FileAppender to the logger
				log.addAppender(new FileAppender(new PatternLayout(),
						logFileName));

			}// end if

		}// end try
		catch (Exception e) {
			System.err.println("Error trying to create: " + logFileName);
			e.printStackTrace();

		}// end catch

	}// end method createLogFile

	private void encodeCookies(StringBuffer logInfo, Cookie[] cookies) {

		Cookie cookie;

		for (int i = 0; i < cookies.length; i++) {

			cookie = cookies[i];
			String name = cookie.getName();
			String value = cookie.getValue();
			logInfo.append("--header=\"Cookie:" + name + "=" + value + "\"");

		}// end for

	}// end method encode Cookies

	private void encodePostParameters(StringBuffer logInfo,
			Enumeration paramNames, ServletRequest request) {

		while (paramNames.hasMoreElements()) {

			String name = (String) paramNames.nextElement();
			String values[] = request.getParameterValues(name);

			String name_encoded = "";

			try {

				name_encoded = URLEncoder.encode(name, "UTF-8");

			}// end try
			catch (java.io.UnsupportedEncodingException e) {

				e.printStackTrace();

			}// end catch

			for (int i = 0; i < values.length; i++) {

				String encodedValue = "";

				logInfo.append("&" + name_encoded + "=");

				try {

					encodedValue = URLEncoder.encode(values[i], "UTF-8");

				}// end try
				catch (java.io.UnsupportedEncodingException e) {

					e.printStackTrace();

				}// end catch

				logInfo.append(encodedValue);

			}// end for

		}// end while

	}// end method encodePostParameters

	private class FilterRequestWrapper extends HttpServletRequestWrapper {

		private SavedStream stream;

		// private ServletRequest request;

		public FilterRequestWrapper(ServletRequest request)
				throws java.io.IOException {

			super((HttpServletRequest) request);

			stream = new SavedStream(request.getInputStream());

		}// end constructor

		public ServletInputStream getInputStream() {

			return stream;

		}// end method getInputStream

		private class SavedStream extends ServletInputStream {

			private BufferedInputStream stream;

			public SavedStream(ServletInputStream inputStream) {

				stream = new BufferedInputStream(inputStream);

			}// end constructor

			public int read() throws java.io.IOException {

				return stream.read();

			}// end method readLine

			public void mark(int readlimit) {

				stream.mark(readlimit);

			}// end method mark

			public void reset() throws java.io.IOException {

				stream.reset();

			}// end method reset

		}// end class SavedStream

	}// end class FilterRequestWrapper

}
