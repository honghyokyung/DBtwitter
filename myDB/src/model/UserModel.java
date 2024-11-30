package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class UserModel {
    private Connection con;
    private FollowModel followModel;  // FollowModel을 추가하여 팔로잉/팔로워 정보를 가져옴
    private String currentProfileUserId; 

    public UserModel(Connection con) {
        this.con = con;
        this.followModel = new FollowModel(con);  // FollowModel 초기화
    }

    public void setCurrentProfileUserId(String userId) {
        this.currentProfileUserId = userId;
    }

    public String getCurrentProfileUserId() {
        return this.currentProfileUserId;
    }
    public boolean loginUser(String userId, String password) {
        String query = "SELECT * FROM user WHERE user_id = ? AND pwd = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserProfile(String loggedInUserId) {
        String query = "SELECT user_id, user_name, intro, image_url FROM user WHERE user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, loggedInUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("user_id"),
                    rs.getString("user_name"),
                    rs.getString("intro"),
                    rs.getString("image_url")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateProfile(String loggedInUserId, String userName, String intro, String imgUrl) {
        String query = "UPDATE user SET user_name = ?, intro = ?, image_url = ? WHERE user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userName);
            stmt.setString(2, intro);
            stmt.setString(3, imgUrl);
            stmt.setString(4, loggedInUserId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 팔로잉 수 가져오기
    public int getFollowingCount(String userId) {
        String query = "SELECT COUNT(*) FROM following WHERE user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);  // userId는 String으로 처리
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);  // 팔로잉 수 반환
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // 오류 발생시 0 반환
    }

    // 팔로워 수 가져오기
    public int getFollowersCount(String userId) {
        String query = "SELECT COUNT(*) FROM following WHERE following_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);  // userId는 String으로 처리
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);  // 팔로워 수 반환
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // 오류 발생시 0 반환
    }
    
    

 // 팔로잉 리스트 가져오기 (User 객체로 반환)
    public List<User> getFollowingList(String userId) {
        if (userId == null) {
            return Collections.emptyList();  // userId가 null이면 빈 리스트 반환
        }
        List<User> followingList = followModel.getFollowingList(userId);
        return followingList != null ? followingList : Collections.emptyList();  // null 처리
    }

    // 팔로워 리스트 가져오기 (User 객체로 반환)
    public List<User> getFollowersList(String userId) {
        if (userId == null) {
            return Collections.emptyList();  // userId가 null이면 빈 리스트 반환
        }
        List<User> followersList = followModel.getFollowersList(userId);
        return followersList != null ? followersList : Collections.emptyList();  // null 처리
    }
    
    public String getUserProfileImageUrl(String userId) {
        String imageUrl = null;
        try (PreparedStatement pstmt = con.prepareStatement("SELECT image_url FROM User WHERE user_id = ?")) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    imageUrl = rs.getString("image_url");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 로깅 또는 예외 처리
        }
        return imageUrl != null && !imageUrl.isEmpty() ? imageUrl : null;
    }

}
