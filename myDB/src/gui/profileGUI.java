package gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.Stack;

import model.UserModel;
import model.FollowModel;
import model.User;
import myDB.Twitter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URI;

public class profileGUI extends JFrame {

	private CardLayout cardLayout; // 화면 전환을 위한 CardLayout
    private JPanel mainPanel; // 메인 패널 (CardLayout을 적용)

    // 로그인, 프로필, 게시물 패널 변수 선언
    private JPanel loginPanel, profilePanel, postsPanel;
    private JTextField userIdField, userNameField; // 입력 필드: 사용자 ID, 이름
    private JPasswordField passwordField; // 비밀번호 입력 필드
    private JTextArea introArea; // 자기소개 텍스트 영역
    private JButton loginButton, settingButton, followingButton, followersButton, saveButton, cancelButton, postsButton, likesButton, bookmarksButton;

    private String loggedInUserId;  // 로그인된 사용자 ID
    private Connection con; // 데이터베이스 연결
    private UserModel userModel; // 사용자 정보 모델
    
 // 기존 필드들 유지
    private Stack<NavigationState> navigationStack = new Stack<>();

    public profileGUI() {
        try {
			con = Twitter.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        userModel = new UserModel(con);

        setTitle("Twitter Application"); // GUI 제목 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 닫기 동작 설정
        setSize(400, 600);  // 창 크기 설정
        setLocationRelativeTo(null); // 화면 중앙 배치

        cardLayout = new CardLayout(); // CardLayout 생성
        mainPanel = new JPanel(cardLayout);// 메인 패널에 CardLayout 적용
        mainPanel.setBackground(Color.WHITE);

        createLoginPanel(); // 로그인 패널 생성
        mainPanel.add(loginPanel, "Login"); // 로그인 패널을 메인 패널에 추가

        add(mainPanel); // 메인 패널을 프레임에 추가
        cardLayout.show(mainPanel, "Login"); // 초기 화면 설정

        setVisible(true); // GUI 표시
    }

    // 로그인 화면 생성 대충임의로함.
    private void createLoginPanel() {
        loginPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 로그인 화면 레이아웃 설정
        loginPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // 여백 설정

        userIdField = new JTextField(); // 사용자 ID 입력 필드
        passwordField = new JPasswordField(); // 비밀번호 입력 필드
        loginButton = new JButton("Login"); // 로그인 버튼

        loginPanel.add(new JLabel("User ID:")); // 사용자 ID 레이블
        loginPanel.add(userIdField); // 사용자 ID 입력 필드 추가
        loginPanel.add(new JLabel("Password:")); // 비밀번호 레이블
        loginPanel.add(passwordField); // 비밀번호 입력 필드 추가
        loginPanel.add(new JLabel()); // 빈 공간 추가
        loginPanel.add(loginButton); // 로그인 버튼 추가

        loginButton.addActionListener(e -> handleLogin()); // 로그인 버튼 클릭 시 동작 설정
    }


 // 프로필 화면 생성
    private void createProfilePanel(User user) {
        profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(Color.WHITE);

     // 현재 프로필 사용자 ID 추적 로직 추가
        userModel.setCurrentProfileUserId(user.getUserId());
     
        // 상단 영역: 프로필 이미지와 설정 버튼
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(106, 181, 249));
        topPanel.setPreferredSize(new Dimension(0, 50)); // 높이를 100으로 설정 (원하는 값으로 조정)
        topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 10)); // 왼쪽 정렬, setting 버튼을 오른쪽 정렬 위해
        
     // 프로필 사진을 담을 패널 생성
        //JPanel profileImagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        //profileImagePanel.setBackground(Color.WHITE); // 탑 패널과 동일한 배경색
        
     // 유저 사진 -> 클릭 시 프로필로 이동
        JButton userImageBtn = new JButton();
        userImageBtn.setBackground(new Color(255, 255, 255));
        //userImageBtn.setPreferredSize(new Dimension(100, 100)); // 크기를 조금 더 크게 설정
        userImageBtn.setBounds(12, 10, 55, 55);
        userImageBtn.setContentAreaFilled(false); // 버튼 배경 투명
        userImageBtn.setBorderPainted(false);    // 버튼 테두리 제거
        userImageBtn.setFocusPainted(false);     // 버튼 포커스 표시 제거
        userImageBtn.setOpaque(false);           // 불투명 설정 해제
        //profiPanel.add(userImageBtn);
        // 유저 사진 로드 함수 호출
        loadUserImage(userImageBtn, user.getUserId());
        //profileImagePanel.add(userImageBtn);

     // 메인 패널의 레이아웃을 BorderLayout으로 변경
        profilePanel.setLayout(new BorderLayout());
        
     // profileImagePanel을 CENTER와 NORTH 사이에 배치
        //profilePanel.add(profileImagePanel, BorderLayout.CENTER);
        ///profilePanel.add(topPanel, BorderLayout.NORTH);
        //profilePanel.add(infoPanel, BorderLayout.SOUTH);
        
        JPanel infoPanel = new JPanel();
        JButton actionButton;
        boolean isOtherUser = !loggedInUserId.equals(user.getUserId());
       
        if (isOtherUser) {
            
        	// 이미지 로드
        	ImageIcon originalIcon = new ImageIcon("src/icon/backButton.png"); // 클래스 경로 기준
        	Image originalImage = originalIcon.getImage();

        	// 이미지 크기 조정 (예: 20x20)
        	Image scaledImage = originalImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);

        	// 크기 조정된 이미지를 다시 ImageIcon으로 변환
        	ImageIcon scaledIcon = new ImageIcon(scaledImage);

        	// JButton 생성 및 아이콘 설정
        	JButton backButton = new JButton(scaledIcon);
        	backButton.setPreferredSize(new Dimension(30, 30)); // 버튼 크기 조정
        	backButton.setContentAreaFilled(false); // 배경 비활성화
        	backButton.setBorderPainted(false); // 버튼 테두리 제거
        	backButton.setFocusPainted(false); // 버튼 포커스 표시 제거
        	//Image backImage = backIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        	//backIcon = new ImageIcon(backImage);

        	// 버튼 동작 설정
        	//backButton.addActionListener(e -> createProfilePanel(userModel.getUserProfile(loggedInUserId)));  // 프로필로 돌아가기
        	if (!loggedInUserId.equals(user.getUserId())) {
                backButton.addActionListener(e -> navigateBack());
            }
        	// 패널에 추가
        	topPanel.add(backButton, BorderLayout.WEST);

        	// 팔로우/언팔로우 버튼
        	actionButton = new JButton(FollowModel.isFollowing(loggedInUserId, user.getUserId()) ? "Following" : "Follow");
        	actionButton.setPreferredSize(new Dimension(80, 30));
        	actionButton.setBorder(BorderFactory.createEmptyBorder());
        	actionButton.setContentAreaFilled(false);

        	actionButton.setBackground(Color.WHITE);  
            actionButton.setForeground(Color.WHITE);  
            actionButton.setBorder(new LineBorder(Color.WHITE));
        	actionButton.setOpaque(false);

        	topPanel.add(actionButton, BorderLayout.EAST);

        	// 팔로우 버튼 클릭 이벤트 설정
        	actionButton.addActionListener(e -> {
        	    try {
        	        boolean isCurrentlyFollowing = FollowModel.isFollowing(loggedInUserId, user.getUserId());
        	        boolean toggleSuccess = FollowModel.toggleFollow(loggedInUserId, user.getUserId());
        	        
        	        if (toggleSuccess) {
        	            // 상태가 변경되었으므로 버튼 텍스트 업데이트
        	            if (isCurrentlyFollowing) {
        	                actionButton.setText("Follow");
        	                JOptionPane.showMessageDialog(null, "Unfollowed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        	            } else {
        	                actionButton.setText("Following");
        	                JOptionPane.showMessageDialog(null, "Followed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        	            }
        	        } else {
        	            JOptionPane.showMessageDialog(null, "Failed to update follow status.", "Error", JOptionPane.ERROR_MESSAGE);
        	        }
        	    } catch (Exception ex) {
        	        ex.printStackTrace();
        	        JOptionPane.showMessageDialog(null, "An error occurred while processing your request.", "Error", JOptionPane.ERROR_MESSAGE);
        	    }
        	});}

        else {

            JButton settingButton = new JButton("Edit profile") {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // 타원형 테두리 그리기
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2)); // 테두리 두께 설정
                    g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

                    g2.dispose();
                }
            };

            settingButton.setFont(new Font("Arial", Font.PLAIN, 12));

            FontMetrics metrics = settingButton.getFontMetrics(settingButton.getFont());
            int width = metrics.stringWidth("Edit profile") + 10; // 텍스트의 가로 크기 + 여백
            int height = metrics.getHeight()+10 ; // 텍스트의 세로 크기 + 여백

            settingButton.setPreferredSize(new Dimension(width, height));
            settingButton.setBorder(BorderFactory.createEmptyBorder()); // 테두리 없애기
            settingButton.setContentAreaFilled(false); // 배경 없애기
            settingButton.setFocusPainted(false); // 포커스 효과 없애기
            settingButton.setForeground(Color.WHITE); // 텍스트 색상 설정

            topPanel.add(settingButton, BorderLayout.EAST);
            settingButton.addActionListener(e -> createEditableProfilePanel(user));
            
        }
        
        //JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); // 세로 정렬
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 전체 왼쪽 정렬
        infoPanel.add(Box.createRigidArea(new Dimension(0, 30))); // 위쪽 여백
        infoPanel.add(Box.createRigidArea(new Dimension(20, 0))); // 왼쪽 여백 추가

        
        // 사용자 정보 라벨
        JLabel nameLabel = new JLabel(user.getUserName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // 왼쪽 정렬

        JLabel idLabel = new JLabel("@" + user.getUserId());
        idLabel.setForeground(Color.GRAY);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // 왼쪽 정렬
        idLabel.setFont(new Font("Bold", Font.PLAIN, 12));

        
        JLabel introLabel = new JLabel(user.getIntro());
        introLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // 왼쪽 정렬

        infoPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 위쪽 여백
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5))); // 이름과 아이디 간격
        infoPanel.add(idLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 아이디와 소개 간격
        infoPanel.add(introLabel);

        // 팔로우 관련 영역
        JPanel followPanel = new JPanel();
        followPanel.setLayout(new BoxLayout(followPanel, BoxLayout.X_AXIS)); // 세로 정렬
        followPanel.setBackground(Color.WHITE);
        followPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 전체 왼쪽 정렬

        JLabel followingCountLabel = new JLabel(String.valueOf(userModel.getFollowingCount(user.getUserId())));
        JButton followingButton = new JButton("Following");
        followingButton.setPreferredSize(new Dimension(80, 30));
        followingButton.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel followersCountLabel = new JLabel(String.valueOf(userModel.getFollowersCount(user.getUserId())));
        JButton followersButton = new JButton("Followers");
        followersButton.setPreferredSize(new Dimension(80, 30));
        followersButton.setFont(new Font("Arial", Font.PLAIN, 12));

        followPanel.add(followingCountLabel);
        followPanel.add(Box.createRigidArea(new Dimension(6, 0))); 
        followPanel.add(followingButton);
        followPanel.add(Box.createRigidArea(new Dimension(10, 10))); // 버튼 간격
        followPanel.add(followersCountLabel);
        followPanel.add(Box.createRigidArea(new Dimension(6, 0))); 
        followPanel.add(followersButton);

        // infoPanel에 followPanel 추가
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 위쪽 여백
        infoPanel.add(followPanel);  // '팔로잉'과 '팔로워' 버튼을 infoPanel 하단에 배치

        followingButton.setBackground(Color.WHITE);  
        followingButton.setForeground(Color.GRAY);  
        followingButton.setBorder(new LineBorder(Color.WHITE));
        followersButton.setBackground(Color.WHITE);  
        followersButton.setForeground(Color.GRAY);  
        followersButton.setBorder(new LineBorder(Color.WHITE));

        followingButton.addActionListener(e -> viewFollowing());
        followersButton.addActionListener(e -> viewFollowers());

        
        // 하단 탭: posts, likes, bookmarks
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel postsTab = new JPanel();
        tabbedPane.setPreferredSize(new Dimension(400, 370));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setBorder(new LineBorder(Color.WHITE));

        
        postsTab.add(new JLabel("게시물이 여기에 표시됩니다."));
        JPanel likesTab = new JPanel();
        likesTab.add(new JLabel("좋아요한 게시물이 여기에 표시됩니다."));
         
        postsTab.setBackground(Color.WHITE);
        likesTab.setBackground(Color.WHITE);
        //bookmarksTab.setBackground(Color.WHITE);

        tabbedPane.addTab("Posts", postsTab);
        tabbedPane.addTab("Likes", likesTab);
        
        if (!isOtherUser) {
            JPanel bookmarksTab = new JPanel();
            bookmarksTab.add(new JLabel("북마크한 게시물이 여기에 표시됩니다."));
            bookmarksTab.setBackground(Color.WHITE);
            tabbedPane.addTab("Bookmarks", bookmarksTab);
        }

        tabbedPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        profilePanel.add(topPanel, BorderLayout.NORTH);
        profilePanel.add(infoPanel, BorderLayout.CENTER); 
        profilePanel.add(tabbedPane, BorderLayout.SOUTH);

        
        mainPanel.add(profilePanel, "Profile");
        cardLayout.show(mainPanel, "Profile");
        
        
    }

    private void showPosts() {
        postsPanel = new JPanel();
        postsPanel.add(new JLabel("Posts will be displayed here"));
        mainPanel.add(postsPanel, "Posts");
        cardLayout.show(mainPanel, "Posts");
    }

    private void showLikes() {
        postsPanel = new JPanel();
        postsPanel.add(new JLabel("Likes will be displayed here"));
        mainPanel.add(postsPanel, "Likes");
        cardLayout.show(mainPanel, "Likes");
    }

    private void showBookmarks() {
        postsPanel = new JPanel();
        postsPanel.add(new JLabel("Bookmarks will be displayed here"));
        mainPanel.add(postsPanel, "Bookmarks");
        cardLayout.show(mainPanel, "Bookmarks");
    }

    private void createEditableProfilePanel(User user) {
        JPanel editablePanel = new JPanel(new BorderLayout());
        editablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 20));
        editablePanel.setBackground(Color.WHITE);
        
        JLabel editProfileLabel = new JLabel("Edit Profile", SwingConstants.CENTER);
        editProfileLabel.setFont(new Font("Modern", Font.BOLD, 20)); // 텍스트 크기와 스타일 설정
        editProfileLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); // 여백 추가
        editablePanel.add(editProfileLabel, BorderLayout.NORTH); // 최상단에 배치
        
        // Editable profile info panel
        JPanel profileInfoPanel = new JPanel(new GridLayout(5, 2, 10, 10));  // Increased row count to 5
        profileInfoPanel.setBackground(Color.WHITE);
       
     // Add image URL input field
        JLabel imgUrlLabel = new JLabel("Image URL");
        JTextField imgUrlField = new JTextField(user.getImgUrl());
        imgUrlLabel.add(Box.createRigidArea(new Dimension(0, 20))); // 위쪽 여백
        
        JLabel userIdLabel = new JLabel("User ID");
        JTextField userIdField = new JTextField("@"+user.getUserId());
        userIdField.setBorder(new LineBorder(Color.WHITE));
        userIdField.setBackground(Color.WHITE);
        userIdField.setEditable(false);  // Read-only field
        userIdField.setFocusable(false); // Cannot click

        JLabel userNameLabel = new JLabel("Name");
        userNameField = new JTextField(user.getUserName());
        JLabel introLabel = new JLabel("Bio");
        introArea = new JTextArea(user.getIntro(), 3, 20);
        introArea.setLineWrap(true);
        introArea.setWrapStyleWord(true);

        profileInfoPanel.add(userIdLabel);
        profileInfoPanel.add(userIdField);  // user_id is read-only
        profileInfoPanel.add(userNameLabel);
        profileInfoPanel.add(userNameField);
        profileInfoPanel.add(introLabel);
        profileInfoPanel.add(new JScrollPane(introArea));
        profileInfoPanel.add(imgUrlLabel);
        profileInfoPanel.add(imgUrlField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 40));
        buttonPanel.setBackground(Color.WHITE);
     //cancel button
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> createProfilePanel(user));
        cancelButton.setBackground(new Color(106, 181, 249));  
        cancelButton.setForeground(Color.WHITE);  
        cancelButton.setBorder(new LineBorder(new Color(106, 181, 249)));
        cancelButton.setPreferredSize(new Dimension(80, 30));
        
      // Save button
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveProfile(user, imgUrlField));
        saveButton.setBackground(new Color(106, 181, 249));  // 배경색을 파란색으로 변경
        saveButton.setForeground(Color.WHITE);  // 글자색을 흰색으로 변경
        saveButton.setBorder(new LineBorder(new Color(106, 181, 249)));
        saveButton.setPreferredSize(new Dimension(80, 30));
        

     // cancel and save buttons positioned at the ends
        buttonPanel.add(cancelButton, BorderLayout.WEST);  // 왼쪽에 배치
        buttonPanel.add(saveButton, BorderLayout.EAST);    // 오른쪽에 배치

        // Add panels to the editable panel
        editablePanel.add(profileInfoPanel, BorderLayout.CENTER);
        editablePanel.add(buttonPanel, BorderLayout.SOUTH); // 버튼들을 하단에 배치

        // Add editable panel to main panel
        mainPanel.add(editablePanel, "EditableProfile");
        cardLayout.show(mainPanel, "EditableProfile");
    }
    

    private void handleLogin() {
        String userIdText = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (userIdText.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both User ID and Password.");
            return;
        }

        try {
            String userId = userIdText; // Treat user_id as String now
            if (userModel.loginUser(userId, password)) {
            	navigationStack.clear();
                loggedInUserId = userId;
                User user = userModel.getUserProfile(loggedInUserId);
                //createProfilePanel(user);//???
                if (user != null) {
                    createProfilePanel(user);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to load profile data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid User ID or Password.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "User ID must be a valid string.");
        }
        
        
    }
    private void loadUserImage(JButton button, String userId) {
        try {
            // UserModel을 통해 이미지 URL 가져오기
            String imageUrl = userModel.getUserProfileImageUrl(userId);
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    URI uri = new URI(imageUrl);
                    URL url = uri.toURL();
                    ImageIcon circularIcon = createCircularImageIcon(url, 55, 55);
                    button.setIcon(circularIcon);
                } catch (Exception e) {
                    // URL 처리 중 오류 발생 시 기본 이미지 설정
                    setDefaultUserImage(button);
                }
            } else {
                // 이미지 URL이 없는 경우 기본 이미지 설정
                setDefaultUserImage(button);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 기본 이미지 설정
            setDefaultUserImage(button);
        }
    }
	
	
	private ImageIcon createCircularImageIcon(URL url, int width, int height) throws Exception {
		// URL에서 BufferedImage 로드
	    BufferedImage originalImage = ImageIO.read(url);

	    // 원본 이미지 크기 확인
	    int originalWidth = originalImage.getWidth();
	    int originalHeight = originalImage.getHeight();

	    // 정사각형으로 변환
	    int size = Math.min(originalWidth, originalHeight); // 최소 크기로 맞춤
	    BufferedImage squareImage = originalImage.getSubimage(
	        (originalWidth - size) / 2, 
	        (originalHeight - size) / 2, 
	        size, 
	        size
	    );

	    // 고품질로 크기 조정
	    BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = scaledImage.createGraphics();
	    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2d.drawImage(squareImage, 0, 0, width, height, null);
	    g2d.dispose();

	    // 원형으로 처리
	    BufferedImage circularImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = circularImage.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, width, height));
	    g2.drawImage(scaledImage, 0, 0, width, height, null);
	    g2.dispose();
	    
        return new ImageIcon(circularImage);
    }

	private void setDefaultUserImage(JButton button) {
	    try {
	        File imageFile = new File("src/icon/defaultUserImage.jpeg");
	        if (imageFile.exists()) {
	            URL defaultImageUrl = imageFile.toURI().toURL();
	            ImageIcon defaultIcon = createCircularImageIcon(defaultImageUrl, 55, 55);
	            button.setIcon(defaultIcon);
	        } else {
	            System.err.println("Default image file not found at: " + imageFile.getAbsolutePath());
	            button.setText("no image");
	            button.setBackground(Color.LIGHT_GRAY);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        button.setText("image error");
	        button.setBackground(Color.LIGHT_GRAY);
	    }
	}
    private boolean isValidImageUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            try (InputStream inputStream = url.openStream()) {
                BufferedImage image = ImageIO.read(inputStream);
                return image != null;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void saveProfile(User user, JTextField imgUrlField) {
        String userName = userNameField.getText().trim();
        String intro = introArea.getText().trim();
        String imgUrl = imgUrlField.getText().trim();  // Image URL from the text field

        if (userName.isEmpty() || intro.isEmpty() || imgUrl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.");
            return;
        }
     // 이미지 URL 검증 추가
        if (!isValidImageUrl(imgUrl)) {
            JOptionPane.showMessageDialog(this, "Invalid image URL. Please provide a valid image link.");
            return;
        }

        try {
            // 기존 프로필 업데이트 코드
            userModel.updateProfile(loggedInUserId, userName, intro, imgUrl);
            JOptionPane.showMessageDialog(this, "Profile updated successfully.");

            User updatedUser = userModel.getUserProfile(loggedInUserId);
            createProfilePanel(updatedUser);
            cardLayout.show(mainPanel, "Profile");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private static class NavigationState {
        String screenName;
        User user;

        NavigationState(String screenName, User user) {
            this.screenName = screenName;
            this.user = user;
        }
    }
    
    private void navigateBack() {
        if (!navigationStack.isEmpty()) {
            NavigationState previousState = navigationStack.pop();
            
            switch (previousState.screenName) {
                case "Profile":
                    createProfilePanel(previousState.user);
                    break;
                case "FollowingList":
                    viewFollowing();
                    break;
                case "FollowersList":
                    viewFollowers();
                    break;
                default:
                    createProfilePanel(userModel.getUserProfile(loggedInUserId));
            }
        }
    }
    
    private void viewFollowing() {
    	// 현재 프로필의 사용자 ID 저장 (다른 사용자 프로필일 수 있음)
        String currentProfileUserId = userModel.getCurrentProfileUserId(); // 이런 메서드를 UserModel에 추가해야 함

        // 현재 상태를 내비게이션 스택에 push
        navigationStack.push(new NavigationState("Profile", userModel.getUserProfile(currentProfileUserId)));

        List<User> followingList = userModel.getFollowingList(currentProfileUserId);

        JPanel followingPanel = new JPanel();
       followingPanel.setLayout(new BoxLayout(followingPanel, BoxLayout.Y_AXIS));
       followingPanel.setBackground(Color.WHITE);
       //followingPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬
       //followingPanel.setPreferredSize(new Dimension(400, followingList.size() * 80)); // 세로 크기 설정


        for (User user : followingList) {
            JButton userButton = new JButton(user.getUserName() + "  @" + user.getUserId());
            userButton.setBackground(Color.WHITE);
            userButton.setBorder(new LineBorder(new Color(106, 181, 249)));
            userButton.setMaximumSize(new Dimension(400, 60)); // 버튼 크기 설정
            userButton.addActionListener(e -> {
                // 현재 팔로잉 목록 화면을 스택에 push
                navigationStack.push(new NavigationState("FollowingList", null));
                showUserProfile(user);
            });
            followingPanel.add(userButton);
        }
//*** 세로로 스크롤 하고싶었는데 잘안되엇음..........
        JScrollPane scrollPane = new JScrollPane(followingPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> navigateBack());
        backButton.setBackground(new Color(106, 181, 249));
        backButton.setBorder(new LineBorder(new Color(106, 181, 249)));
        backButton.setForeground(Color.WHITE);
        backButton.setPreferredSize(new Dimension(400, 40));


        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(backButton, BorderLayout.NORTH);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(wrapperPanel, "FollowingList");
        cardLayout.show(mainPanel, "FollowingList");
    }


    private void viewFollowers() {
    	// 현재 프로필의 사용자 ID 저장 (다른 사용자 프로필일 수 있음)
        String currentProfileUserId = userModel.getCurrentProfileUserId(); // 이런 메서드를 UserModel에 추가해야 함

        // 현재 상태를 내비게이션 스택에 push
        navigationStack.push(new NavigationState("Profile", userModel.getUserProfile(currentProfileUserId)));

        List<User> followersList = userModel.getFollowersList(currentProfileUserId);

        JPanel followersPanel = new JPanel();
        followersPanel.setLayout(new BoxLayout(followersPanel, BoxLayout.Y_AXIS));
        followersPanel.setBackground(Color.WHITE);

        for (User user : followersList) {
            JButton userButton = new JButton(user.getUserName() + "  @" + user.getUserId());
            userButton.setBackground(Color.WHITE);
            userButton.setBorder(new LineBorder(new Color(106, 181, 249)));
            userButton.setMaximumSize(new Dimension(400, 60)); // 버튼 크기 설정
            userButton.addActionListener(e -> {
                // 현재 팔로잉 목록 화면을 스택에 push
                navigationStack.push(new NavigationState("FollowersList", null));
                showUserProfile(user);
            });
            followersPanel.add(userButton);
        }

        JScrollPane scrollPane = new JScrollPane(followersPanel);
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> navigateBack());
        backButton.setBackground(new Color(106, 181, 249));
        backButton.setBorder(new LineBorder(new Color(106, 181, 249)));
        backButton.setForeground(Color.WHITE);
        backButton.setPreferredSize(new Dimension(400, 40));

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(backButton, BorderLayout.NORTH);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(wrapperPanel, "FollowersList");
        cardLayout.show(mainPanel, "FollowersList");
    }


    private void viewOwnProfile() {
        // 로그인된 사용자의 프로필을 다시 표시하는 메서드
        createProfilePanel(userModel.getUserProfile(loggedInUserId));
    }
 
    
    private void showUserProfile(User user) {
       /* // 현재 화면을 스택에 push
        navigationStack.push(new NavigationState(
            navigationStack.isEmpty() ? "Profile" : navigationStack.peek().screenName, 
            null
        ));
        createProfilePanel(user);
        */
    	
    	userModel.setCurrentProfileUserId(user.getUserId());
        createProfilePanel(user);
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(profileGUI::new);
    }
}
