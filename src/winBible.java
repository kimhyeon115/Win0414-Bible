import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class winBible extends JDialog {
	
	private String[] abbr = {"창","출","레","민","신","수","삿","룻","삼상","삼하","왕상","왕하","대상","대하","스","느","에","욥","시","잠","전","아","사","렘","애","겔","단","호","욜","암","옵","욘","미","나","합","습","학","슥","말","마","막","눅","요","행","롬","고전","고후","갈","엡","빌","골","살전","살후","딤전","딤후","딛","몬","히","약","벧전","벧후","요일","요이","요삼","유","계"};
	private String[] full = {"창세기","출애굽기","레위기","민수기","신명기","여호수아","사사기","룻기","사무엘상","사무엘하","열왕기상","열왕기하","역대상","역대하","에스라","느헤미야","에스더","욥기","시편","잠언","전도서","아가","이사야","예레미야","예레미야애가","에스겔","다니엘","호세아","요엘","아모스","오바댜","요나","미가","나훔","하박국","스바냐","학개","스가랴","말라기","마태복음","마가복음","누가복음","요한복음","사도행전","로마서","고린도전서","고린도후서","갈라디아서","에베소서","빌립보서","골로새서","데살로니가전서","데살로니가후서","디모데전서","디모데후서","디도서","빌레몬서","히브리서","야고보서","베드로전서","베드로후서","요한일서","요한이서","요한삼서","유다서","요한계시록"};
	private JComboBox cbbook;
	private JComboBox cbchapter;
	private JComboBox cbverse;
	private JTextArea tacontents;
	private JTextField tfsearchword;
	private JTextField tffound;
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					winBible dialog = new winBible();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public winBible() throws IOException {
		setTitle("성경 프로젝트");
		setBounds(100, 100, 740, 444);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		
		JButton btnMerge = new JButton("파일 합치기");
		btnMerge.setEnabled(false);
		btnMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// c:/rlagus/BibleTxts.txt 폴더안의 텍스트파일들을 하나로(bible.txt)
				
				File file = new File("C:/rlagus/BibleTxts/");
				String flist[] = file.list();
				FileWriter fw;
				try {
					fw = new FileWriter("C:/rlagus/Bible.txt");
				
					for(int i=0; i<flist.length;i++) {
						try {						
							FileReader fr = new FileReader("C:/rlagus/BibleTxts/" + flist[i]);							
							int ch;
							while((ch=fr.read()) != -1) {
								fw.write((char)ch);												
							}							
							fr.close();	
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					fw.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		});
		panel.add(btnMerge);
		
		JButton btnFile2DB = new JButton("File2DB");
		btnFile2DB.setEnabled(false);
		btnFile2DB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 창1:1 태초에... => 창	1	1	태초에...
				// 파일(bible.txt)을 열어 한줄씩 읽어와 출력
				String book;
				int chapter;
				int verse;
				String contents;
				
				FileReader fr;
				int cnt=0;
				try {
					fr = new FileReader("C:/rlagus/0414.txt");
					BufferedReader br = new BufferedReader(fr);
					String temp;
					int idx=0;
					int cont=0;
					
					while((temp=br.readLine()) != null) {
						if(temp.charAt(1) >= '0' && temp.charAt(1) <= '9') {
							// 한줄 읽어온 문자열에서 index 1번째 글자가 숫자인지 체크
							idx = 1;
							book = temp.substring(0,idx);							
						}else {
							idx = 2;
							book = temp.substring(0,idx);
						}
						int cntt=0;
						for(int i=0; i<abbr.length ; i++) {
							if(abbr[i].equals(book)) {
								cntt = i;
								break;
							}
						}
						book = full[cntt];
						cont++;
						System.out.println("데이터 저장중 : " + cont);
						int colon=temp.indexOf(':');
						chapter = Integer.parseInt(temp.substring(idx,colon));
						
						int space=temp.indexOf(' ');
						verse = Integer.parseInt(temp.substring(colon+1,space));
						contents = temp.substring(space+1);
						
						Class.forName("com.mysql.cj.jdbc.Driver");
						Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sqlDB","root","1234");
						
						String sql = "INSERT INTO bibletbl(book,chapter,verse,contents) VALUES(?,?,?,?)";
						PreparedStatement pstmt = con.prepareStatement(sql);						
						pstmt.setString(1, book);
						pstmt.setInt(2, chapter);
						pstmt.setInt(3, verse);
						pstmt.setString(4, contents);
						
						pstmt.executeUpdate();						
						
						try {
							if(pstmt != null) {
								pstmt.close();
							}
							if(con != null) {
								con.close();
							}
						} catch(Exception e1) { 
							e1.printStackTrace();
						}						
					}						
					br.close();
					fr.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				System.out.println("저장완료");
			}
			
		});
		panel.add(btnFile2DB);		
		
		cbbook = new JComboBox();
		
		cbbook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String sbook = (String) cbbook.getSelectedItem();				
				try {					
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sqlDB","root","1234");
					Statement stmt = con.createStatement();
					
					String sql = "SELECT distinct chapter FROM bibletbl WHERE book='" + sbook + "'";
					ResultSet rs = stmt.executeQuery(sql);
					
					while(rs.next()) {
						cbchapter.addItem(rs.getString("chapter"));
					}
				} catch(ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();					
				}
			}
		});
		panel.add(cbbook);	
		
		cbchapter = new JComboBox();
		cbchapter.addActionListener(new ActionListener() { // 성경책, 장을 선택하면 선택한 장의 1절부터 N절까지가 콤보박스에 추가된다.
			public void actionPerformed(ActionEvent e) {
				String sBook = (String) cbbook.getSelectedItem();
				String sChapter = (String) cbchapter.getSelectedItem();
				
				cbverse.removeAllItems();
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sqlDB","root","1234");						
					Statement stmt = con.createStatement();
					
					String sql = "select verse from bibleTBL where book='" + sBook + "' and chapter=" + sChapter ;
					ResultSet rs = stmt.executeQuery(sql);
					while(rs.next()) {
						cbverse.addItem(rs.getString("verse"));
					}
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}				
			}
		});
		panel.add(cbchapter);
		
		cbverse = new JComboBox();
		panel.add(cbverse);
		
		JButton btnShow = new JButton("읽기");
		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String sBook = (String) cbbook.getSelectedItem();
				String sChapter = (String) cbchapter.getSelectedItem();
				String sVerse = (String) cbverse.getSelectedItem();
				tacontents.setText("");
				
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sqlDB","root","1234");						
					Statement stmt = con.createStatement();
					
					String sql = "select verse, contents from bibleTBL where book='" + sBook + "' and chapter=" + sChapter;
