package com.sliit.ssd.oauth.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.sliit.ssd.oauth.util.AppConfig;


@Service
public class GoogleDriveService {

	@Autowired
	private AppConfig app_Config;

	private static Logger user_logger = LoggerFactory.getLogger(GoogleDriveService.class);

	
	private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	private static JsonFactory JsonFactory = JacksonFactory.getDefaultInstance();

	private static final List<String> Drive_Scopes = Collections.singletonList(DriveScopes.DRIVE);

	private static final String UserIdentifierKey = "MY_USER";

	private static final String ApplicationName = "UploaderSSD";

	private GoogleAuthorizationCodeFlow google_Authorization_Code_Flow;

	private Drive drive;

	@PostConstruct
	public void init() throws IOException {
		user_logger.info("Started init...");
		GoogleClientSecrets secret = GoogleClientSecrets.load(JsonFactory,
				new InputStreamReader(app_Config.get_GoogledriveSecretKeys().getInputStream()));
				user_logger.info("Secret fetched...");
		google_Authorization_Code_Flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JsonFactory, secret, Drive_Scopes)
				.setDataStoreFactory(new FileDataStoreFactory(app_Config.get_Credentials_Folder().getFile())).build();
		drive = new Drive.Builder(HTTP_TRANSPORT, JsonFactory, getCredential()).setApplicationName(ApplicationName)
				.build();
	}

	public Credential getUserCredential() throws IOException {
		return google_Authorization_Code_Flow.loadCredential(UserIdentifierKey);
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public boolean userAuthenticatedTrue() throws IOException {

		Credential user_credential = getUserCredential();
		boolean tokenValid = false;
		if (user_credential != null) {
			tokenValid = user_credential.refreshToken();
			return tokenValid;
		}
		return tokenValid;
	}

	/**
	 * @param response
	 * @throws IOException
	 * 
	 **/
	public void googleSignInPage(HttpServletResponse response) throws IOException {
		GoogleAuthorizationCodeRequestUrl url = google_Authorization_Code_Flow.newAuthorizationUrl();
		String redirectURL = url.setRedirectUri(app_Config.getCallbackURI()).setAccessType("offline").build();
		response.sendRedirect(redirectURL);
	}

	/**
	 * @param request
	 * @throws IOException
	 */
	public boolean storeAuthorizationCodeTrue(HttpServletRequest request) throws IOException {
		String code = request.getParameter("code");

		if (code != null) {
			saveToken(code);
			return true;
		}
		return false;
	}

	/**
	 * @param code
	 * @throws IOException
	 */
	private void saveToken(String code) throws IOException {
		GoogleTokenResponse response = google_Authorization_Code_Flow.newTokenRequest(code).setRedirectUri(app_Config.getCallbackURI()).execute();
		google_Authorization_Code_Flow.createAndStoreCredential(response, UserIdentifierKey);
	}

	/**
	 * @throws IOException
	 * @throws IllegalStateException
	 * 
	 */
	public void upload_File(MultipartFile multi_part_File) throws IllegalStateException, IOException {

		String original_FileName = multi_part_File.getOriginalFilename();
		String file_Content_Type = multi_part_File.getContentType();

		String temp_Path = app_Config.get_Temp_Folder();

		File copy_File = new File(temp_Path, original_FileName);

		multi_part_File.transferTo(copy_File);

		com.google.api.services.drive.model.File metaDataFile = new com.google.api.services.drive.model.File();
		metaDataFile.setName(original_FileName);
		FileContent fileContent = new FileContent(file_Content_Type, copy_File);

		com.google.api.services.drive.model.File verifyFile = drive.files().create(metaDataFile, fileContent)
				.setFields("id").execute();
				user_logger.info("Created File: "+verifyFile.getId());

	}
	
	public void logoutService(HttpServletRequest request){
		HttpSession httpsession = request.getSession(false);
		httpsession = request.getSession(true);
		if (httpsession != null) {
			httpsession.invalidate();
			user_logger.info("Logged Out...");
		}
	}
}
