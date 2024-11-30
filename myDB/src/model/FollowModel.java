package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import myDB.Twitter;

public class FollowModel {
    public static Connection con;

    public FollowModel(Connection con) {
        this.con = con;
    }
    
    /**
     * 팔로우 기능
     * @param userId 팔로우를 요청한 사용자 ID
     * @param followUserId 팔로우할 사용자 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public static boolean followUser(String userId, String followUserId) {
        String checkUserExistence = "SELECT user_id FROM user WHERE user_id = ?";
        String checkFollow = "SELECT f_id FROM following WHERE user_id = ? AND following_id = ?";
        String addFollowing = "INSERT INTO following (user_id, following_id) VALUES (?, ?)";

        try {
            // 유저 존재 여부 확인
            PreparedStatement checkUserStmt = con.prepareStatement(checkUserExistence);
            checkUserStmt.setString(1, followUserId);
            ResultSet userCheckResult = checkUserStmt.executeQuery();
            if (!userCheckResult.next()) {
                System.out.println("The user you are trying to follow does not exist.");
                return false;
            }

            // 팔로우 중복 확인
            PreparedStatement checkFollowStmt = con.prepareStatement(checkFollow);
            checkFollowStmt.setString(1, userId);
            checkFollowStmt.setString(2, followUserId);
            ResultSet rs = checkFollowStmt.executeQuery();
            if (rs.next()) {
                System.out.println("You are already following this user.");
                return false;
            }

            // 팔로우 추가 (following 테이블에 삽입)
            PreparedStatement followStmt = con.prepareStatement(addFollowing);
            followStmt.setString(1, userId);
            followStmt.setString(2, followUserId);
            followStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 언팔로우 기능
     * @param userId 언팔로우를 요청한 사용자 ID
     * @param unfollowUserId 언팔로우할 사용자 ID
     * @return 성공 여부 (true: 성공, false: 실패)
     */
    public static boolean unfollowUser(String userId, String unfollowUserId) {
        String checkUserExistence = "SELECT user_id FROM user WHERE user_id = ?";
        String removeFollowing = "DELETE FROM following WHERE user_id = ? AND following_id = ?";

        try {
            // 유저 존재 여부 확인
            PreparedStatement checkUserStmt = con.prepareStatement(checkUserExistence);
            checkUserStmt.setString(1, unfollowUserId);
            ResultSet userCheckResult = checkUserStmt.executeQuery();
            if (!userCheckResult.next()) {
                System.out.println("The user you are trying to unfollow does not exist.");
                return false;
            }

            // 팔로우 제거 (following 테이블에서 삭제)
            PreparedStatement unfollowStmt = con.prepareStatement(removeFollowing);
            unfollowStmt.setString(1, userId);
            unfollowStmt.setString(2, unfollowUserId);
            int rowsAffected = unfollowStmt.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                System.out.println("You are not following this user.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public static boolean toggleFollow(String userId, String followUserId) {
        boolean isFollowing = isFollowing(userId, followUserId);
        if (isFollowing) {
            return unfollowUser(userId, followUserId);
        } else {
            return followUser(userId, followUserId);
        }
    }

    public static boolean isFollowing(String userId, String followUserId) {
        String query = "SELECT f_id FROM following WHERE user_id = ? AND following_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, followUserId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // 팔로우 중이면 true, 아니면 false
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<User> getFollowingList(String userId) {
        List<User> followingList = new ArrayList<>();
        String query = "SELECT u.user_id, u.user_name, u.intro, u.image_url FROM following f "
                     + "JOIN user u ON f.following_id = u.user_id WHERE f.user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);  // userId는 다른 사람의 ID로 전달됩니다.
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String followingUserId = rs.getString("user_id");
                String followingUserName = rs.getString("user_name");
                String intro = rs.getString("intro");
                String imgUrl = rs.getString("image_url");
                User followingUser = new User(followingUserId, followingUserName, intro, imgUrl);
                followingList.add(followingUser);
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return followingList;
    }

    public static List<User> getFollowersList(String userId) {
        List<User> followersList = new ArrayList<>();
        String query = "SELECT u.user_id, u.user_name, u.intro, u.image_url FROM following f "
                     + "JOIN user u ON f.user_id = u.user_id WHERE f.following_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);  // userId는 다른 사람의 ID로 전달됩니다.
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String followerUserId = rs.getString("user_id");
                String followerUserName = rs.getString("user_name");
                String intro = rs.getString("intro");
                String imgUrl = rs.getString("image_url");
                User followerUser = new User(followerUserId, followerUserName, intro, imgUrl);
                followersList.add(followerUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return followersList;
    }



}
