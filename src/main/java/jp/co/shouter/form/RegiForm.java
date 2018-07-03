package jp.co.shouter.form;

public class RegiForm
{
	//INSERT INTO users(loginId,password,userName,icon,profile,authority) VALUES('yamada','pass1','taroyamada','icon-user','はじめまして','ROLE_ADMIN');
	private String loginId;
	private String password;
	private String username;
	private String sex;
	private String profile;
	private String authority;

	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}


	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}




}