//					sql = sql + " and verse=" + sVerse;
							
					ResultSet rs = stmt.executeQuery(sql);
					String temp ="";
					while(rs.next()) {
						temp = temp + "\n" + rs.getString("verse") + "   " + rs.getString("contents") + "\n";
					}
					tacontents.setText(temp);
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}				
			}
		});
		panel.add(btnShow);
		
		tfsearchword = new JTextField();
		tfsearchword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					foundsearch();
						
					
				}
			}

			
		});
		panel.add(tfsearchword);
		tfsearchword.setColumns(10);
		
		JButton btnfound = new JButton("찾기");
		btnfound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				foundsearch();
			}
		});
		panel.add(btnfound);
		
		tffound = new JTextField();
		tffound.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String text = tffound.getText();
					int idxspace = text.indexOf(' ');
					int idxcolon = text.indexOf(':');
					String nbook = text.substring(0, idxspace).trim();
					String nchapter = text.substring(idxspace+1, idxcolon).trim();
					String nverse = text.substring(idxcolon+1).trim(); // trim(); 공백은 배제함
					
					nbook = abbr2full(nbook);
					try {
						foundsearch2(nbook,nchapter,nverse);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		panel.add(tffound);
		tffound.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		tacontents = new JTextArea();
		tacontents.setFont(new Font("나눔고딕", Font.BOLD, 20));
		tacontents.setLineWrap(true);
		scrollPane.setViewportView(tacontents);
		
		// cbBook에 66권의 책이름을 추가하시오.(full 배열)
		//for(int i=0;i<full.length;i++)
		//	cbBook.addItem(full[i]);
		
		// cbBook에 66권의 책이름을 추가하시오.(DB-bibleTBL)
		try {
			addBooks();
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}
	}

	protected void foundsearch2(String nbook, String nchapter, String nverse) throws ClassNotFoundException, SQLException {
		tacontents.setText("");				
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sqlDB","root","1234");						
			Statement stmt = con.createStatement();
			
			String sql = "SELECT * from bibleTBL WHERE book=? , chapter=?, verse=?";
			ResultSet rs = stmt.executeQuery(sql);
			String temp="";
			if(rs.next()) {
				while(rs.next()) {
					temp = temp + "[" + rs.getString("book") + " " + rs.getString("chapter") + "장";
					temp = temp + " " + rs.getString("verse") + "절] " + rs.getString("contents");
				}
				tacontents.setText(temp);
			}else {
				tacontents.setText("검색하신 단어가 포함된 내용이 없습니다.");
			}
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}
	}

	protected String abbr2full(String nbook) {
		for(int i=0; i<abbr.length; i++) {
			if(nbook.equals(abbr[i])) {
				nbook = full[i];
				break;
			}
		}
		return nbook;
	}

	private void addBooks() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sqlDB","root","1234");						
		Statement stmt = con.createStatement();
		
		String sql = "select distinct book from bibleTBL";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			cbbook.addItem(rs.getString("book"));
		}
	}
	private void foundsearch() {
		String search = tfsearchword.getText();
		tacontents.setText("");				
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sqlDB","root","1234");						
			Statement stmt = con.createStatement();
			
			String sql = "SELECT * from bibleTBL WHERE contents LIKE '%" + search + "%'";
			ResultSet rs = stmt.executeQuery(sql);
			String temp="";
			int count=0;
			if(rs.next()) {
				while(rs.next()) {
					temp = temp + "[" + rs.getString("book") + " " + rs.getString("chapter") + "장";
					temp = temp + " " + rs.getString("verse") + "절] " + rs.getString("contents") + "\n\n";
					count++;
				}
				tacontents.setText(temp);
				setTitle(count + "회");
			}else {
				tacontents.setText("검색하신 단어가 포함된 내용이 없습니다.");
			}
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}				
	}

}