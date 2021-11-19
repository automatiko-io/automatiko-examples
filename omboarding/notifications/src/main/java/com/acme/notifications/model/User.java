package com.acme.notifications.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phone;

    private Integer userStatus;

    /**
     */
    public User id(Long id) {
        this.id = id;
        return this;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     */
    public User username(String username) {
        this.username = username;
        return this;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     */
    public User firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     */
    public User lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     */
    public User email(String email) {
        this.email = email;
        return this;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     */
    public User password(String password) {
        this.password = password;
        return this;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     */
    public User phone(String phone) {
        this.phone = phone;
        return this;
    }

    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * User Status
     */
    public User userStatus(Integer userStatus) {
        this.userStatus = userStatus;
        return this;
    }

    @JsonProperty("userStatus")
    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(this.id, user.id) && Objects.equals(this.username, user.username)
                && Objects.equals(this.firstName, user.firstName) && Objects.equals(this.lastName, user.lastName)
                && Objects.equals(this.email, user.email) && Objects.equals(this.password, user.password)
                && Objects.equals(this.phone, user.phone) && Objects.equals(this.userStatus, user.userStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, firstName, lastName, email, password, phone, userStatus);
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
                + email + ", password=" + password + ", phone=" + phone + ", userStatus=" + userStatus + "]";
    }

    public User() {
    }

}
