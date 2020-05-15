package project50Java;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Calendar;
import java.util.List;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.*;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.*;
import org.jfree.ui.RectangleEdge;
import com.fazecast.jSerialComm.*;


//eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
public class Project50Java {
	public static void main(String[] args) throws IOException {		
		FrameChart frameChart=new FrameChart();
		frameChart.setVisible(true);
	}
}
//eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee

//---------------------------------------------------------------------------
class FrameChart extends JFrame{
	private static final long serialVersionUID = 1L;

	public FrameChart() throws IOException {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);		
		setBounds(20, 20, 800, 520);
		setTitle("Carmelo2Elecronics");
		setResizable(false);
		addWindowListener(new WindowAdapter() {							
	        public void windowClosing(WindowEvent event) {
	    		int opcion=JOptionPane.showConfirmDialog(null, "Do you want to close the program?", "CONFIRMATION", JOptionPane.OK_CANCEL_OPTION);
	    		if(opcion==0) {
	    			System.exit(0);	    			
	    		}
	        }
	    });	
		PanelChart panelChart=new PanelChart();
		add(panelChart);
	}
}
//---------------------------------------------------------------------------

//class PanelChart*******************************************************************************************
class PanelChart extends JPanel implements ChartMouseListener, ActionListener{

	private static final long serialVersionUID = 1L;
	private JFreeChart jFreeChart;
	private ChartPanel chartPanel;
	private XYSeriesCollection xySeriesCollection;	
	private XYSeries xySerie;
	private XYPlot xyPlot;	
	private CrosshairOverlay crosshairOverlay;
    private Crosshair xCrosshair;
    private Crosshair yCrosshair;
    private ValueAxis xAxis;
    private double x=0, y=0;
    private double [][] matrixDatos;
	private JMenuItem SaveData=new JMenuItem("Save Data");
	private JMenuItem MaxValue=new JMenuItem("Max. Value");
	private JMenuItem MinValue=new JMenuItem("Min. Value");	
	private List <String> time = new ArrayList <String> ();
	private JLabel JLabelValor=new JLabel("Current Value: " + "---------");
	private boolean flangPlot=true;
	private MakeFile makeFile=new MakeFile();
	
