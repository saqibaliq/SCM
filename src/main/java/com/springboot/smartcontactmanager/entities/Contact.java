package com.springboot.smartcontactmanager.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "contact")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	private String cName;
	private String cNickName;
	private String cWork;
	private String cEmail;
	private String cPhone;
	@Column(name = "c_image")
	private String image;
	private String cDesc;
	@ManyToOne
	@JsonIgnore
	private User user;

	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the cId
	 */
	public int getcId() {
		return cId;
	}

	/**
	 * @param cId the cId to set
	 */
	public void setcId(int cId) {
		this.cId = cId;
	}

	/**
	 * @return the cName
	 */
	public String getcName() {
		return cName;
	}

	/**
	 * @param cName the cName to set
	 */
	public void setcName(String cName) {
		this.cName = cName;
	}

	/**
	 * @return the cNickName
	 */
	public String getcNickName() {
		return cNickName;
	}

	/**
	 * @param cNickName the cNickName to set
	 */
	public void setcNickName(String cNickName) {
		this.cNickName = cNickName;
	}

	/**
	 * @return the cWork
	 */
	public String getcWork() {
		return cWork;
	}

	/**
	 * @param cWork the cWork to set
	 */
	public void setcWork(String cWork) {
		this.cWork = cWork;
	}

	/**
	 * @return the cEmail
	 */
	public String getcEmail() {
		return cEmail;
	}

	/**
	 * @param cEmail the cEmail to set
	 */
	public void setcEmail(String cEmail) {
		this.cEmail = cEmail;
	}

	/**
	 * @return the cPhone
	 */
	public String getcPhone() {
		return cPhone;
	}

	/**
	 * @param cPhone the cPhone to set
	 */
	public void setcPhone(String cPhone) {
		this.cPhone = cPhone;
	}

	/**
	 * @return the cImage
	 */

	/**
	 * @return the cDesc
	 */
	public String getcDesc() {
		return cDesc;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @param cDesc the cDesc to set
	 */
	public void setcDesc(String cDesc) {
		this.cDesc = cDesc;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.cId == ((Contact) obj).getcId();
	}

}
