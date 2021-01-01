package dolphinmovie.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import dolphinmovie.server.log.LogManager;
import dolphinmovie.server.navermovie.NaverMovie;
import dolphinmovie.server.theater.TheaterDAO;
import dolphinmovie.server.theater.TheaterManager;
import dolphinmovie.server.user.UserDAO;
import dolphinmovie.server.user.UserManager;

public class DMServerMainUI extends JFrame {
	
	private JTable boxofficeListTable = new JTable();
	private JTable theaterListTable = new JTable();
	private JTextField userSearchString = new JTextField(30);
	private NaverMovie naverMovie = NaverMovie.getInstance();
	private JButton userSearchBtn = new JButton("Find");
	private JLabel concurrentUserLabel = new JLabel("0");
	
	UserManager user = UserManager.getInstance();
	
	public DMServerMainUI() {
		super("D_Movie_Server");
		
		String[] dailyList = naverMovie.getDailyMoviesName();
		String[] weeklyList = naverMovie.getWeeklyMoviesName();
		int listLength = Math.max(dailyList.length, weeklyList.length);
		Object[][] rowsForBoxoffice = new Object[listLength][3];
		Container pane = this.getContentPane();
		pane.setBackground(Color.LIGHT_GRAY);
		
		//중앙에 박스오피스리스트 부분 표시
		JScrollPane jspForBoxoffice = new JScrollPane(boxofficeListTable);
		
		for(int i=0;i<listLength;i++) {
			rowsForBoxoffice[i][0] = i+1;
			
			if(dailyList.length > i)
				rowsForBoxoffice[i][1] = dailyList[i];
			else 
				rowsForBoxoffice[i][1] = "-";
			if(weeklyList.length > i)
				rowsForBoxoffice[i][2] = weeklyList[i];
			else 
				rowsForBoxoffice[i][2] = "-";
		}
		String[] columnsForBoxoffice = {" ", "Daily", "Weekly"};
		
		boxofficeListTable.setModel(new DefaultTableModel(rowsForBoxoffice,columnsForBoxoffice));
		boxofficeListTable.setGridColor(Color.BLACK);
		
		DefaultTableCellRenderer cellAlignCenter = new DefaultTableCellRenderer();
		cellAlignCenter.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer cellAlignLeft = new DefaultTableCellRenderer();
		cellAlignLeft.setHorizontalAlignment(SwingConstants.LEFT);
		
		boxofficeListTable.getColumn(" ").setPreferredWidth(10);
		boxofficeListTable.getColumn(" ").setCellRenderer(cellAlignCenter);
		boxofficeListTable.getColumn("Daily").setPreferredWidth(210);
		boxofficeListTable.getColumn("Daily").setCellRenderer(cellAlignLeft);
		boxofficeListTable.getColumn("Weekly").setPreferredWidth(210);
		boxofficeListTable.getColumn("Weekly").setCellRenderer(cellAlignLeft);
		boxofficeListTable.setColumnSelectionAllowed(false);
		
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout());
		centerPanel.add(jspForBoxoffice);
		
		pane.add("Center",centerPanel);
		
