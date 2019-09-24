package com.sliit.ssd.oauth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties
public class AppConfig {

	@Value("${google.oauth.callback.url}")
	private String CallbackURI;

	@Value("${google.secret.key.path}")
	private Resource googledriveSecretKeys;

	@Value("${google.credentials.folder.path}")
	private Resource credentials_Folder;
	
	@Value("${uploadapp.temp.path}")
	private String temp_Folder;

	public String getCallbackURI() {
		return CallbackURI;
	}

	public void set_CallbackURI(String cALLBACK_URI) {
		CallbackURI = cALLBACK_URI;
	}

	public Resource get_GoogledriveSecretKeys() {
		return this.googledriveSecretKeys;
	}

	public void set_GoogledriveSecretKeys(Resource googledriveSecretKeys) {
		this.googledriveSecretKeys = googledriveSecretKeys;
	}

	public Resource get_Credentials_Folder() {
		return credentials_Folder;
	}

	public void set_Credentials_Folder(Resource credentials_Folder) {
		this.credentials_Folder = credentials_Folder;
	}

	public String get_Temp_Folder() {
		return temp_Folder;
	}

	public void set_Temp_Folder(String temp_Folder) {
		this.temp_Folder = temp_Folder;
	}
	
	

}
