/*
 * This file is part of Alpine.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) Steve Springett. All Rights Reserved.
 */
package alpine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.security.Principal;
import java.util.List;

/**
 * Persistable object representing an ManagedUser.
 *
 * @author Steve Springett
 * @since 1.0.0
 */
@PersistenceCapable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManagedUser implements Serializable, Principal, UserPrincipal {

    private static final long serialVersionUID = 7944779964068911025L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @JsonIgnore
    private long id;

    @Persistent
    @Unique(name = "MANAGEDUSER_USERNAME_IDX")
    @Column(name = "USERNAME")
    @NotNull
    @Size(min = 1, max = 255)
    @Pattern(regexp = "[\\P{Cc}]+", message = "The username must not contain control characters")
    private String username;

    @Persistent
    @Column(name = "PASSWORD", allowsNull = "false")
    @NotNull
    @Size(min = 1, max = 255)
    @Pattern(regexp = "[\\P{Cc}]+", message = "The password must not contain control characters")
    @JsonIgnore
    private String password;

    @Size(max = 255)
    @Pattern(regexp = "[\\P{Cc}]+", message = "The new password must not contain control characters")
    private transient String newPassword; // not persisted

    @Size(max = 255)
    @Pattern(regexp = "[\\P{Cc}]+", message = "The confirm password must not contain control characters")
    private transient String confirmPassword; // not persisted

    @Persistent
    @Column(name = "FULLNAME")
    @Size(max = 255)
    @Pattern(regexp = "[\\P{Cc}]+", message = "The full name must not contain control characters")
    private String fullname;

    @Persistent
    @Column(name = "EMAIL")
    @Size(max = 255)
    @Pattern(regexp = "[\\P{Cc}]+", message = "The email address must not contain control characters")
    private String email;

    @Persistent
    @Column(name = "SUSPENDED")
    private boolean suspended;

    @Persistent(table = "MANAGEDUSERS_TEAMS", defaultFetchGroup = "true")
    @Join(column = "MANAGEDUSER_ID")
    @Element(column = "TEAM_ID")
    @Order(extensions = @Extension(vendorName = "datanucleus", key = "list-ordering", value = "name ASC"))
    private List<Team> teams;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    /**
     * Do not use - only here to satisfy Principal implementation requirement.
     * @deprecated use {@link ManagedUser#getUsername()}
     * @return the value of {@link #getUsername()}
     */
    @Deprecated
    @JsonIgnore
    public String getName() {
        return getUsername();
    }

}