		//
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout(2,1));
		
		JPanel northPanelTop = new JPanel();
		northPanelTop.setLayout(new FlowLayout(FlowLayout.LEFT));
		northPanelTop.add(new JLabel("DolphinMovie"));
		northPanelTop.add("North",userSearchString);
		northPanelTop.add(userSearchBtn);
		userSearchBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String query = userSearchString.getText();
				if(!query.equals("")) {
					UserDAO result = user.findUser(query);
					if(result == null) {
						JOptionPane.showMessageDialog(null,"Not Logined");
					} else {
						JOptionPane.showMessageDialog(null, "ID: "+result.getId() +", name:" + result.getName() +" Login");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Enter ID");
				}
				
				userSearchString.setText("");
			}
			
		});
		northPanelTop.add(new JLabel("User Count : "));
		northPanelTop.add(concurrentUserLabel);
		//concurrentUserLabel.addPropertyChangeListener(new );
		northPanel.add("North",northPanelTop);
		
		JPanel northPanelBottom = new JPanel();
		JPanel northPanelBottomFirst = new JPanel();
		northPanelBottomFirst.setLayout(new GridLayout(1,3,5,5));
		
		
		JButton boxofficeListUpdateBtn = getBoxofficeListUpdateButton();
		JButton logClearBtn = getLogClearButton();
		JButton theaterListUpdateBtn = getTheaterListUpdateButton();
		
		northPanelBottomFirst.add(boxofficeListUpdateBtn);
		northPanelBottomFirst.add(logClearBtn);
		northPanelBottomFirst.add(theaterListUpdateBtn);
		
		northPanelBottom.add(northPanelBottomFirst);
		
		JPanel northPanelBottomSecond = new JPanel();
		northPanelBottomSecond.setLayout(new GridLayout());
		
		northPanelBottomSecond.add(new JButton("Test"));
		
		northPanelBottom.add(northPanelBottomSecond);
		
		northPanel.add("Center",northPanelBottom);
		
		pane.add("North",northPanel);
		
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new GridLayout());
		
		JScrollPane jspForTheater = new JScrollPane(theaterListTable);
		LinkedList<TheaterDAO> theaterList = TheaterManager.getInstance().getTheaterList();
		String[] colForTheater = {"index", "name" , "address"};
		Object[][] rowsForTheater = new Object[theaterList.size()][3];
		int i=0;
		
		for(TheaterDAO t: theaterList) {
			rowsForTheater[i][0] = i+1;
			rowsForTheater[i][1] = t.getName();
			rowsForTheater[i++][2] = t.getAddress();
		}
		
		theaterListTable.setModel(new DefaultTableModel(rowsForTheater,colForTheater));
		theaterListTable.setGridColor(Color.BLACK);
		
		theaterListTable.getColumn("index").setPreferredWidth(20);
		theaterListTable.getColumn("index").setCellRenderer(cellAlignCenter);
		theaterListTable.getColumn("name").setPreferredWidth(300);
		theaterListTable.getColumn("name").setCellRenderer(cellAlignLeft);
		theaterListTable.getColumn("address").setPreferredWidth(300);
		theaterListTable.getColumn("address").setCellRenderer(cellAlignLeft);
		theaterListTable.setColumnSelectionAllowed(false);
		
		eastPanel.add(jspForTheater);
		
		pane.add("East", eastPanel);
	}
	
	private JButton getTheaterListUpdateButton() {
		JButton btn = new JButton("Update Theaters");
		
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<LinkedList<TheaterDAO>,TheaterDAO> worker = new TheaterUpdateWorker();
				worker.execute();
			}
			
		});
		
		return btn;
	}
	
	private JButton getLogClearButton() {
		JButton btn = new JButton("Clear Log Files");
		
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LogManager.deleteLogFiles();
				JOptionPane.showMessageDialog(null, "Log File Clear!");
			}
			
		});
		
		return btn;
	}
	
	private JButton getBoxofficeListUpdateButton() {
		JButton btn = new JButton("Update Boxoffice");
		
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String[] daily = naverMovie.getDailyMoviesName();
				String[] weekly = naverMovie.getWeeklyMoviesName();
				int listLength = Math.max(daily.length, weekly.length);
				Object[][] rows = new Object[listLength][3];
				for(int i=0;i<listLength;i++) {
					rows[i][0] = i+1;
					
					if(daily.length > i)
						rows[i][1] = daily[i];
					else 
						rows[i][1] = "-";
					
					if(weekly.length > i)
						rows[i][2] = weekly[i];
					else 
						rows[i][2] = "-";
				}
				
				String[] columns = {" ", "Daily", "Weekly"};
				
				boxofficeListTable.setModel(new DefaultTableModel(rows,columns));
				boxofficeListTable.setGridColor(Color.BLACK);
				
				DefaultTableCellRenderer cellAlignCenter = new DefaultTableCellRenderer();
				cellAlignCenter.setHorizontalAlignment(SwingConstants.CENTER);
				DefaultTableCellRenderer cellAlignLeft = new DefaultTableCellRenderer();
				cellAlignLeft.setHorizontalAlignment(SwingConstants.LEFT);
				
				boxofficeListTable.getColumn(" ").setPreferredWidth(10);
				boxofficeListTable.getColumn(" ").setCellRenderer(cellAlignCenter);
				boxofficeListTable.getColumn("Daily").setPreferredWidth(210);
				boxofficeListTable.getColumn("Daily").setCellRenderer(cellAlignLeft);
				boxofficeListTable.getColumn("Weekly").setPreferredWidth(210);
				boxofficeListTable.getColumn("Weekly").setCellRenderer(cellAlignLeft);
				boxofficeListTable.setColumnSelectionAllowed(false);
			}
			
		});
		
		return btn;
	}
	
	private static class TheaterUpdateWorker extends SwingWorker<LinkedList<TheaterDAO>, TheaterDAO> {

		@Override
		protected LinkedList<TheaterDAO> doInBackground() throws Exception {
			TheaterManager tm = TheaterManager.getInstance();
			tm.updateTheaterTable();
			
			return tm.getTheaterList();
		}
		
		/*
		@Override 
		protected void process(List<TheaterDAO> chunks) {
			
		}
		*/
		
		@Override 
		protected void done() {
			JOptionPane.showMessageDialog(null, "Theaters Updated!");
		}
		
	}
}