	public PanelChart() throws IOException {		
		new MakeFolder();

		xySeriesCollection = new XYSeriesCollection();		
		xySerie= new XYSeries( "samples" );
		xySeriesCollection.addSeries(xySerie);	
		jFreeChart = ChartFactory.createXYLineChart("Chart", "Time", "Samples", xySeriesCollection);
		xyPlot = jFreeChart.getXYPlot();
		xyPlot.getDomainAxis();
		xyPlot.getRangeAxis();	
		chartPanel=new ChartPanel(jFreeChart);	
		this.add(chartPanel);
		
		add(JLabelValor);

        crosshairOverlay = new CrosshairOverlay();
        xCrosshair = new Crosshair(Double.NaN, Color.GREEN, new BasicStroke(1f));
        xCrosshair.setLabelVisible(true);
        yCrosshair = new Crosshair(Double.NaN, Color.GREEN, new BasicStroke(1f));
        yCrosshair.setLabelVisible(true);
        crosshairOverlay.addDomainCrosshair(xCrosshair);
        crosshairOverlay.addRangeCrosshair(yCrosshair);
        chartPanel.addOverlay(crosshairOverlay);		
        chartPanel.addChartMouseListener(this);
		
		SerialPort serialPort=new LookingPortsConfigure().getSelectedPort();
		new ReceiveString(serialPort, xySerie, JLabelValor, time);
		
		//added items
		chartPanel.getPopupMenu().addSeparator();	
		chartPanel.getPopupMenu().add(MaxValue);
		chartPanel.getPopupMenu().add(MinValue);
		chartPanel.getPopupMenu().addSeparator();
		chartPanel.getPopupMenu().add(SaveData);
		
		SaveData.addActionListener(this);
		MaxValue.addActionListener(this);
		MinValue.addActionListener(this);
	}
	public void chartMouseClicked(ChartMouseEvent e) {
		flangPlot=true;							//start cursors
	}	
	public void chartMouseMoved(ChartMouseEvent event) {
		if(flangPlot==true) {
			Rectangle2D dataArea = chartPanel.getScreenDataArea();
			jFreeChart = event.getChart();
			xyPlot = (XYPlot) jFreeChart.getPlot();
			xAxis = xyPlot.getDomainAxis();
			x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, RectangleEdge.BOTTOM);
			y = DatasetUtilities.findYValue(xyPlot.getDataset(), 0, x);       
			xCrosshair.setValue(x);
			yCrosshair.setValue(y);	
		}
	}
	public void actionPerformed(ActionEvent e) {		
		String StringItem = null;		
		try {			
			JMenuItem source = (JMenuItem)(e.getSource());
			StringItem=source.getText();			
			flangPlot=false;						//stop cursors		
			if(StringItem.equals("Save Data")){
							
				matrixDatos=xySerie.toArray();				
				int itemCount=xySerie.getItemCount();				
				Object[] objTime=time.toArray();
				makeFile.saveItems(itemCount, matrixDatos, objTime);
			}						
			if(StringItem.equals("Max. Value")){				
				double yMaxV=0;
				matrixDatos =new double[xySerie.getItemCount()][2];
				int s;			
				
				matrixDatos=xySerie.toArray();			
				yMaxV=xySerie.getMaxY();			

				for(s=0;s<xySerie.getItemCount();s++){
					if(matrixDatos[1][s]==xySerie.getMaxY()){
						break;
					}
				}			
				xCrosshair.setValue(s);			
				yCrosshair.setValue(yMaxV);			
			}
			if(StringItem.equals("Min. Value")){				
				double yMinV=0;
				matrixDatos =new double[xySerie.getItemCount()][2];
				int s;			
				
				matrixDatos=xySerie.toArray();			
				yMinV=xySerie.getMinY();			

				for(s=0;s<xySerie.getItemCount();s++){
					if(matrixDatos[1][s]==xySerie.getMinY()){
						break;
					}
				}			
				xCrosshair.setValue(s);			
				yCrosshair.setValue(yMinV);	
			}			
		}catch(Exception q){	
			
		}
	}
}
//class PanelChart*******************************************************************************************

//uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu
class MakeFile extends JFrame{

	private static final long serialVersionUID = 1L;
	private String nameFile;
	private BufferedWriter DataBufferedWriter;
	private FileWriter DatafileWriter;
	private DecimalFormat df = new DecimalFormat("000000.000");
	private File FileSaveItems;

	public MakeFile() throws IOException {		
		super();
	}	
	public void saveItems(int itemCount,double [][] matrixDatos, Object[] objTime) throws IOException {		
		nameFile=JOptionPane.showInputDialog(null, null, "File Name", JOptionPane.CLOSED_OPTION);		
		FileSaveItems=new File("C:/DataSensor/" + nameFile + ".txt");
		DatafileWriter=new FileWriter(FileSaveItems,false);
		DataBufferedWriter=new BufferedWriter(DatafileWriter);		
		String item;
		for(int i=0; i< itemCount; i++) {				
			item= objTime[i].toString()+ "\t" + df.format(matrixDatos[0][i]) + "\t" + df.format(matrixDatos[1][i]);	
			DataBufferedWriter.write(item) ;
			DataBufferedWriter.newLine();			
		}				
		DataBufferedWriter.close();	
		DatafileWriter.close();	
	}
}
//uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu

