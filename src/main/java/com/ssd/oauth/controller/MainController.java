package com.sliit.ssd.oauth.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.sliit.ssd.oauth.model.FileModel;
import com.sliit.ssd.oauth.service.GoogleDriveService;

@Controller
public class MainController {

	@Autowired
	GoogleDriveService google_Drive_Service;

	private static Logger logger = LoggerFactory.getLogger(MainController.class);

	@GetMapping("/")
	public String showIndex() throws IOException {
		return google_Drive_Service.userAuthenticatedTrue() ? "home.html" : "index.html";
	}

	@GetMapping("/googlesignin")
	public void getGoogleSignIn(HttpServletResponse response) throws IOException {
		google_Drive_Service.googleSignInPage(response);
	}

	@GetMapping("/oauth")
	public String storeCredentials(HttpServletRequest request) throws IOException {
		return google_Drive_Service.storeAuthorizationCodeTrue(request) ? "home.html" : "index.html";
	}

	@PostMapping("/upload")
	public String postUploadFile(HttpServletRequest servletRequest, @ModelAttribute FileModel file)
			throws IllegalStateException, IOException {
				google_Drive_Service.upload_File(file.getMultipartFile());
		return "home.html";
	}

	@GetMapping("/logout")
	public String getLogout(HttpServletRequest request) throws IOException {
		google_Drive_Service.logoutService(request);
		return "index.html/";
	}

}
