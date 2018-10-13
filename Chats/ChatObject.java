package com.src.magakim.dogpark.Chats;

public class ChatObject {
        private String message;
        private Boolean currentUser;
        private String username;
        private String imageURL;
        private String time;
        public ChatObject(String message, Boolean currentUser, String username, String imageURL, String time) {
            this.message = message;
            this.currentUser = currentUser;
            this.username = username;
            this.imageURL = imageURL;
            this.time = time;
        }
        public ChatObject(String message, Boolean currentUser, String username, String time) {
            this.message = message;
            this.currentUser = currentUser;
            this.username = username;
            this.time = time;
            this.imageURL = "default";
    }

        public String getTime() {return time;}
        public String getImageURL() {return imageURL;}
        public String getMessage() {return message;}
        public String getUsername() {return username;}
        public void setMessage(String userID) {
            this.message = message;
        }

        public Boolean getCurrentUser() {return currentUser;}
        public void setCurrentUser(Boolean currentUser) {this.currentUser = currentUser;}

}