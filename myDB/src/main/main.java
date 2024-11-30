package main;

import gui.profileGUI;
import myDB.Twitter;

import java.sql.SQLException;

import javax.swing.*;

public class main {
    public static void main(String[] args) {
        // 데이터베이스 연결
    	try {
            Twitter.getConnection();  // getConnection() 메서드로 연결을 설정
        } catch (SQLException e) {
            System.err.println("Database connection failed.");
            e.printStackTrace();
            return;
        }

        // GUI 애플리케이션 실행
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // TwitterGUI 클래스 실행
                profileGUI frame = new profileGUI();
                frame.setVisible(true);
            }
        });
    }
}
