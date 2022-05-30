$(document).ready(function() {
    (function (ko) {

        let CommentsViewModel = function () {
            let pathname = window.location.pathname;
            const postId = pathname.split("/").slice(-1);

            const self = this;
            this.comments = ko.observableArray();
            this.newComment = ko.observable('').extend({
                validation: {
                    message: "This field is required.",
                    validator: function (value) {
                        return value.trim().length !== 0
                    }
                }
            });
            this.currentUserId = ko.observable('');
            this.isUserAdminOrSuperAdmin = ko.observable('');

            $.ajax('/comments?postId=' + postId[0], {
                type : 'GET',
                success : function (data) {
                    data.forEach(element => console.log(element));
                    data.forEach(element => self.comments.push(element));
                },
                error : function (jqHXR) {
                    console.log(jqHXR);
                }
            });

            $.ajax('/current-user', {
                type : 'GET',
                success : function (data) {
                    self.currentUserId(data.id);
                    self.isUserAdminOrSuperAdmin(data.isUserAdminOrSuperAdmin);

                    console.log(self.currentUserId());
                    console.log(self.isUserAdminOrSuperAdmin());
                },
                error : function (jqHXR) {
                    console.log(jqHXR);
                }
            });


            this.removeComment = function (comment, event) {
                console.log(comment);

                $.ajax('/delete-comment/' + comment.id, {
                    type : 'DELETE'
                });

                let idx = self.comments.indexOf(comment);
                self.comments.splice(idx, 1);
            };

            console.log(self.currentUserId());
            console.log(self.isUserAdminOrSuperAdmin());

            self.handleSubmit = function () {

                let errors = ko.validation.group(self);

                if (errors().length > 0) {
                    console.log("error");
                    errors.showAllMessages();
                    return;
                }

                $.ajax('/add-comment', {
                    type : 'POST',
                    dataType : 'json',
                    contentType : 'application/json',
                    data: JSON.stringify({ 'body' : self.newComment(), 'postId' : postId[0]}),
                    success : function (data) {
                        console.log(data);
                        self.comments.push(data);
                        self.newComment('');

                        let propBooleanlValid = ko.validation.group(self.newComment, { deep: false });
                        propBooleanlValid.showAllMessages(false);
                    },
                    error : function (jqHXR) {
                        console.log(jqHXR);
                    }
                });

                console.log(self.newComment());

            }
        }


        ko.applyBindings(new CommentsViewModel());

        // console.log($(location).attr('href'));

        if ($(location).attr('href').includes('scroll=bottom')) {
            const scrollingElement = (document.scrollingElement || document.body);

            $(scrollingElement).animate({
                scrollTop: document.body.scrollHeight,
            }, 500);
        }

    })(ko);

})