//*******************************************************************************************
class MakeFolder{
	public MakeFolder() throws IOException {
		File directorio = new File("C:/DataSensor");
		if(!directorio.exists()) {
			directorio.mkdirs();
			JOptionPane.showMessageDialog(null, "A folder has been made in: C:/DataSensor", "INFORMATION MESSAGE",JOptionPane.INFORMATION_MESSAGE);
		}else {
			JOptionPane.showMessageDialog(null, "There is a folder in: C:/DataSensor", "INFORMATION MESSAGE",JOptionPane.INFORMATION_MESSAGE);
		}	
	}
}
//*******************************************************************************************

//wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
class Time{
	 
private Calendar now;
private String formatTime="%02d:%02d:%02d.%03d";
private String formatDate="%02d/%02d/%04d";
private String StringTime;
private String StringDate;

	public Time() {				
		now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		int second = now.get(Calendar.SECOND);
		int msecond = now.get(Calendar.MILLISECOND);
		int month=now.get(Calendar.MONTH)+1;
		int day= now.get(Calendar.DAY_OF_MONTH);
		int year=now.get(Calendar.YEAR);
		StringTime=String.format(formatTime, hour, minute, second, msecond );
		StringDate=String.format(formatDate, month, day, year);		
	}	
	public String getTime() {
		return StringTime;
	}
	public String getDate(){
		return StringDate;
	}
}
//wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
class LookingPortsConfigure{
	
	private SerialPort[] serialPortArray;	
	private String[] dataPort;	
	private Object selectedPortObject;	
	private SerialPort serialPort;
	private String selection;

	public LookingPortsConfigure() {	
		serialPortArray= SerialPort.getCommPorts();			
		dataPort = new String[serialPortArray.length];				
		if(serialPortArray.length==0) {
			JOptionPane.showMessageDialog(null, "No busy comm port", "ERROR MESSAGE", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		for (int i = 0; i < serialPortArray.length; ++i) {
			dataPort[i]=i + "  "+
			serialPortArray[i].getSystemPortName()+ "  " +
			serialPortArray[i].getDescriptivePortName()+ "  ";
		}
		selectedPortObject = JOptionPane.showInputDialog(null,"Choose port", "PORTS", JOptionPane.QUESTION_MESSAGE, null, dataPort,"Seleccione");				
		if(selectedPortObject==null){
			JOptionPane.showMessageDialog(null, "You did not select port", "ERROR MESSAGE", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}else {
			selection=selectedPortObject.toString().substring(0,1);			
			serialPort=SerialPort.getCommPort(serialPortArray[Integer.parseInt(selection)].getSystemPortName());		
			serialPort.setComPortParameters(9600, 8, 1, 0);		//port configuration
			serialPort.openPort();	
		}
	}	
	public SerialPort getSelectedPort() {
		return serialPort;
	}
}
//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh

//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
class ReceiveString implements SerialPortPacketListener{

	private StringBuilder stringBuilder;
	private String stringReceived;	
	private XYSeries xySerie;
	private JLabel JLabelValor;
	private List <String> time = new ArrayList <String> ();
	private int a=0;
	
	public ReceiveString(SerialPort serialPort, XYSeries xySerie, JLabel JLabelValor, List <String> time) {		
		this.xySerie= xySerie;
		this.JLabelValor=JLabelValor;
		this.time=time;
		serialPort.addDataListener(this);
	}	
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
	}		
	public void serialEvent(SerialPortEvent event) {
		stringBuilder = new StringBuilder();
		byte[] newData = event.getReceivedData();
		for (int i = 0; i < newData.length; ++i) {
			if((char)newData[i]!='\n') {
				stringBuilder.append((char)newData[i]);
			}else {
				break;
			}
		}	
		stringReceived=stringBuilder.toString();
		stringReceived.trim();
		JLabelValor.setText("Current Value: " + stringReceived);			
		try {
			xySerie.add(a++,Double.parseDouble(stringReceived));
			time.add(new Time().getTime());
		}catch(Exception e) {
			e.getStackTrace();
		}
		stringBuilder=null;
	}	
	public int getPacketSize() {
		return 10;		//Number of characters you receive from Arduino. Use a fixed number of characters always.
	}						
}
//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh


