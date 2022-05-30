$(document).ready(function() {
    (function (ko) {

        let AdminPanelViewModel = function () {
            const self = this;
            this.postsNumber = ko.observable('');
            this.commentsNumber = ko.observable('');
            this.admins = ko.observableArray();
            this.bannedUsers = ko.observableArray();
            this.users = ko.observableArray();

            this.currentUserId = ko.observable('');
            this.isCurrentUserSuperAdmin = ko.observable('');

            $.ajax('/get-all-users', {
                type : 'GET',
                success : function (users) {
                    users.forEach(user => console.log(user));
                    users.forEach(user => {
                        if (self.isUserAdminOrSuperAdmin(user.roles)) {
                            self.admins.push(user)
                        }
                        else if (user.active) {
                            self.users.push(user)
                        }
                        else {
                            self.bannedUsers.push(user)
                        }
                    });
                },
                error : function (jqHXR) {
                    console.log(jqHXR);
                }
            });

            $.ajax('/current-user', {
                type : 'GET',
                success : function (data) {
                    self.currentUserId(data.id);
                    self.isCurrentUserSuperAdmin(data.isUserSuperAdmin);

                    console.log(self.currentUserId());
                    console.log(self.isCurrentUserSuperAdmin());
                },
                error : function (jqHXR) {
                    console.log(jqHXR);
                }
            });

            $.ajax('/get-content', {
                type : 'GET',
                success : function (data) {
                    self.postsNumber(data.postCount);
                    self.commentsNumber(data.commentCount);
                },
                error : function (jqHXR) {
                    console.log(jqHXR);
                }
            });

            this.isUserAdminOrSuperAdmin = function (roles) {
                console.log(roles)
                for (let i = 0; i < roles.length; i++) {
                    if (roles[i].name === 'ADMIN' || roles[i].name === 'SUPER_ADMIN') {
                        return true;
                    }
                }
                return false;
            }

            this.isUserSuperAdmin = function (roles) {
                console.log(roles)
                for (let i = 0; i < roles.length; i++) {
                    if (roles[i].name === 'SUPER_ADMIN') {
                        return true;
                    }
                }
                return false;
            }

            this.banUser = function (user, event) {
                console.log(user)

                $.ajax('/user/' + user.username + '/ban', {
                    type : 'POST',
                    success : function (path) {
                        for (let i = 0; i < self.users().length; i++) {
                            if (user.id === self.users()[i].id) {
                                let bannedUser = self.users.splice(i, 1)[0];
                                bannedUser.active = false;
                                self.bannedUsers.push(bannedUser);
                            }
                        }
                    },
                    error : function (jqHXR) {
                        console.log(jqHXR);
                    }
                });
            }

            this.unbanUser = function (user, event) {
                $.ajax('/user/' + user.username + '/unban', {
                    type : 'POST',
                    success : function (path) {
                        for (let i = 0; i < self.bannedUsers().length; i++) {
                            if (user.id === self.bannedUsers()[i].id) {
                                let user_ = self.bannedUsers.splice(i, 1)[0];
                                user_.active = true;
                                self.users.push(user_);
                            }
                        }
                    },
                    error : function (jqHXR) {
                        console.log(jqHXR);
                    }
                });
            }

            this.makeAdmin = function (user, event) {
                $.ajax('/user/' + user.username + '/make-admin', {
                    type : 'POST',
                    success : function (path) {
                        for (let i = 0; i < self.users().length; i++) {
                            if (user.id === self.users()[i].id) {
                                if (!self.isUserAdminOrSuperAdmin(self.users()[i].roles)) {
                                    let user_ = self.users.splice(i, 1)[0];
                                    user_.roles.push({id: 2, name: 'ADMIN'});
                                    self.admins.push(user_);
                                }
                            }
                        }
                    },
                    error : function (jqHXR) {
                        console.log(jqHXR);
                    }
                });
            }

            this.declineAdmin = function (user, event) {

                console.log(user)

                $.ajax('/user/' + user.username + '/delete-admin', {
                    type : 'POST',
                    success : function (path) {
                        for (let i = 0; i < self.admins().length; i++) {
                            if (user.id === self.admins()[i].id) {
                                if (!self.isUserSuperAdmin(self.admins()[i].roles)) {
                                    let user_ = self.admins.splice(i, 1)[0];
                                    let idx =  user_.roles.indexOf({id: 2, name: 'ADMIN'});
                                    user_.roles.splice(idx, 1);
                                    console.log(user_)
                                    self.users.push(user_);
                                }
                            }
                        }
                    },
                    error : function (jqHXR) {
                        console.log(jqHXR);
                    }
                });
            }
        }

        ko.applyBindings(new AdminPanelViewModel());

    })(ko);

})
