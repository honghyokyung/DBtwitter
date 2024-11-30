package model;

public class User {
    private String userId;
    private String userName;
    private String intro;
    private String imgUrl;
    


    public User(String userId, String userName, String intro, String imgUrl) {
        this.userId = userId;
        this.userName = userName;
        this.intro = intro;
        this.imgUrl = imgUrl;
    }

    // Getter와 Setter 메서드
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
   

}
